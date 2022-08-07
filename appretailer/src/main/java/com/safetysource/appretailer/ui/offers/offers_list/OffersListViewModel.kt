package com.safetysource.appretailer.ui.offers.offers_list

import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.OfferModel
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.OfferRepository
import com.safetysource.data.repository.SubscribedOfferRepository
import com.safetysource.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class OffersListViewModel @Inject constructor(
    private val offerRepository: OfferRepository,
    private val subscribedOfferRepository: SubscribedOfferRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _availableOffersLiveData = LiveEvent<List<OfferModel>>()
    val availableOffersLiveData: LiveData<List<OfferModel>> get() = _availableOffersLiveData

    private val _subscribedOffersLiveData = LiveEvent<List<OfferModel>>()
    val subscribedOffersLiveData: LiveData<List<OfferModel>> get() = _subscribedOffersLiveData

    fun getData() {
        safeLauncher {
            showLoading()
            val t1 = async { getAllAvailableOffers() }
            val t2 = async { getAllSubscribedOffers() }
            t1.await(); t2.await()
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

    private suspend fun getAllSubscribedOffers() = safeLauncher {
        val result =
            subscribedOfferRepository.getRetailerSubscribedOffers(userRepository.getUserId() ?: "")
        if (result is StatefulResult.Success) {
            result.data?.let { subscribedOffers ->
                val offers = subscribedOffers
                    .map { it.offerId }
                    .map { async { offerRepository.getOfferById(it ?: "") } }
                    .map { it.await() }.filterIsInstance<StatefulResult.Success<OfferModel>>()
                    .map { it.data }
                    .filterNotNull()
                offers.forEach { offer ->
                    offer.subscribedOfferModel =
                        subscribedOffers.firstOrNull { it.offerId == offer.id }
                }
                _subscribedOffersLiveData.value = offers
            }
        } else
            handleError(result.errorModel)
    }

}