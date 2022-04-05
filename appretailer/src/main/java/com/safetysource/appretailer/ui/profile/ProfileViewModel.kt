package com.safetysource.appretailer.ui.profile

import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.ProductItemModel
import com.safetysource.data.model.RetailerModel
import com.safetysource.data.model.TeamModel
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.RetailerRepository
import com.safetysource.data.repository.TeamRepository
import com.safetysource.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val retailerRepository: RetailerRepository,
    private val teamRepository: TeamRepository,
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
}