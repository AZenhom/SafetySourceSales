package com.safetysource.appadmin.ui.product_categories.categories_list

import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.ProductCategoryModel
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.ProductCategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductCategoriesViewModel @Inject constructor(
    private val productCategoryRepository: ProductCategoryRepository
) : BaseViewModel() {

    fun getProductCategories(): LiveData<List<ProductCategoryModel>> {
        showLoading()
        val liveData = LiveEvent<List<ProductCategoryModel>>()
        safeLauncher {
            val result = productCategoryRepository.getProductCategories()
            if (result is StatefulResult.Success)
                liveData.value = result.data ?: listOf()
            else
                handleError(result.errorModel)
            hideLoading()
        }
        return liveData
    }
}