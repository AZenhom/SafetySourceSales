package com.safetysource.appadmin.ui.retailers.retailer_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.ProductItemState
import com.safetysource.data.model.RetailerModel
import com.safetysource.data.model.RetailerReportModel
import com.safetysource.data.model.TransactionModel
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.ProductItemRepository
import com.safetysource.data.repository.ReportsRepository
import com.safetysource.data.repository.TransactionsRepository
import com.safetysource.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RetailerDetailsViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val reportsRepository: ReportsRepository,
    private val userRepository: UserRepository,
    private val productItemRepository: ProductItemRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val retailerModel: RetailerModel? =
        savedStateHandle[RetailerDetailsActivity.RETAILER_MODEL]

    fun getRetailerReport(): LiveData<RetailerReportModel?> {
        showLoading()
        val liveData = LiveEvent<RetailerReportModel?>()
        safeLauncher {
            val result = reportsRepository.getRetailerReportById(retailerModel?.id ?: "")
            if (result is StatefulResult.Success)
                liveData.value = result.data
            else
                handleError(result.errorModel)
            hideLoading()
        }
        return liveData
    }

    fun getTransactions(): LiveData<List<TransactionModel>> {
        showLoading()
        val liveData = LiveEvent<List<TransactionModel>>()
        safeLauncher {
            val result =
                transactionsRepository.getTransactions(retailerId = retailerModel?.id ?: "")
            if (result is StatefulResult.Success)
                liveData.value = result.data ?: listOf()
            else
                handleError(result.errorModel)
            hideLoading()
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