package com.safetysource.appretailer.ui.product_item

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.hadilq.liveevent.LiveEvent
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

    fun getProduct(): LiveData<ProductModel?> {
        showLoading()
        val liveData = LiveEvent<ProductModel?>()
        safeLauncher {
            val result =
                productRepository.getProductById(productItemModel?.productId ?: "")
            if (result is StatefulResult.Success)
                liveData.value = result.data
            else
                handleError(result.errorModel)
            hideLoading()
        }
        return liveData
    }

    fun sellUnsellProductItem(
        productModel: ProductModel,
        sellOrUnsell: Boolean
    ): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            if (productItemModel == null)
                return@safeLauncher
            showLoading()

            productItemModel.state =
                if (sellOrUnsell) ProductItemState.SOLD else ProductItemState.PENDING_UNSELLING
            productItemModel.updatedAt = null

            val productItemResponse =
                productItemRepository.createUpdateProductItem(productItemModel)

            if (productItemResponse is StatefulResult.Success) {
                val transactionModel = TransactionModel(
                    id = transactionsRepository.getNewTransactionId(),
                    type = if (sellOrUnsell) TransactionType.SELLING else TransactionType.UNSELLING,
                    retailerId = userRepository.getUserId(),
                    teamId = retailerRepository.getCurrentRetailerModel()?.teamId,
                    productId = productModel.id,
                    serial = productItemModel.serial,
                    commissionAppliedOrRemoved = productModel.commissionValue,
                    isUnsellingApproved = false
                )
                val transactionResponse =
                    transactionsRepository.createUpdateTransaction(transactionModel)

                if (transactionResponse is StatefulResult.Success) {
                    val teamReport =
                        reportsRepository.getTeamReportById(transactionModel.teamId ?: "").data
                    val retailerReport =
                        reportsRepository.getRetailerReportById(
                            transactionModel.retailerId ?: ""
                        ).data

                    val commission: Float =
                        if (sellOrUnsell) (productModel.commissionValue ?: 0f)
                        else -(productModel.commissionValue ?: 0f)

                    if (teamReport != null) {
                        teamReport.dueCommissionValue =
                            (teamReport.dueCommissionValue ?: 0f) + commission
                        teamReport.updatedAt = null

                        reportsRepository.createUpdateTeamReport(teamReport)
                    }

                    if (retailerReport != null) {
                        retailerReport.dueCommissionValue =
                            (retailerReport.dueCommissionValue ?: 0f) + commission
                        retailerReport.updatedAt = null

                        reportsRepository.createUpdateRetailerReport(retailerReport)
                    }

                    liveData.value = true
                } else
                    handleError(transactionResponse.errorModel)
            } else
                handleError(productItemResponse.errorModel)

            hideLoading()
        }
        return liveData
    }
}