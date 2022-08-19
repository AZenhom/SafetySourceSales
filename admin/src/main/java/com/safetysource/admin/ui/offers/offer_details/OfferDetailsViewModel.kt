package com.safetysource.admin.ui.offers.offer_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.*
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OfferDetailsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val productCategoryRepository: ProductCategoryRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    var offerModel: OfferModel? =
        savedStateHandle[OfferDetailsActivity.OFFER_MODEL]

    private val _exclusiveCategoryLiveData = MutableLiveData<ProductCategoryModel?>()
    val exclusiveCategoryLiveData: LiveData<ProductCategoryModel?> get() = _exclusiveCategoryLiveData

    private val _exclusiveProductLiveData = MutableLiveData<ProductModel?>()
    val exclusiveProductLiveData: LiveData<ProductModel?> get() = _exclusiveProductLiveData

    init {
        getProductCategory()
        getProduct()
    }

    private fun getProduct() {
        if (offerModel?.productId == null)
            return
        showLoading()
        safeLauncher {
            val result =
                productRepository.getProductById(offerModel?.productId ?: "")
            hideLoading()
            if (result is StatefulResult.Success)
                _exclusiveProductLiveData.value = result.data
            else
                handleError(result.errorModel)
        }
    }

    private fun getProductCategory() {
        if (offerModel?.productCategoryId == null)
            return
        showLoading()
        safeLauncher {
            val result =
                productCategoryRepository
                    .getProductCategoryById(offerModel?.productCategoryId ?: "")
            hideLoading()
            if (result is StatefulResult.Success)
                _exclusiveCategoryLiveData.value = result.data
            else
                handleError(result.errorModel)
        }
    }
}