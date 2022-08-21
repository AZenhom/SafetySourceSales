package com.safetysource.retailer.ui.product_item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.R
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.*
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductItemDetailsViewModel @Inject constructor(
    private val productItemRepository: ProductItemRepository,
    private val productRepository: ProductRepository,
    private val transactionsRepository: TransactionsRepository,
    private val userRepository: UserRepository,
    private val retailerRepository: RetailerRepository,
    private val reportsRepository: ReportsRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val productItemModel: ProductItemModel? =
        savedStateHandle[ProductItemDetailsActivity.PRODUCT_ITEM_MODEL]
    private val _allowedAndCanUnsellLiveData = MutableLiveData<Pair<Boolean, Boolean>>()
    val allowedAndCanUnsellLiveData: LiveData<Pair<Boolean, Boolean>> get() = _allowedAndCanUnsellLiveData

    init {
        safeLauncher {
            val allowedProducts = retailerRepository.getCurrentRetailerModel()?.allowedProductIds
            val isProductAllowed =
                allowedProducts.isNullOrEmpty() || allowedProducts.find { it == productItemModel?.productId } != null
            val isEligibleForUnsell =
                productItemModel?.lastSoldByRetailerId != null
                        && productItemModel.lastSoldByRetailerId == userRepository.getUserId()
            _allowedAndCanUnsellLiveData.value = Pair(isProductAllowed, isEligibleForUnsell)
        }
    }

    fun getProduct(): LiveData<ProductModel?> {
        showLoading()
        val liveData = LiveEvent<ProductModel?>()
        safeLauncher {
            val result =
                productRepository.getProductById(productItemModel?.productId ?: "")
            hideLoading()
            if (result is StatefulResult.Success)
                liveData.value = result.data
            else
                handleError(result.errorModel)
        }
        return liveData
    }

    fun sellUnsellProductItem(
        productModel: ProductModel,
        sellOrUnsell: Boolean
    ): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            showLoading()

            val teamReport =
                reportsRepository.getTeamReportById(
                    retailerRepository.getCurrentRetailerModel()?.teamId ?: ""
                ).data
            val retailerReport =
                reportsRepository.getRetailerReportById(
                    userRepository.getUserId() ?: ""
                ).data

            if (teamReport == null || retailerReport == null || productItemModel == null) {
                showErrorMsg(R.string.something_went_wrong)
                hideLoading()
                return@safeLauncher
            }


            productItemModel.state =
                if (sellOrUnsell) ProductItemState.SOLD else ProductItemState.PENDING_UNSELLING
            productItemModel.lastSoldByRetailerId =
                if (sellOrUnsell) userRepository.getUserId() else null
            productItemModel.updatedAt = null


            val transactionModel = TransactionModel(
                id = transactionsRepository.getNewTransactionId(),
                type = if (sellOrUnsell) TransactionType.SELLING else TransactionType.UNSELLING,
                retailerId = userRepository.getUserId(),
                teamId = retailerRepository.getCurrentRetailerModel()?.teamId,
                productId = productModel.id,
                categoryId = productModel.productCategoryId,
                serial = productItemModel.serial,
                commissionAppliedOrRemoved = productModel.commissionValue,
                isUnsellingApproved = false
            )

            val commission: Float =
                if (sellOrUnsell) (productModel.commissionValue ?: 0f)
                else -(productModel.commissionValue ?: 0f)

            teamReport.dueCommissionValue =
                (teamReport.dueCommissionValue ?: 0f) + commission
            teamReport.updatedAt = null


            retailerReport.dueCommissionValue =
                (retailerReport.dueCommissionValue ?: 0f) + commission
            retailerReport.updatedAt = null

            productItemRepository.sellUnsellProductItem(
                productItemModel,
                transactionModel,
                retailerReport,
                teamReport
            )
            liveData.value = true
            hideLoading()
        }
        return liveData
    }
}