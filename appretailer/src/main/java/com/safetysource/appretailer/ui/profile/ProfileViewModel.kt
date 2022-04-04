package com.safetysource.appretailer.ui.profile

import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.RetailerModel
import com.safetysource.data.repository.RetailerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val retailerRepository: RetailerRepository
) : BaseViewModel() {

    fun getUserProfile(): LiveData<RetailerModel> {
        val liveData = LiveEvent<RetailerModel>()
        safeLauncher {
            liveData.value = retailerRepository.getCurrentRetailerModel()
        }
        return liveData
    }
}