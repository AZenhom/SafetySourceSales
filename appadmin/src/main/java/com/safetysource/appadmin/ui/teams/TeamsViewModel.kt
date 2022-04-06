package com.safetysource.appadmin.ui.teams

import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.TeamModel
import com.safetysource.data.model.TeamReportModel
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.ReportsRepository
import com.safetysource.data.repository.TeamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TeamsViewModel @Inject constructor(
    private val teamRepository: TeamRepository,
    private val reportsRepository: ReportsRepository,
) : BaseViewModel() {

    fun createTeam(name: String): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            showLoading()
            val teamId = teamRepository.getNewTeamId()
            val team = TeamModel(id = teamId, name = name)
            val teamCreateResponse = teamRepository.createUpdateTeam(team)
            if (teamCreateResponse is StatefulResult.Success) {
                val teamReport = TeamReportModel(
                    teamId = teamId,
                    dueCommissionValue = 0.0f,
                    totalRedeemed = 0.0f
                )
                val reportRepository = reportsRepository.createUpdateTeamReport(teamReport)
                if (reportRepository is StatefulResult.Success)
                    liveData.value = true
                else
                    handleError(teamCreateResponse.errorModel)
            } else
                handleError(teamCreateResponse.errorModel)
            hideLoading()
        }
        return liveData
    }

    fun getTeams(): LiveData<List<TeamModel>> {
        showLoading()
        val liveData = LiveEvent<List<TeamModel>>()
        safeLauncher {
            val result = teamRepository.getTeams()
            if (result is StatefulResult.Success)
                liveData.value = result.data ?: listOf()
            else
                handleError(result.errorModel)
            hideLoading()
        }
        return liveData
    }
}