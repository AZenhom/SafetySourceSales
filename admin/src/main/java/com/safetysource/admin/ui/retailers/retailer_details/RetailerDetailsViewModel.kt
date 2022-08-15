package com.safetysource.admin.ui.retailers.retailer_details

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

    val transactionFilterModel =
        TransactionFilterModel(retailer = retailerModel, dateFrom = null, dateTo = null)

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

    fun redeemRetailerCommission(): LiveData<Boolean> {
        showLoading()
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            val retailerReport =
                reportsRepository.getRetailerReportById(retailerModel?.id ?: "").data
            if (retailerReport == null) {
                liveData.value = false
                hideLoading()
            }
            retailerReport!!.totalRedeemed =
                (retailerReport.totalRedeemed ?: 0f) + (retailerReport.dueCommissionValue ?: 0f)
            retailerReport.dueCommissionValue = 0f
            retailerReport.updatedAt = null

            val teamReport =
                reportsRepository.getTeamReportById(retailerModel?.teamId ?: "").data
            if (teamReport == null) {
                liveData.value = false
                hideLoading()
            }
            teamReport!!.totalRedeemed =
                (teamReport.totalRedeemed ?: 0f) + (teamReport.dueCommissionValue ?: 0f)
            teamReport.dueCommissionValue = 0f
            teamReport.updatedAt = null

            reportsRepository.createUpdateRetailerReport(retailerReport)
            reportsRepository.createUpdateTeamReport(teamReport)

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
}