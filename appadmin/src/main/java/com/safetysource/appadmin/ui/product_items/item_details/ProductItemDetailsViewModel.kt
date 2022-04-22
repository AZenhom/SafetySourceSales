package com.safetysource.appadmin.ui.product_items.item_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.*
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class ProductItemDetailsViewModel @Inject constructor(
    private val productItemRepository: ProductItemRepository,
    private val transactionsRepository: TransactionsRepository,
    private val retailerRepository: RetailerRepository,
    private val teamRepository: TeamRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val productModel: ProductModel? =
        savedStateHandle[ProductItemDetailsActivity.PRODUCT_MODEL]
    val productItemModel: ProductItemModel? =
        savedStateHandle[ProductItemDetailsActivity.PRODUCT_ITEM_MODEL]

    val transactionFilterModel =
        TransactionFilterModel(serial = productItemModel?.serial, dateFrom = null, dateTo = null)

    fun getTransactions(): LiveData<List<TransactionModel>> {
        showLoading()
        val liveData = LiveEvent<List<TransactionModel>>()
        safeLauncher {
            val result =
                transactionsRepository.getTransactions(transactionFilterModel)
            if (result is StatefulResult.Success) {
                val transactions = result.data ?: listOf()

                // Retailers
                val retailers = transactions
                    .map { it.retailerId }.distinct()
                    .map { async { retailerRepository.getRetailerById(it ?: "") } }
                    .map { it.await() }.filterIsInstance<StatefulResult.Success<RetailerModel>>()
                    .map { it.data }
                transactions.forEach { transactionModel ->
                    transactionModel.retailerModel =
                        retailers.firstOrNull { transactionModel.retailerId == it?.id }
                }

                // Teams
                val teams = transactions
                    .map { it.teamId }.distinct()
                    .map { async { teamRepository.getTeamById(it ?: "") } }
                    .map { it.await() }.filterIsInstance<StatefulResult.Success<TeamModel>>()
                    .map { it.data }
                transactions.forEach { transactionModel ->
                    transactionModel.teamModel =
                        teams.firstOrNull { transactionModel.teamId == it?.id }
                }

                // Products
                transactions.forEach { transactionModel ->
                    transactionModel.productModel = productModel
                }

                liveData.value = transactions
            } else
                handleError(result.errorModel)
            hideLoading()
        }
        return liveData
    }

    fun approveUnsellTransaction(transactionModel: TransactionModel): LiveData<Boolean> {
        showLoading()
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            transactionModel.isUnsellingApproved = true
            transactionModel.unsellingApprovedByAdminId = userRepository.getUserId()
            transactionModel.updatedAt = null

            val transactionResult = transactionsRepository.createUpdateTransaction(transactionModel)
            if (transactionResult is StatefulResult.Success) {
                val productItemModel =
                    productItemRepository.getProductItemBySerial(transactionModel.serial ?: "").data
                if (productItemModel != null) {
                    productItemModel.state = ProductItemState.NOT_SOLD_YET
                    productItemModel.updatedAt = null

                    val productItemResult =
                        productItemRepository.createUpdateProductItem(productItemModel)

                    if (productItemResult is StatefulResult.Success) {
                        productItemModel.state = ProductItemState.NOT_SOLD_YET
                        liveData.value = true
                    } else
                        handleError(productItemResult.errorModel)
                }
            } else
                handleError(transactionResult.errorModel)

            hideLoading()
        }
        return liveData
    }
}