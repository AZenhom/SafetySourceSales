package com.safetysource.admin.ui.product_categories.create_edit_category

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.ProductCategoryModel
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.ProductCategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateEditProductCategoryViewModel @Inject constructor(
    private val productCategoryRepository: ProductCategoryRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val productCategoryModel: ProductCategoryModel? =
        savedStateHandle[CreateEditProductCategoryActivity.CATEGORY_TO_EDIT]

    private var categoryId: String = if (productCategoryModel != null) productCategoryModel.id ?: ""
    else productCategoryRepository.getNewProductCategoryId()

    fun uploadProductCategoryImageAndGetUrl(fileUri: Uri): LiveData<String> {
        val liveData = LiveEvent<String>()
        safeLauncher {
            showLoading()
            val result =
                productCategoryRepository.uploadProductCategoryImageAndGetUrl(categoryId, fileUri)
            hideLoading()
            if (result is StatefulResult.Success) {
                liveData.value = result.data ?: ""
            } else
                handleError(result.errorModel)
        }
        return liveData
    }

    fun createProductCategory(
        imageUrl: String,
        name: String,
        rank: Int
    ): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            showLoading()
            val category = ProductCategoryModel(
                id = categoryId,
                name = name,
                imgUrl = imageUrl,
                rank = rank
            )
            val response = productCategoryRepository.createUpdateProductCategory(category)
            hideLoading()
            if (response is StatefulResult.Success)
                liveData.value = true
            else
                handleError(response.errorModel)
        }
        return liveData
    }
}