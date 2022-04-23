package com.safetysource.appadmin.ui.admins

import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.R
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.*
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateEditAdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
) : BaseViewModel() {

    fun createNewAdmin(
        phoneNumber: String,
        name: String,
    ): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            showLoading()

            val searchResponse = adminRepository.getAdminByPhoneNumber(phoneNumber)
            if (searchResponse !is StatefulResult.Success || searchResponse.data != null) {
                showErrorMsg(R.string.admin_with_phone_already_registered)
                hideLoading()
                return@safeLauncher
            }

            val admin = AdminModel(
                id = adminRepository.getNewAdminId(),
                name = name,
                phoneNo = phoneNumber,
                role = AdminRole.SECONDARY_ADMIN
            )
            val adminCreateResponse = adminRepository.createUpdateAdmin(admin)
            if (adminCreateResponse is StatefulResult.Success)
                liveData.value = true
            else
                handleError(adminCreateResponse.errorModel)
            hideLoading()
        }
        return liveData
    }
}