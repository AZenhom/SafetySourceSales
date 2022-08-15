package com.safetysource.admin.ui.offers.offers_lists

import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.OfferModel
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.OfferRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class OffersListsViewModel @Inject constructor(
    private val offerRepository: OfferRepository,
) : BaseViewModel() {

    private val _availableOffersLiveData = LiveEvent<List<OfferModel>>()
    val availableOffersLiveData: LiveData<List<OfferModel>> get() = _availableOffersLiveData

    private val _upcomingOffersLiveData = LiveEvent<List<OfferModel>>()
    val upcomingOffersLiveData: LiveData<List<OfferModel>> get() = _upcomingOffersLiveData

    private val _historyOffersLiveData = LiveEvent<List<OfferModel>>()
    val historyOffersLiveData: LiveData<List<OfferModel>> get() = _historyOffersLiveData

    fun getData() {
        safeLauncher {
            showLoading()
            val t1 = async { getAllAvailableOffers() }
            val t2 = async { getAllUpcomingOffers() }
            val t3 = async { getAllHistoryOffers() }
            t1.await(); t2.await(); t3.await()
            hideLoading()
        }
    }

    private suspend fun getAllAvailableOffers() {
        val result = offerRepository.getAllAvailableOffers()
        if (result is StatefulResult.Success)
            _availableOffersLiveData.value = result.data ?: listOf()
        else
            handleError(result.errorModel)
    }

    private suspend fun getAllUpcomingOffers() {
        val result = offerRepository.getAllUpcomingOffers()
        if (result is StatefulResult.Success)
            _upcomingOffersLiveData.value = result.data ?: listOf()
        else
            handleError(result.errorModel)
    }

    private suspend fun getAllHistoryOffers() {
        val result = offerRepository.getAllHistoryOffers()
        if (result is StatefulResult.Success)
            _historyOffersLiveData.value = result.data ?: listOf()
        else
            handleError(result.errorModel)
    }
}