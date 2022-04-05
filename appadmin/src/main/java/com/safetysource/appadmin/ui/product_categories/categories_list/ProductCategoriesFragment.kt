package com.safetysource.appadmin.ui.product_categories.categories_list

import androidx.fragment.app.viewModels
import com.safetysource.appadmin.R
import com.safetysource.appadmin.databinding.FragmentProductCategoriesBinding
import com.safetysource.appadmin.ui.product_categories.create_edit_category.CreateEditProductCategoryActivity
import com.safetysource.appadmin.ui.products.products_List.ProductsListActivity
import com.safetysource.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductCategoriesFragment :
    BaseFragment<FragmentProductCategoriesBinding, ProductCategoriesViewModel>(R.layout.fragment_product_categories) {

    override val viewModel: ProductCategoriesViewModel by viewModels()
    override val binding by viewBinding(FragmentProductCategoriesBinding::bind)

    private lateinit var adapter: ProductCategoryAdapter

    override fun onViewCreated() {
        initViews()
    }

    private fun initViews() {
        with(binding) {
            adapter = ProductCategoryAdapter(
                onItemClicked = {
                    startActivity(
                        ProductsListActivity.getIntent(
                            requireContext(),
                            it.id ?: return@ProductCategoryAdapter
                        )
                    )
                }
            )
            rvProductCategories.adapter = adapter

            fabAdd.setOnClickListener {
                startActivity(CreateEditProductCategoryActivity.getIntent(requireContext()))
            }

        }
    }

    override fun onResume() {
        super.onResume()
        getProductCategoriesList()
    }

    private fun getProductCategoriesList() {
        adapter.submitList(emptyList())
        viewModel.getProductCategories().observe(this) {
            adapter.submitList(it)
        }
    }
}