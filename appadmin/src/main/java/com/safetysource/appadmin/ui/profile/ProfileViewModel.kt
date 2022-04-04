package com.safetysource.appadmin.ui.profile

import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.AdminModel
import com.safetysource.data.model.RetailerModel
import com.safetysource.data.repository.AdminRepository
import com.safetysource.data.repository.RetailerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : BaseViewModel() {

    fun getUserProfile(): LiveData<AdminModel> {
        val liveData = LiveEvent<AdminModel>()
        safeLauncher {
            liveData.value = adminRepository.getCurrentAdminModel()
        }
        return liveData
    }
}