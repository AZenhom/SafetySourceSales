package com.safetysource.appadmin.ui.retailers.retailer_details

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.safetysource.appadmin.databinding.ActivityRetailerDetailsBinding
import com.safetysource.core.base.BaseActivity
import com.safetysource.data.model.RetailerModel

class RetailerDetailsActivity :
    BaseActivity<ActivityRetailerDetailsBinding, RetailerDetailsViewModel>() {

    companion object {
        const val RETAILER_MODEL = "RETAILER_MODEL"
        fun getIntent(
            context: Context,
            retailerModel: RetailerModel
        ) =
            Intent(context, RetailerDetailsActivity::class.java).apply {
                putExtra(RETAILER_MODEL, retailerModel)
            }
    }

    override val viewModel: RetailerDetailsViewModel by viewModels()
    override val binding by viewBinding(ActivityRetailerDetailsBinding::inflate)

    override fun onActivityCreated() {

    }
}