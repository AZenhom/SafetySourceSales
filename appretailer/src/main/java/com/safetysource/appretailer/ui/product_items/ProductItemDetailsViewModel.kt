package com.safetysource.appretailer.ui.product_items

import androidx.lifecycle.SavedStateHandle
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.ProductItemModel
import com.safetysource.data.model.ProductModel
import com.safetysource.data.repository.ProductItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductItemDetailsViewModel @Inject constructor(
    private val productItemRepository: ProductItemRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val productItemModel: ProductItemModel? =
        savedStateHandle[ProductItemDetailsActivity.PRODUCT_ITEM_MODEL]

    private val productModel: ProductModel? =
        savedStateHandle[ProductItemDetailsActivity.PRODUCT_MODEL]
}