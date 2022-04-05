package com.safetysource.appadmin.ui.product_items.items_statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.ProductItemModel
import com.safetysource.data.model.ProductItemState
import com.safetysource.data.model.ProductModel
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.ProductItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductItemStatisticsViewModel @Inject constructor(
    private val productItemRepository: ProductItemRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val productModel: ProductModel? =
        savedStateHandle[ProductItemsStatisticsActivity.PRODUCT_MODEL]

    fun createProductItem(
        serial: String,
    ): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            showLoading()
            val productItemModel = ProductItemModel(
                productId = productModel?.id,
                serial = serial,
                state = ProductItemState.NOT_SOLD_YET
            )
            val response = productItemRepository.createUpdateProductItem(productItemModel)
            hideLoading()
            if (response is StatefulResult.Success)
                liveData.value = true
            else
                handleError(response.errorModel)
        }
        return liveData
    }

    fun getProductItemBySerial(serial: String): LiveData<ProductItemModel?> {
        showLoading()
        val liveData = LiveEvent<ProductItemModel?>()
        safeLauncher {
            val result =
                productItemRepository.getProductItemBySerial(serial)
            if (result is StatefulResult.Success)
                liveData.value = result.data
            else
                handleError(result.errorModel)
            hideLoading()
        }
        return liveData
    }

    fun getProductItems(): LiveData<List<ProductItemModel>> {
        showLoading()
        val liveData = LiveEvent<List<ProductItemModel>>()
        safeLauncher {
            val result =
                productItemRepository.getProductItems(productModel?.id ?: "")
            if (result is StatefulResult.Success)
                liveData.value = result.data ?: listOf()
            else
                handleError(result.errorModel)
            hideLoading()
        }
        return liveData
    }
}