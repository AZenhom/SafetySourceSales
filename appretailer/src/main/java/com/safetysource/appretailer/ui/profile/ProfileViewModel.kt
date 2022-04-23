package com.safetysource.appretailer.ui.profile

import android.content.Context
import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.ProductItemModel
import com.safetysource.data.model.RetailerModel
import com.safetysource.data.model.RetailerReportModel
import com.safetysource.data.model.TeamModel
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.*
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val retailerRepository: RetailerRepository,
    private val reportsRepository: ReportsRepository,
    private val teamRepository: TeamRepository,
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository
) : BaseViewModel() {

    fun getUserProfile(): LiveData<RetailerModel> {
        val liveData = LiveEvent<RetailerModel>()
        safeLauncher {
            liveData.value = retailerRepository.getCurrentRetailerModel()
        }
        return liveData
    }

    fun getRetailerTeam(): LiveData<TeamModel?> {
        showLoading()
        val liveData = LiveEvent<TeamModel?>()
        safeLauncher {
            val result =
                teamRepository.getTeamById(
                    retailerRepository.getCurrentRetailerModel()?.teamId ?: ""
                )
            if (result is StatefulResult.Success)
                liveData.value = result.data
            else
                handleError(result.errorModel)
            hideLoading()
        }
        return liveData
    }

    fun getRetailerReport(): LiveData<RetailerReportModel?> {
        val liveData = LiveEvent<RetailerReportModel?>()
        safeLauncher {
            val result = reportsRepository.getRetailerReportById(userRepository.getUserId() ?: "")
            if (result is StatefulResult.Success)
                liveData.value = result.data
            else
                handleError(result.errorModel)
        }
        return liveData
    }

    fun switchLanguage(context: Context): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            val currentLanguage = settingsRepository.getCurrentLanguage()
            val newLanguage = if (currentLanguage == "en") "ar" else "en"
            Lingver.getInstance().setLocale(context, newLanguage)
            settingsRepository.setCurrentLanguage(newLanguage)
            liveData.value = true
        }
        return liveData
    }
}