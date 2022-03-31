package com.safetysource.appadmin.ui.retailers.create_edit_retailer

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.R
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.RetailerModel
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.RetailerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateEditRetailerViewModel @Inject constructor(
    private val retailerRepository: RetailerRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val teamId: String? = savedStateHandle[CreateEditRetailerActivity.TEAM_ID]

    fun createNewRetailer(
        phoneNumber: String,
        contactNumber: String,
        name: String,
    ): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            showLoading()

            val searchResponse = retailerRepository.getRetailerByPhoneNumber(phoneNumber)
            if (searchResponse !is StatefulResult.Success || searchResponse.data == null) {
                showErrorMsg(R.string.retailer_with_phone_already_registered)
                return@safeLauncher
            }

            val retailer = RetailerModel(
                id = retailerRepository.getNewRetailerId(),
                teamId = teamId,
                name = name,
                phoneNo = phoneNumber,
                contactNo = contactNumber,
            )
            val response = retailerRepository.createUpdateRetailer(retailer)
            hideLoading()
            if (response is StatefulResult.Success)
                liveData.value = true
            else
                handleError(response.errorModel)
        }
        return liveData
    }
}