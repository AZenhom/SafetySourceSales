package com.safetysource.appretailer.ui.offers.offer_details

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.safetysource.appretailer.databinding.ActivityOfferDetailsBinding
import com.safetysource.core.base.BaseActivity
import com.safetysource.data.model.OfferModel
import com.safetysource.data.model.ProductCategoryModel
import com.safetysource.data.model.ProductModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OfferDetailsActivity : BaseActivity<ActivityOfferDetailsBinding, OfferDetailsViewModel>() {

    companion object {
        const val OFFER_MODEL = "OFFER_MODEL"
        fun getIntent(context: Context, offerModel: OfferModel? = null) =
            Intent(context, OfferDetailsActivity::class.java).apply {
                putExtra(OFFER_MODEL, offerModel)
            }
    }

    override val viewModel: OfferDetailsViewModel by viewModels()
    override val binding by viewBinding(ActivityOfferDetailsBinding::inflate)

    private var categoriesList: List<ProductCategoryModel> = emptyList()
    private var productsList: List<ProductModel> = emptyList()

    override fun onActivityCreated() {
        initViews()
        initObservers()
    }

    private fun initViews() {

    }

    private fun initObservers() {

    }
}