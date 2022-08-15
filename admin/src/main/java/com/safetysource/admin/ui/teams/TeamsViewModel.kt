package com.safetysource.admin.ui.teams

import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.TeamModel
import com.safetysource.data.model.TeamReportModel
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.ReportsRepository
import com.safetysource.data.repository.TeamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
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
                hideLoading()
                if (reportRepository is StatefulResult.Success)
                    liveData.value = true
                else
                    handleError(teamCreateResponse.errorModel)
            } else
                handleError(teamCreateResponse.errorModel)
        }
        return liveData
    }

    fun getTeams(): LiveData<List<TeamModel>> {
        showLoading()
        val liveData = LiveEvent<List<TeamModel>>()
        safeLauncher {
            val teamsResult = teamRepository.getTeams()
            if (teamsResult is StatefulResult.Success) {
                val teams = teamsResult.data ?: listOf()
                val teamReports = teams
                    .map { it.id }
                    .map { async { reportsRepository.getTeamReportById(it ?: "") } }
                    .map { it.await() }.filterIsInstance<StatefulResult.Success<TeamReportModel>>()
                    .map { it.data }
                teams.forEach { teamModel ->
                    teamModel.teamReportModel =
                        teamReports.firstOrNull { teamModel.id == it?.teamId }
                }
                liveData.value = teams
                hideLoading()
            } else
                handleError(teamsResult.errorModel)
        }
        return liveData
    }
}