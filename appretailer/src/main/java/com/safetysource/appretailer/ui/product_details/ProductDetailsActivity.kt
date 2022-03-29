package com.safetysource.appretailer.ui.product_details

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.safetysource.appretailer.databinding.ActivityProductDetailsBinding
import com.safetysource.core.base.BaseActivity
import com.safetysource.data.model.ProductItemModel
import com.safetysource.data.model.ProductModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailsActivity :
    BaseActivity<ActivityProductDetailsBinding, ProductDetailsViewModel>() {

    companion object {

        const val PRODUCT_ITEM_MODEL = "PRODUCT_ITEM_MODEL"
        const val PRODUCT_MODEL = "PRODUCT_MODEL"

        fun getIntent(
            context: Context,
            productItemModel: ProductItemModel,
            productModel: ProductModel
        ) = Intent(context, ProductDetailsActivity::class.java).apply {
            putExtra(PRODUCT_ITEM_MODEL, productItemModel)
            putExtra(PRODUCT_MODEL, productModel)
        }
    }

    override val viewModel: ProductDetailsViewModel by viewModels()
    override val binding: ActivityProductDetailsBinding by viewBinding(ActivityProductDetailsBinding::inflate)

    override fun onActivityCreated() {

    }
}