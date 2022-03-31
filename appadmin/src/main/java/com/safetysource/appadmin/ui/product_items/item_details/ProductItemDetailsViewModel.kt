package com.safetysource.appadmin.ui.product_items.item_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.ProductItemModel
import com.safetysource.data.model.ProductItemState
import com.safetysource.data.model.ProductModel
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.ProductItemRepository
import com.safetysource.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductItemDetailsViewModel @Inject constructor(
    private val productItemRepository: ProductItemRepository,
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val productItemModel: ProductItemModel? =
        savedStateHandle[ProductItemDetailsActivity.PRODUCT_ITEM_MODEL]

    fun getProduct(): LiveData<ProductModel?> {
        showLoading()
        val liveData = LiveEvent<ProductModel?>()
        safeLauncher {
            val result =
                productRepository.getProductById(productItemModel?.productId ?: "")
            if (result is StatefulResult.Success)
                liveData.value = result.data
            else
                handleError(result.errorModel)
            hideLoading()
        }
        return liveData
    }

    fun updateProductItemState(
        state: ProductItemState,
    ): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            if(productItemModel == null)
                return@safeLauncher
            showLoading()

            productItemModel.state = state
            productItemModel.updatedAt = null

            val response = productItemRepository.createUpdateProductItem(productItemModel)
            hideLoading()
            if (response is StatefulResult.Success)
                liveData.value = true
            else
                handleError(response.errorModel)
        }
        return liveData
    }
}