package com.safetysource.appadmin.ui.products.products_List

import androidx.lifecycle.SavedStateHandle
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductsListViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val productCategoryId: String? =
        savedStateHandle[ProductsListActivity.PRODUCT_CATEGORY_ID]
}