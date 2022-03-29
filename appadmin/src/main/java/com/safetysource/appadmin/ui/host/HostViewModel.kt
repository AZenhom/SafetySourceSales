package com.safetysource.appadmin.ui.host

import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HostViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : BaseViewModel() {

    private val _onLogOutLiveData: LiveEvent<Boolean> = LiveEvent()
    val onLogOutLiveData: LiveData<Boolean> get() = _onLogOutLiveData

    fun logout() = safeLauncher {
        userRepository.removeAllPref()
        _onLogOutLiveData.value = true
    }
}