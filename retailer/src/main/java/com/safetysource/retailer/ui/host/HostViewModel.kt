package com.safetysource.retailer.ui.host

import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.RetailerRepository
import com.safetysource.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HostViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val retailerRepository: RetailerRepository
) : BaseViewModel() {

    private val _onLogOutLiveData: LiveEvent<Boolean> = LiveEvent()
    val onLogOutLiveData: LiveData<Boolean> get() = _onLogOutLiveData

    fun updateCurrentRetailerModel() = safeLauncher {
        val userId = userRepository.getUserId()
        if (userId.isNullOrEmpty()) {
            logout()
            return@safeLauncher
        }
        val result = retailerRepository.getRetailerById(userId)
        if (result is StatefulResult.Success && result.data != null)
            retailerRepository.setCurrentRetailerModel(result.data)
        else
            logout()
    }

    fun logout() = safeLauncher {
        userRepository.removeAllPref()
        _onLogOutLiveData.value = true
    }
}