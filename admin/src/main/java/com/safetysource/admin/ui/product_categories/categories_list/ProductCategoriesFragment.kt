package com.safetysource.admin.ui.product_categories.categories_list

import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.viewModels
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.safetysource.core.R
import com.safetysource.admin.databinding.FragmentProductCategoriesBinding
import com.safetysource.admin.ui.product_categories.create_edit_category.CreateEditProductCategoryActivity
import com.safetysource.admin.ui.product_items.item_details.ProductItemDetailsActivity
import com.safetysource.admin.ui.products.products_List.ProductsListActivity
import com.safetysource.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductCategoriesFragment :
    BaseFragment<FragmentProductCategoriesBinding, ProductCategoriesViewModel>(com.safetysource.admin.R.layout.fragment_product_categories) {

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
                },
                onEditClicked = {
                    startActivity(CreateEditProductCategoryActivity.getIntent(requireContext(), it))
                }
            )
            rvProductCategories.adapter = adapter

            btnQrScan.setOnClickListener {
                val scanOptions = ScanOptions()
                scanOptions.setOrientationLocked(false)
                barcodeLauncher.launch(scanOptions)
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
        viewModel.getProductItemBySerial(serial).observe(this) { pair ->
            if (pair.first == null || pair.second == null)
                showErrorMsg(getString(R.string.serial_not_associated_with_any_product))
            else
                startActivity(
                    ProductItemDetailsActivity.getIntent(
                        requireContext(),
                        pair.first!!,
                        pair.second!!
                    )
                )
        }
    }
}