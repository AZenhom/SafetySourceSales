package com.safetysource.appadmin.ui.products.products_List

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.safetysource.appadmin.databinding.ActivityProductsListBinding
import com.safetysource.appadmin.ui.products.create_edit_product.CreateEditProductActivity
import com.safetysource.core.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsListActivity : BaseActivity<ActivityProductsListBinding, ProductsListViewModel>() {

    companion object {
        const val PRODUCT_CATEGORY_ID = "PRODUCT_CATEGORY_ID"
        fun getIntent(
            context: Context,
            productCategoryId: String
        ) =
            Intent(context, CreateEditProductActivity::class.java).apply {
                putExtra(PRODUCT_CATEGORY_ID, productCategoryId)
            }
    }

    override val viewModel: ProductsListViewModel by viewModels()
    override val binding by viewBinding(ActivityProductsListBinding::inflate)

    override fun onActivityCreated() {

    }
}