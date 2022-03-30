package com.safetysource.appadmin.ui.product_items.items_statistics

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.safetysource.appadmin.databinding.ActivityProductItemsStatisticsBinding
import com.safetysource.core.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductItemsStatisticsActivity : BaseActivity<ActivityProductItemsStatisticsBinding, ProductItemStatisticsViewModel>() {

    companion object {
        const val PRODUCT_ID = "PRODUCT_ID"
        fun getIntent(
            context: Context,
            productId: String
        ) =
            Intent(context, ProductItemsStatisticsActivity::class.java).apply {
                putExtra(PRODUCT_ID, productId)
            }
    }

    override val viewModel: ProductItemStatisticsViewModel by viewModels()
    override val binding by viewBinding(ActivityProductItemsStatisticsBinding::inflate)

    override fun onActivityCreated() {

    }
}