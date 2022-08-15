package com.safetysource.admin.ui.products.products_List

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.ProductModel
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductsListViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val productCategoryId: String? =
        savedStateHandle[ProductsListActivity.PRODUCT_CATEGORY_ID]

    fun getProducts(): LiveData<List<ProductModel>> {
        showLoading()
        val liveData = LiveEvent<List<ProductModel>>()
        safeLauncher {
            val result = productRepository.getCategoryProducts(productCategoryId ?: "")
            hideLoading()
            if (result is StatefulResult.Success)
                liveData.value = result.data ?: listOf()
            else
                handleError(result.errorModel)
        }
        return liveData
    }
}