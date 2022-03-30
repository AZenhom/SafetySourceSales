package com.safetysource.appadmin.ui.product_items.item_details

import androidx.lifecycle.SavedStateHandle
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.repository.ProductItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductItemDetailsViewModel @Inject constructor(
    private val productItemRepository: ProductItemRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val serial: String? =
        savedStateHandle[ProductItemDetailsActivity.SERIAL]
}