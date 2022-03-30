package com.safetysource.appadmin.ui.product_categories.categories_list

import androidx.fragment.app.viewModels
import com.safetysource.appadmin.R
import com.safetysource.appadmin.databinding.FragmentProductCategoriesBinding
import com.safetysource.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductCategoriesFragment :
    BaseFragment<FragmentProductCategoriesBinding, ProductCategoriesViewModel>(R.layout.fragment_product_categories) {

    override val viewModel: ProductCategoriesViewModel by viewModels()
    override val binding by viewBinding(FragmentProductCategoriesBinding::bind)

    override fun onViewCreated() {

    }
}