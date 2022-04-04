package com.safetysource.appretailer.ui.product_items

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.safetysource.appretailer.databinding.ActivityProductItemDetailsBinding
import com.safetysource.core.base.BaseActivity
import com.safetysource.data.model.ProductItemModel
import com.safetysource.data.model.ProductModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductItemDetailsActivity :
    BaseActivity<ActivityProductItemDetailsBinding, ProductItemDetailsViewModel>() {

    companion object {

        const val PRODUCT_ITEM_MODEL = "PRODUCT_ITEM_MODEL"

        fun getIntent(
            context: Context,
            productItemModel: ProductItemModel,
            productModel: ProductModel
        ) = Intent(context, ProductItemDetailsActivity::class.java).apply {
            putExtra(PRODUCT_ITEM_MODEL, productItemModel)
        }
    }

    override val viewModel: ProductItemDetailsViewModel by viewModels()
    override val binding: ActivityProductItemDetailsBinding by viewBinding(ActivityProductItemDetailsBinding::inflate)

    override fun onActivityCreated() {

    }
}