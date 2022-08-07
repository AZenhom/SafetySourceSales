package com.safetysource.appadmin.ui.retailers.retailers_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.R
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.*
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.RetailerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RetailersViewModel @Inject constructor(
    private val retailerRepository: RetailerRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val teamModel: TeamModel? = savedStateHandle[RetailersActivity.TEAM_MODEL]

    fun getRetailers(): LiveData<List<RetailerModel>> {
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

    fun addExistingRetailerToTeam(phoneNumber: String): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            showLoading()

            val searchResponse = retailerRepository.getRetailerByPhoneNumber(phoneNumber)
            if (searchResponse !is StatefulResult.Success || searchResponse.data == null) {
                showErrorMsg(R.string.retailer_with_phone_already_registered)
                hideLoading()
                return@safeLauncher
            } else if (searchResponse.data?.teamId != null) {
                showErrorMsg(R.string.retailer_with_phone_does_not_exist)
                hideLoading()
                return@safeLauncher
            }

            val retailer = searchResponse.data!!
            retailer.teamId = teamModel?.id

            val result = retailerRepository.createUpdateRetailer(retailer)
            hideLoading()
            if (result is StatefulResult.Success) {
                liveData.value = true
                showSuccessMsg(R.string.retailer_added_to_team_successfully)
            } else
                handleError(result.errorModel)
        }
        return liveData
    }

    fun removeRetailerFromTeam(retailerModel: RetailerModel): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            showLoading()
            retailerModel.teamId = null
            val response = retailerRepository.createUpdateRetailer(retailerModel)
            hideLoading()
            if (response is StatefulResult.Success) {
                liveData.value = true
                getRetailers()
            } else
                handleError(response.errorModel)
        }
        return liveData
    }
}