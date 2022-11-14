package com.safetysource.admin.ui.offers.offer_details

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.safetysource.admin.databinding.ActivityOfferDetailsBinding
import com.safetysource.core.R
import com.safetysource.core.base.BaseActivity
import com.safetysource.core.ui.makeVisible
import com.safetysource.core.utils.getDateText
import com.safetysource.data.model.OfferModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("SetTextI18n")
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

    override fun onActivityCreated() {
        initViews()
        initObservers()
    }

    private fun initViews() {
        registerToolBarOnBackPressed(binding.toolbar)
        fillUIWithData()
    }

    private fun fillUIWithData() {
        if (viewModel.offerModel == null) {
            showErrorMsg(getString(R.string.something_went_wrong))
            finish()
            return
        }
        viewModel.offerModel?.let {
            with(binding) {
                // Image
                Picasso.get()
                    .load(it.imgUrl)
                    .error(R.drawable.ic_image_placeholder)
                    .into(ivOfferImage)
                // Text
                tvOfferText.text = it.text
                // Needed Sell Count
                tvSellCount.text = getString(R.string.pieces_count, it.neededSellCount.toString())
                // Claim Value
                tvClaimValue.text = "${it.valPerRepeat} ${getString(R.string.egyptian_pound)}"
                // Expires At
                tvExpiresAt.text = it.expiresAt?.time?.getDateText("EE, d MMM yyyy, hh:mm aa")
            }
        }
    }

    private fun initObservers() {
        with(viewModel) {
            // Exclusive Category
            exclusiveCategoryLiveData.observe(this@OfferDetailsActivity) {
                it?.let {
                    with(binding) {
                        lblExclusiveCategory.makeVisible()
                        tvExclusiveCategory.makeVisible()
                        tvExclusiveCategory.text = it.name
                    }
                }
            }
            // Exclusive Product
            exclusiveProductLiveData.observe(this@OfferDetailsActivity) {
                it?.let {
                    with(binding) {
                        lblExclusiveProduct.makeVisible()
                        tvExclusiveProduct.makeVisible()
                        tvExclusiveProduct.text = it.name
                    }
                }
            }
        }
    }
}