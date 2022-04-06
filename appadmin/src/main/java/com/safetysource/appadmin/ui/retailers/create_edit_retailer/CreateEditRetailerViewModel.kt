package com.safetysource.appadmin.ui.retailers.create_edit_retailer

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.R
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.RetailerModel
import com.safetysource.data.model.RetailerReportModel
import com.safetysource.data.model.TeamModel
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.ReportsRepository
import com.safetysource.data.repository.RetailerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateEditRetailerViewModel @Inject constructor(
    private val retailerRepository: RetailerRepository,
    private val reportsRepository: ReportsRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val teamModel: TeamModel? = savedStateHandle[CreateEditRetailerActivity.TEAM_MODEL]

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
                teamId = teamModel?.id,
                name = name,
                phoneNo = phoneNumber,
                contactNo = contactNumber,
            )
            val retailerCreateResponse = retailerRepository.createUpdateRetailer(retailer)
            if (retailerCreateResponse is StatefulResult.Success) {
                val retailerReport = RetailerReportModel(
                    retailerId = retailer.id,
                    dueCommissionValue = 0.0f,
                    totalRedeemed = 0.0f
                )
                val reportRepository = reportsRepository.createUpdateRetailerReport(retailerReport)
                if (reportRepository is StatefulResult.Success)
                    liveData.value = true
                else
                    handleError(retailerCreateResponse.errorModel)
            } else
                handleError(retailerCreateResponse.errorModel)
            hideLoading()
        }
        return liveData
    }
}