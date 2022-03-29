package com.safetysource.appretailer.ui.product_details

import androidx.lifecycle.SavedStateHandle
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.ProductItemModel
import com.safetysource.data.model.ProductModel
import com.safetysource.data.repository.ProductItemRepository
import javax.inject.Inject

class ProductDetailsViewModel @Inject constructor(
    private val productItemRepository: ProductItemRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val productItemModel: ProductItemModel? =
        savedStateHandle[ProductDetailsActivity.PRODUCT_ITEM_MODEL]

    private val productModel: ProductModel? =
        savedStateHandle[ProductDetailsActivity.PRODUCT_MODEL]
}