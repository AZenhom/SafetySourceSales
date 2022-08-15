package com.safetysource.admin.ui.product_categories.categories_list

import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.ProductCategoryModel
import com.safetysource.data.model.ProductItemModel
import com.safetysource.data.model.ProductModel
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.ProductCategoryRepository
import com.safetysource.data.repository.ProductItemRepository
import com.safetysource.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductCategoriesViewModel @Inject constructor(
    private val productCategoryRepository: ProductCategoryRepository,
    private val productItemRepository: ProductItemRepository,
    private val productRepository: ProductRepository,
) : BaseViewModel() {

    var productModel: ProductModel? = null

    fun getProductCategories(): LiveData<List<ProductCategoryModel>> {
        showLoading()
        val liveData = LiveEvent<List<ProductCategoryModel>>()
        safeLauncher {
            val result = productCategoryRepository.getProductCategories()
            hideLoading()
            if (result is StatefulResult.Success)
                liveData.value = result.data ?: listOf()
            else
                handleError(result.errorModel)
        }
        return liveData
    }

    fun getProductItemBySerial(serial: String): LiveData<ProductItemModel?> {
        val liveData = LiveEvent<ProductItemModel?>()
        safeLauncher {
            val result =
                productItemRepository.getProductItemBySerial(serial)
            if (result is StatefulResult.Success) {
                if (result.data != null) {
                    productModel =
                        productRepository.getProductById(result.data!!.productId ?: "").data
                }
                hideLoading()
                liveData.value = result.data
            } else
                handleError(result.errorModel)
        }
        return liveData
    }

}