package com.safetysource.appretailer.ui.transactions

import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.ProductModel
import com.safetysource.data.model.TransactionFilterModel
import com.safetysource.data.model.TransactionModel
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val productRepository: ProductRepository,
    private val teamRepository: TeamRepository,
    private val retailerRepository: RetailerRepository,
) : BaseViewModel() {

    fun getTransactions(): LiveData<List<TransactionModel>> {
        showLoading()
        val liveData = LiveEvent<List<TransactionModel>>()
        safeLauncher {
            val result =
                transactionsRepository.getTransactions(
                    TransactionFilterModel(
                        retailer = retailerRepository.getCurrentRetailerModel(),
                        dateFrom = null,
                        dateTo = null
                    )
                )
            if (result is StatefulResult.Success) {
                val transactions = result.data ?: listOf()

                // Retailers
                val retailerModel = retailerRepository.getCurrentRetailerModel()
                transactions.forEach { it.retailerModel = retailerModel }

                // Teams
                val teamModel = teamRepository.getTeamById(
                    retailerRepository.getCurrentRetailerModel()?.teamId ?: ""
                ).data
                transactions.forEach {
                    it.teamModel = teamModel
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
            } else
                handleError(result.errorModel)
            hideLoading()
        }
        return liveData
    }
}