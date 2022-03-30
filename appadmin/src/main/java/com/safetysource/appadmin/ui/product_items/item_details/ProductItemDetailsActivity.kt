package com.safetysource.appadmin.ui.product_items.item_details

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.safetysource.appadmin.databinding.ActivityProductItemDetailsBinding
import com.safetysource.core.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductItemDetailsActivity : BaseActivity<ActivityProductItemDetailsBinding, ProductItemDetailsViewModel>() {

    companion object {
        const val SERIAL = "SERIAL"
        fun getIntent(
            context: Context,
            serial: String
        ) =
            Intent(context, ProductItemDetailsActivity::class.java).apply {
                putExtra(SERIAL, serial)
            }
    }

    override val viewModel: ProductItemDetailsViewModel by viewModels()
    override val binding by viewBinding(ActivityProductItemDetailsBinding::inflate)

    override fun onActivityCreated() {

    }
}