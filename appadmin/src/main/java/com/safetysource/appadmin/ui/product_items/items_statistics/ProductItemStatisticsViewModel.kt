package com.safetysource.appadmin.ui.product_items.items_statistics

import androidx.lifecycle.SavedStateHandle
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.repository.ProductItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductItemStatisticsViewModel @Inject constructor(
    private val productItemRepository: ProductItemRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val productId: String? =
        savedStateHandle[ProductItemsStatisticsActivity.PRODUCT_ID]
}