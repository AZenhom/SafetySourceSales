package com.safetysource.appadmin.ui.retailers.retailer_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.RetailerModel
import com.safetysource.data.model.RetailerReportModel
import com.safetysource.data.model.TransactionModel
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.ReportsRepository
import com.safetysource.data.repository.TransactionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RetailerDetailsViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val reportsRepository: ReportsRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val retailerModel: RetailerModel? =
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
}