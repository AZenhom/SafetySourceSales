package com.safetysource.admin.ui.transactions.transactions_list

import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.*
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val userRepository: UserRepository,
    private val productItemRepository: ProductItemRepository,
    private val retailerRepository: RetailerRepository,
    private val teamRepository: TeamRepository,
    private val productRepository: ProductRepository,
) : BaseViewModel() {

    var transactionFilterModel: TransactionFilterModel = TransactionFilterModel()

    fun getTransactions(): LiveData<List<TransactionModel>> {
        showLoading()
        val liveData = LiveEvent<List<TransactionModel>>()
        safeLauncher {
            val result = transactionsRepository.getTransactions(transactionFilterModel)
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
                val products = transactions
                    .map { it.productId }.distinct()
                    .map { async { productRepository.getProductById(it ?: "") } }
                    .map { it.await() }.filterIsInstance<StatefulResult.Success<ProductModel>>()
                    .map { it.data }
                transactions.forEach { transactionModel ->
                    transactionModel.productModel =
                        products.firstOrNull { transactionModel.productId == it?.id }
                }

                liveData.value = transactions
                hideLoading()
            } else
                handleError(result.errorModel)
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
                    productItemModel.lastSoldByRetailerId = null
                    productItemModel.updatedAt = null

                    val productItemResult =
                        productItemRepository.createUpdateProductItem(productItemModel)
                    hideLoading()
                    if (productItemResult is StatefulResult.Success)
                        liveData.value = true
                    else
                        handleError(productItemResult.errorModel)
                }
            } else
                handleError(transactionResult.errorModel)
        }
        return liveData
    }

    fun getProductItemBySerial(serial: String): LiveData<Pair<ProductModel?, ProductItemModel?>> {
        val liveData = LiveEvent<Pair<ProductModel?, ProductItemModel?>>()
        safeLauncher {
            var pair = Pair<ProductModel?, ProductItemModel?>(null, null)
            val result =
                productItemRepository.getProductItemBySerial(serial)
            if (result is StatefulResult.Success) {
                if (result.data != null) {
                    val productModel =
                        productRepository.getProductById(result.data!!.productId ?: "").data
                    pair = Pair(productModel, result.data)
                }
                hideLoading()
                liveData.value = pair
            } else
                handleError(result.errorModel)
        }
        return liveData
    }
}