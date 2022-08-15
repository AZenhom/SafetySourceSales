package com.safetysource.admin.ui.offers.create_edit_offer

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.OfferModel
import com.safetysource.data.model.ProductCategoryModel
import com.safetysource.data.model.ProductModel
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.OfferRepository
import com.safetysource.data.repository.ProductCategoryRepository
import com.safetysource.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreateEditOfferViewModel @Inject constructor(
    private val offerRepository: OfferRepository,
    private val productCategoryRepository: ProductCategoryRepository,
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    var offerModel: OfferModel? =
        savedStateHandle[CreateEditOfferActivity.OFFER_TO_EDIT]

    private var offerId: String = if (offerModel != null) offerModel!!.id ?: ""
    else {
        val id = offerRepository.getNewOfferId()
        offerModel = OfferModel(id = id)
        id
    }

    private val _categoriesLiveData = LiveEvent<List<ProductCategoryModel>>()
    val categoriesLiveData: LiveData<List<ProductCategoryModel>> get() = _categoriesLiveData
    private val _selectedCategoryLiveData = LiveEvent<ProductCategoryModel?>()
    val selectedCategoryLiveData: LiveData<ProductCategoryModel?> get() = _selectedCategoryLiveData

    private val _productsLiveData = LiveEvent<List<ProductModel>>()
    val productsLiveData: LiveData<List<ProductModel>> get() = _productsLiveData
    private val _selectedProductLiveData = LiveEvent<ProductModel?>()
    val selectedProductLiveData: LiveData<ProductModel?> get() = _selectedProductLiveData

    var startsAt: Date = offerModel?.startsAt ?: Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
    var expiresAt: Date = offerModel?.expiresAt ?: Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.time

    fun uploadOfferImageAndGetUrl(fileUri: Uri): LiveData<String> {
        val liveData = LiveEvent<String>()
        safeLauncher {
            showLoading()
            val result =
                offerRepository.uploadOfferImageAndGetUrl(offerId, fileUri)
            hideLoading()
            if (result is StatefulResult.Success) {
                liveData.value = result.data ?: ""
            } else
                handleError(result.errorModel)
        }
        return liveData
    }

    fun createUpdateOffer(
        text: String,
        imageUrl: String,
        neededSellCount: Int,
        canRepeat: Boolean,
        valPerRepeat: Float,
    ): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            showLoading()
            val offer = OfferModel(
                id = offerId,
                imgUrl = imageUrl,
                text = text,
                productCategoryId = selectedCategoryLiveData.value?.id,
                productId = selectedProductLiveData.value?.id,
                neededSellCount = neededSellCount,
                canRepeat = canRepeat,
                valPerRepeat = valPerRepeat,
                startsAt = startsAt,
                expiresAt = expiresAt,
            )
            val response = offerRepository.createUpdateOffer(offer)
            hideLoading()
            if (response is StatefulResult.Success)
                liveData.value = true
            else
                handleError(response.errorModel)
        }
        return liveData
    }

    fun setCategory(category: ProductCategoryModel?) {
        offerModel?.productCategoryId = category?.id
        offerModel?.productId = null
        _productsLiveData.value = emptyList()
        _selectedCategoryLiveData.value = category
        safeLauncher {
            showLoading()
            getProducts()
            hideLoading()
        }
    }

    fun setProduct(product: ProductModel?) {
        offerModel?.productId = product?.id
        _selectedProductLiveData.value = product
    }

    fun getInitialData() = safeLauncher {
        showLoading()
        getProductCategories()
        getProducts()
        hideLoading()
    }

    private suspend fun getProductCategories() {
        val result = productCategoryRepository.getProductCategories()
        if (result is StatefulResult.Success) {
            _categoriesLiveData.value = result.data ?: listOf()
            if (selectedCategoryLiveData.value == null && offerModel?.productCategoryId != null) {
                _selectedCategoryLiveData.value =
                    result.data?.firstOrNull { it.id == offerModel?.productCategoryId }
            }
        } else
            handleError(result.errorModel)
    }

    private suspend fun getProducts() {
        offerModel?.productCategoryId?.let { categoryId ->
            val result = productRepository.getCategoryProducts(categoryId)
            if (result is StatefulResult.Success) {
                _productsLiveData.value = result.data ?: listOf()
                if (selectedProductLiveData.value == null && offerModel?.productId != null) {
                    _selectedProductLiveData.value =
                        result.data?.firstOrNull { it.id == offerModel?.productId }
                }
            } else
                handleError(result.errorModel)
        }
    }
}