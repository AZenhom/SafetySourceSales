package com.safetysource.admin.ui.product_items.items_statistics

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
class ProductItemStatisticsViewModel @Inject constructor(
    private val productItemRepository: ProductItemRepository,
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val productModel: ProductModel? =
        savedStateHandle[ProductItemsStatisticsActivity.PRODUCT_MODEL]

    fun createProductItem(serial: String): LiveData<Boolean> {
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

    fun createProductItems(serials: List<String>): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            showLoading()
            val productItemModels = serials.map { serial ->
                ProductItemModel(
                    productId = productModel?.id,
                    serial = serial,
                    state = ProductItemState.NOT_SOLD_YET
                )
            }
            val response = productItemRepository.createUpdateMultipleProductItems(productItemModels)
            hideLoading()
            if (response is StatefulResult.Success)
                liveData.value = true
            else
                handleError(response.errorModel)
        }
        return liveData
    }

    fun getProductItemBySerial(serial: String): LiveData<Pair<ProductModel?, ProductItemModel?>> {
        val liveData = LiveEvent<Pair<ProductModel?, ProductItemModel?>>()
        safeLauncher {
            var pair = Pair<ProductModel?, ProductItemModel?>(null, null)
            val result =
                productItemRepository.getProductItemBySerial(serial)
            if (result is StatefulResult.Success) {
                if (result.data != null) {
                    val productModel =
                        productRepository.getProductById(result.data!!.productId ?: "").data
                    pair = Pair(productModel, result.data)
                }
                hideLoading()
                liveData.value = pair
            } else
                handleError(result.errorModel)
        }
        return liveData
    }

    fun getProductItemsBySerials(serials: List<String>): LiveData<List<ProductItemModel>> {
        showLoading()
        val liveData = LiveEvent<List<ProductItemModel>>()
        safeLauncher {
            val result =
                productItemRepository.getProductItemsBySerials(serials)
            hideLoading()
            if (result is StatefulResult.Success)
                liveData.value = result.data ?: listOf()
            else
                handleError(result.errorModel)
        }
        return liveData
    }

    fun getProductItems(): LiveData<List<ProductItemModel>> {
        showLoading()
        val liveData = LiveEvent<List<ProductItemModel>>()
        safeLauncher {
            val result =
                productItemRepository.getProductItems(productModel?.id ?: "")
            hideLoading()
            if (result is StatefulResult.Success)
                liveData.value = result.data ?: listOf()
            else
                handleError(result.errorModel)
        }
        return liveData
    }
}