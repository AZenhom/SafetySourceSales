package com.safetysource.appadmin.ui.products.products_List

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.safetysource.appadmin.databinding.ActivityProductsListBinding
import com.safetysource.appadmin.ui.product_items.items_statistics.ProductItemsStatisticsActivity
import com.safetysource.appadmin.ui.products.create_edit_product.CreateEditProductActivity
import com.safetysource.core.base.BaseActivity
import com.safetysource.core.utils.getScreenWidth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsListActivity : BaseActivity<ActivityProductsListBinding, ProductsListViewModel>() {

    companion object {
        const val PRODUCT_CATEGORY_ID = "PRODUCT_CATEGORY_ID"
        fun getIntent(
            context: Context,
            productCategoryId: String
        ) =
            Intent(context, ProductsListActivity::class.java).apply {
                putExtra(PRODUCT_CATEGORY_ID, productCategoryId)
            }
    }

    override val viewModel: ProductsListViewModel by viewModels()
    override val binding by viewBinding(ActivityProductsListBinding::inflate)

    lateinit var adapter: ProductAdapter

    override fun onActivityCreated() {
        initViews()
    }

    private fun initViews() {
        with(binding) {
            val totalMargins = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._16sdp) * 3
            val leftScreenSize = getScreenWidth(this@ProductsListActivity) - totalMargins
            adapter = ProductAdapter(leftScreenSize / 2,
                onItemClicked = {
                    startActivity(
                        ProductItemsStatisticsActivity.getIntent(this@ProductsListActivity, it)
                    )
                },
                onEditClicked = {
                    startActivity(
                        CreateEditProductActivity.getIntent(
                            this@ProductsListActivity,
                            it.productCategoryId ?: "",
                            it
                        )
                    )
                }
            )
            rvProducts.adapter = adapter

            toolbar.setNavigationOnClickListener { onBackPressed() }
            fabAdd.setOnClickListener {
                startActivity(
                    CreateEditProductActivity.getIntent(
                        this@ProductsListActivity,
                        viewModel.productCategoryId ?: return@setOnClickListener
                    )
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getProducts().observe(this) {
            adapter.submitList(it)
        }
    }
}