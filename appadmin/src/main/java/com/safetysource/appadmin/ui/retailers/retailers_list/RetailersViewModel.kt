package com.safetysource.appadmin.ui.retailers.retailers_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.ProductCategoryModel
import com.safetysource.data.model.RetailerModel
import com.safetysource.data.model.TeamReportModel
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.ReportsRepository
import com.safetysource.data.repository.RetailerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RetailersViewModel @Inject constructor(
    private val retailerRepository: RetailerRepository,
    private val reportsRepository: ReportsRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val teamId: String? = savedStateHandle[RetailersActivity.TEAM_ID]

    fun getTeamReport(): LiveData<TeamReportModel?> {
        showLoading()
        val liveData = LiveEvent<TeamReportModel?>()
        safeLauncher {
            val result = reportsRepository.getTeamReportById(teamId ?: "")
            if (result is StatefulResult.Success)
                liveData.value = result.data
            else
                handleError(result.errorModel)
            hideLoading()
        }
        return liveData
    }

    fun getRetailers(): LiveData<List<RetailerModel>> {
        showLoading()
        val liveData = LiveEvent<List<RetailerModel>>()
        safeLauncher {
            val result = retailerRepository.getTeamRetailers(teamId ?: "")
            if (result is StatefulResult.Success)
                liveData.value = result.data ?: listOf()
            else
                handleError(result.errorModel)
            hideLoading()
        }
        return liveData
    }
}