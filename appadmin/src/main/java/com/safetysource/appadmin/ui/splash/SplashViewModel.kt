package com.safetysource.appadmin.ui.splash

import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.repository.AdminRepository
import com.safetysource.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val adminRepository: AdminRepository
) : BaseViewModel() {

    private var waitedOnce = false

    fun checkNavigation(): LiveData<NavigationCases> {
        val liveData = LiveEvent<NavigationCases>()
        safeLauncher {
            if(!waitedOnce) {
                delay(2000)
                waitedOnce = true
            }
            val isPhoneSignedIn = userRepository.isSignedIn()
            val currentUser = adminRepository.getCurrentAdminModel()
            liveData.value = when {
                isPhoneSignedIn && currentUser != null -> NavigationCases.REGISTERED
                isPhoneSignedIn -> NavigationCases.NOT_REGISTERED
                else -> NavigationCases.NOT_PHONE_AUTHENTICATED
            }
        }
        return liveData
    }

    fun logout(): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            userRepository.removeAllPref()
            liveData.value = true
        }
        return liveData
    }
}

enum class NavigationCases {
    NOT_PHONE_AUTHENTICATED,
    NOT_REGISTERED,
    REGISTERED,
}