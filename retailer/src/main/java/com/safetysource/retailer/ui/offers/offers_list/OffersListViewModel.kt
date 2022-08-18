package com.safetysource.retailer.ui.offers.offers_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _availableOffersLiveData = MutableLiveData<List<OfferModel>>()
    val availableOffersLiveData: LiveData<List<OfferModel>> get() = _availableOffersLiveData

    private val _subscribedOffersLiveData = MutableLiveData<List<OfferModel>>()
    val subscribedOffersLiveData: LiveData<List<OfferModel>> get() = _subscribedOffersLiveData

    fun getData() = safeLauncher {
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
                getAvailableOffers()
            }
        } else
            handleError(result.errorModel)
    }

    private suspend fun getAvailableOffers() {
        val result = offerRepository.getAllAvailableOffers()
        if (result is StatefulResult.Success) {
            val availableOffers = result.data?.toMutableList() ?: mutableListOf()
            availableOffers.removeIf { availableOffer ->
                subscribedOffersLiveData.value?.find { it.id == availableOffer.id } != null
            }
            _availableOffersLiveData.value = availableOffers
        } else
            handleError(result.errorModel)
    }

}