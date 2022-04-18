package com.safetysource.appadmin.ui.product_categories.categories_list

import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.viewModels
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.safetysource.core.R
import com.safetysource.appadmin.databinding.FragmentProductCategoriesBinding
import com.safetysource.appadmin.ui.product_categories.create_edit_category.CreateEditProductCategoryActivity
import com.safetysource.appadmin.ui.product_items.item_details.ProductItemDetailsActivity
import com.safetysource.appadmin.ui.products.products_List.ProductsListActivity
import com.safetysource.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductCategoriesFragment :
    BaseFragment<FragmentProductCategoriesBinding, ProductCategoriesViewModel>(com.safetysource.appadmin.R.layout.fragment_product_categories) {

    override val viewModel: ProductCategoriesViewModel by viewModels()
    override val binding by viewBinding(FragmentProductCategoriesBinding::bind)

    private lateinit var adapter: ProductCategoryAdapter

    private val barcodeLauncher: ActivityResultLauncher<ScanOptions> = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        try {
            if (result.contents == null)
                showErrorMsg(getString(R.string.scan_cancelled))
            else
                searchSerial(result.contents)
        } catch (e: Exception) {
            e.printStackTrace()
            showErrorMsg(getString(R.string.something_went_wrong))
        }
    }

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

            btnQrScan.setOnClickListener {
                barcodeLauncher.launch(ScanOptions())
            }

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

    private fun searchSerial(serial: String) {
        viewModel.getProductItemBySerial(serial).observe(this) { productItem ->
            if (productItem == null)
                showErrorMsg(getString(R.string.serial_not_associated_with_any_product))
            else
                startActivity(
                    ProductItemDetailsActivity.getIntent(
                        requireContext(),
                        viewModel.productModel ?: return@observe,
                        productItem
                    )
                )
        }
    }
}