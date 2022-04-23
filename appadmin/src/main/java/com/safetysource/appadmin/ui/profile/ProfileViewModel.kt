package com.safetysource.appadmin.ui.profile

import android.content.Context
import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.AdminModel
import com.safetysource.data.model.RetailerModel
import com.safetysource.data.repository.AdminRepository
import com.safetysource.data.repository.RetailerRepository
import com.safetysource.data.repository.SettingsRepository
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val settingsRepository: SettingsRepository
) : BaseViewModel() {

    private lateinit var currentLanguage: String

    fun getUserProfile(): LiveData<AdminModel> {
        val liveData = LiveEvent<AdminModel>()
        safeLauncher {
            liveData.value = adminRepository.getCurrentAdminModel()
        }
        return liveData
    }

    fun switchLanguage(context: Context): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            val currentLanguage = settingsRepository.getCurrentLanguage()
            val newLanguage = if (currentLanguage == "en") "ar" else "en"
            Lingver.getInstance().setLocale(context, newLanguage)
            settingsRepository.setCurrentLanguage(newLanguage)
            liveData.value = true
        }
        return liveData
    }
}