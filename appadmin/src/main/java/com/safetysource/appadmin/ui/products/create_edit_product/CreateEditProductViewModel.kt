package com.safetysource.appadmin.ui.products.create_edit_product

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.ProductModel
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.ProductRepository
import com.safetysource.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateEditProductViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private lateinit var adminId: String

    private val productCategoryId: String? =
        savedStateHandle[CreateEditProductActivity.PRODUCT_CATEGORY_ID]
    val productModel: ProductModel? =
        savedStateHandle[CreateEditProductActivity.PRODUCT_TO_EDIT]

    private var productId: String = if (productModel != null) productModel.id ?: ""
    else productRepository.getNewProductId()

    init {
        safeLauncher {
            adminId = userRepository.getUserId() ?: ""
        }
    }

    fun uploadProductImageAndGetUrl(fileUri: Uri): LiveData<String?> {
        val liveData = LiveEvent<String?>()
        safeLauncher {
            showLoading()
            val result =
                productRepository.uploadProductImageAndGetUrl(productId, fileUri)
            hideLoading()
            if (result is StatefulResult.Success) {
                liveData.value = result.data
            } else
                handleError(result.errorModel)
        }
        return liveData
    }

    fun createProduct(
        name: String,
        imgUrl: String,
        wholesalePrice: Float,
        commissionValue: Float,
    ): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            showLoading()
            val product = ProductModel(
                id = productId,
                name = name,
                imgUrl = imgUrl,
                wholesalePrice = wholesalePrice,
                productCategoryId = productCategoryId,
                commissionValue = commissionValue,
                lastUpdatedByAdminId = adminId
            )
            val response = productRepository.createUpdateProduct(product)
            hideLoading()
            if (response is StatefulResult.Success)
                liveData.value = true
            else
                handleError(response.errorModel)
        }
        return liveData
    }
}