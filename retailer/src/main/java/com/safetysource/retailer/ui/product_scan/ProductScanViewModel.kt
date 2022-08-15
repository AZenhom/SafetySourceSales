package com.safetysource.retailer.ui.product_scan

import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.ProductItemModel
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.ProductItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductScanViewModel @Inject constructor(
    private val productItemRepository: ProductItemRepository
) : BaseViewModel() {

    fun getProductItemBySerial(serial: String): LiveData<ProductItemModel?> {
        showLoading()
        val liveData = LiveEvent<ProductItemModel?>()
        safeLauncher {
            val result =
                productItemRepository.getProductItemBySerial(serial)
            hideLoading()
            if (result is StatefulResult.Success)
                liveData.value = result.data
            else
                handleError(result.errorModel)
        }
        return liveData
    }
}