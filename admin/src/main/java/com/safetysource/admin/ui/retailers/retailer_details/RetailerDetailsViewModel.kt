package com.safetysource.admin.ui.retailers.retailer_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
class RetailerDetailsViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val reportsRepository: ReportsRepository,
    private val userRepository: UserRepository,
    private val productItemRepository: ProductItemRepository,
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val teamModel: TeamModel? =
        savedStateHandle[RetailerDetailsActivity.TEAM_MODEL]
    val retailerModel: RetailerModel? =
        savedStateHandle[RetailerDetailsActivity.RETAILER_MODEL]

    private val _productsLiveData = MutableLiveData<List<ProductModel>>()
    val productsLiveData: LiveData<List<ProductModel>> get() = _productsLiveData

    val transactionFilterModel =
        TransactionFilterModel(retailer = retailerModel, dateFrom = null, dateTo = null)

    init {
        getRestrictedProducts()
    }

    private fun getRestrictedProducts() {
        showLoading()
        safeLauncher {
            val products = (retailerModel?.allowedProductIds ?: emptyList())
                .map { async { productRepository.getProductById(it) } }
                .map { it.await() }.filterIsInstance<StatefulResult.Success<ProductModel>>()
                .mapNotNull { it.data }
            _productsLiveData.value = products
            hideLoading()
        }
    }

    fun getRetailerReport(): LiveData<RetailerReportModel?> {
        showLoading()
        val liveData = LiveEvent<RetailerReportModel?>()
        safeLauncher {
            val result = reportsRepository.getRetailerReportById(retailerModel?.id ?: "")
            hideLoading()
            if (result is StatefulResult.Success)
                liveData.value = result.data
            else
                handleError(result.errorModel)
        }
        return liveData
    }

    fun getTransactions(): LiveData<List<TransactionModel>> {
        showLoading()
        val liveData = LiveEvent<List<TransactionModel>>()
        safeLauncher {
            val result =
                transactionsRepository.getTransactions(transactionFilterModel)
            if (result is StatefulResult.Success) {
                val transactions = result.data ?: listOf()

                // Retailer
                transactions.forEach { it.retailerModel = retailerModel }

                // Team
                transactions.forEach { it.teamModel = teamModel }

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
                liveData.value = result.data ?: listOf()
                hideLoading()
            } else
                handleError(result.errorModel)
        }
        return liveData
    }

    fun redeemRetailerCommission(valueToRedeem: Float): LiveData<Boolean> {
        showLoading()
        val liveData = LiveEvent<Boolean>()
        safeLauncher {

            val response1 =
                async { reportsRepository.getRetailerReportById(retailerModel?.id ?: "").data }
            val response2 =
                async { reportsRepository.getTeamReportById(retailerModel?.teamId ?: "").data }
            val retReport = response1.await()
            val teamReport = response2.await()

            if (retReport == null || teamReport == null) {
                liveData.value = false
                hideLoading()
            }
            retReport!!.totalRedeemed = (retReport.totalRedeemed ?: 0f) + valueToRedeem
            retReport.dueCommissionValue = (retReport.dueCommissionValue ?: 0f) - valueToRedeem
            retReport.updatedAt = null

            teamReport!!.totalRedeemed = (teamReport.totalRedeemed ?: 0f) + valueToRedeem
            teamReport.dueCommissionValue = (teamReport.dueCommissionValue ?: 0f) - valueToRedeem
            teamReport.updatedAt = null

            val result1 = async { reportsRepository.createUpdateRetailerReport(retReport) }
            val result2 = async { reportsRepository.createUpdateTeamReport(teamReport) }
            result1.await(); result2.await()

            liveData.value = true
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