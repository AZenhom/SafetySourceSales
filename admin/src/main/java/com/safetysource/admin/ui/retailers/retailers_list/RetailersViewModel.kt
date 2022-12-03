package com.safetysource.admin.ui.retailers.retailers_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.R
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.*
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.ReportsRepository
import com.safetysource.data.repository.RetailerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class RetailersViewModel @Inject constructor(
    private val retailerRepository: RetailerRepository,
    private val reportsRepository: ReportsRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val teamModel: TeamModel? = savedStateHandle[RetailersActivity.TEAM_MODEL]

    fun getTeamReport(): LiveData<TeamReportModel?> {
        val liveData = LiveEvent<TeamReportModel?>()
        safeLauncher {
            val result = reportsRepository.getTeamReportById(teamModel?.id ?: "")
            if (result is StatefulResult.Success)
                liveData.value = result.data
            else
                handleError(result.errorModel)
        }
        return liveData
    }

    fun getTeamRetailers(): LiveData<List<RetailerModel>> {
        showLoading()
        val liveData = LiveEvent<List<RetailerModel>>()
        safeLauncher {
            val result = retailerRepository.getTeamRetailers(teamModel?.id ?: "")
            hideLoading()
            if (result is StatefulResult.Success)
                liveData.value = result.data ?: listOf()
            else
                handleError(result.errorModel)
        }
        return liveData
    }

    fun getTeamLessRetailers(): LiveData<List<RetailerModel>> {
        showLoading()
        val liveData = LiveEvent<List<RetailerModel>>()
        safeLauncher {
            val result = retailerRepository.getTeamLessRetailers()
            hideLoading()
            if (result is StatefulResult.Success)
                liveData.value = result.data ?: listOf()
            else
                handleError(result.errorModel)
        }
        return liveData
    }

    fun addExistingRetailerToTeam(retailer: RetailerModel): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            showLoading()

            val teamReport = reportsRepository.getTeamReportById(teamModel?.id ?: "").data
            val retailerReport = reportsRepository.getRetailerReportById(retailer.id ?: "").data
            if (teamReport == null || retailerReport == null) {
                showErrorMsg(R.string.something_went_wrong)
                hideLoading()
                return@safeLauncher
            }
            retailer.teamId = teamModel?.id
            teamReport.dueCommissionValue =
                (teamReport.dueCommissionValue ?: 0f) + (retailerReport.dueCommissionValue ?: 0f)
            teamReport.totalRedeemed =
                (teamReport.totalRedeemed ?: 0f) + (retailerReport.totalRedeemed ?: 0f)
            teamReport.updatedAt = null

            val result1 = async { retailerRepository.createUpdateRetailer(retailer) }
            val result2 = async { reportsRepository.createUpdateTeamReport(teamReport) }

            if (result1.await() is StatefulResult.Success && result2.await() is StatefulResult.Success) {
                liveData.value = true
                showSuccessMsg(R.string.retailer_added_to_team_successfully)
            } else
                showErrorMsg(R.string.something_went_wrong)
            hideLoading()
        }
        return liveData
    }

    fun removeRetailerFromTeam(retailer: RetailerModel): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            showLoading()
            val teamReport = reportsRepository.getTeamReportById(teamModel?.id ?: "").data
            val retailerReport = reportsRepository.getRetailerReportById(retailer.id ?: "").data
            if (teamReport == null || retailerReport == null) {
                showErrorMsg(R.string.something_went_wrong)
                hideLoading()
                return@safeLauncher
            }
            retailer.teamId = TeamModel.TEAM_LESS
            teamReport.dueCommissionValue =
                (teamReport.dueCommissionValue ?: 0f) - (retailerReport.dueCommissionValue ?: 0f)
            teamReport.totalRedeemed =
                (teamReport.totalRedeemed ?: 0f) - (retailerReport.totalRedeemed ?: 0f)
            teamReport.updatedAt = null

            val result1 = async { retailerRepository.createUpdateRetailer(retailer) }
            val result2 = async { reportsRepository.createUpdateTeamReport(teamReport) }

            if (result1.await() is StatefulResult.Success && result2.await() is StatefulResult.Success) {
                liveData.value = true
                showSuccessMsg(R.string.retailer_removed_from_team_successfully)
            } else
                showErrorMsg(R.string.something_went_wrong)
            hideLoading()
        }
        return liveData
    }
}