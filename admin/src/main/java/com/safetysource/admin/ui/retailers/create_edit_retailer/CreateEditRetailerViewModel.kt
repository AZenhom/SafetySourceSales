package com.safetysource.admin.ui.retailers.create_edit_retailer

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.R
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.ProductModel
import com.safetysource.data.model.RetailerModel
import com.safetysource.data.model.RetailerReportModel
import com.safetysource.data.model.TeamModel
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.ProductRepository
import com.safetysource.data.repository.ReportsRepository
import com.safetysource.data.repository.RetailerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateEditRetailerViewModel @Inject constructor(
    private val retailerRepository: RetailerRepository,
    private val reportsRepository: ReportsRepository,
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val teamModel: TeamModel? = savedStateHandle[CreateEditRetailerActivity.TEAM_MODEL]
    val retailerToEdit: RetailerModel? =
        savedStateHandle[CreateEditRetailerActivity.RETAILER_TO_EDIT]

    private val _productsLiveData = LiveEvent<List<ProductModel>>()
    val productsLiveData: LiveData<List<ProductModel>> get() = _productsLiveData

    init {
        getAllProducts()
    }

    fun createNewRetailer(
        phoneNumber: String,
        contactNumber: String,
        name: String,
    ): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            showLoading()

            val searchResponse = retailerRepository.getRetailerByPhoneNumber(phoneNumber)
            if (searchResponse !is StatefulResult.Success || searchResponse.data != null) {
                showErrorMsg(R.string.retailer_with_phone_already_registered)
                hideLoading()
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
                hideLoading()
                if (reportRepository is StatefulResult.Success)
                    liveData.value = true
                else
                    handleError(retailerCreateResponse.errorModel)
            } else
                handleError(retailerCreateResponse.errorModel)
        }
        return liveData
    }

    fun updateRetailer(name: String): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            if (retailerToEdit == null) {
                showErrorMsg(R.string.something_went_wrong)
                return@safeLauncher
            }
            retailerToEdit.name = name
            showLoading()
            val result = retailerRepository.createUpdateRetailer(retailerToEdit)
            if (result is StatefulResult.Success)
                liveData.value = true
            else
                handleError(result.errorModel)
            hideLoading()
        }
        return liveData
    }

    private fun getAllProducts() {
        showLoading()
        safeLauncher {
            val result = productRepository.getAllProducts()
            hideLoading()
            if (result is StatefulResult.Success)
                _productsLiveData.value = result.data ?: emptyList()
            else
                handleError(result.errorModel)
        }
    }
}