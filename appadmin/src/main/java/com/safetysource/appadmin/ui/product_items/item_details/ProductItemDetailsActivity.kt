package com.safetysource.appadmin.ui.product_items.item_details

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.safetysource.appadmin.databinding.ActivityProductItemDetailsBinding
import com.safetysource.core.base.BaseActivity
import com.safetysource.data.model.ProductItemModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductItemDetailsActivity : BaseActivity<ActivityProductItemDetailsBinding, ProductItemDetailsViewModel>() {

    companion object {
        const val PRODUCT_ITEM_MODEL = "PRODUCT_ITEM_MODEL"
        fun getIntent(
            context: Context,
            productItemModel: ProductItemModel
        ) =
            Intent(context, ProductItemDetailsActivity::class.java).apply {
                putExtra(PRODUCT_ITEM_MODEL, productItemModel)
            }
    }

    override val viewModel: ProductItemDetailsViewModel by viewModels()
    override val binding by viewBinding(ActivityProductItemDetailsBinding::inflate)

    override fun onActivityCreated() {

    }
}