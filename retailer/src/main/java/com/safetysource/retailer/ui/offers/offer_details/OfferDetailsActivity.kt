package com.safetysource.retailer.ui.offers.offer_details

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import androidx.activity.viewModels
import com.safetysource.core.R
import com.safetysource.core.base.BaseActivity
import com.safetysource.core.ui.dialogs.InfoDialog
import com.safetysource.core.ui.makeVisible
import com.safetysource.core.ui.setIsVisible
import com.safetysource.core.utils.getDateText
import com.safetysource.data.model.OfferModel
import com.safetysource.retailer.databinding.ActivityOfferDetailsBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

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

    override fun onResume() {
        super.onResume()
        refreshButtonsStates()
    }

    private fun initViews() {
        fillUIWithData()
        with(binding) {
            toolbar.setNavigationOnClickListener { onBackPressed() }
            btnSubscribe.setOnClickListener {
                viewModel.createOfferSubscription().observe(this@OfferDetailsActivity) {
                    showSuccessMsg(getString(R.string.subscribed_in_offer_successfully))
                    refreshButtonsStates()
                }
            }
            btnClaimOrRemove.setOnClickListener {
                val now = Calendar.getInstance().time
                if (now.before(viewModel.offerModel?.expiresAt)) {
                    showWarningMsg(getString(R.string.you_can_claim_after_expiry))
                    return@setOnClickListener
                }
                viewModel.claimOffer().observe(this@OfferDetailsActivity) {
                    if (it > 0f) {
                        showSuccessMsg(getString(R.string.offer_commission_claimed_successfully))
                        finish()
                    } else
                        showOfferRemovalDialog()
                }
            }
        }
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

    private fun refreshButtonsStates() {
        viewModel.offerModel?.let {
            with(binding) {
                val now = Calendar.getInstance().time
                btnSubscribe.setIsVisible(
                    it.subscribedOfferModel == null
                            && now.after(it.startsAt) && now.before(it.expiresAt)
                )

                btnClaimOrRemove.setIsVisible(it.subscribedOfferModel != null)
                btnClaimOrRemove.backgroundTintList = if (now.after(it.expiresAt))
                    ColorStateList.valueOf(resources.getColor(R.color.colorPrimary, null))
                else
                    ColorStateList.valueOf(resources.getColor(R.color.grey_light2, null))
            }
        }
    }

    private fun initObservers() {
        with(viewModel) {
            // Sold Count on Claim Button
            sellingCountLiveData.observe(this@OfferDetailsActivity) { sold ->
                val neededSellCount = offerModel?.neededSellCount ?: return@observe
                val text = if (offerModel?.canRepeat == true) {
                    if (sold > neededSellCount) {
                        "(${sold / neededSellCount}) + ${sold % neededSellCount}/$neededSellCount"
                    } else
                        "$sold/$neededSellCount"
                } else {
                    "${minOf(neededSellCount, sold)}/$neededSellCount"
                }
                binding.btnClaimOrRemove.text = getString(R.string.claim, text)
            }
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

    private fun showOfferRemovalDialog() {
        var infoDialog: InfoDialog? = null
        infoDialog = InfoDialog(
            context = this,
            imageRes = R.drawable.warning,
            message = getString(R.string.insufficient_sell_count),
            confirmText = getString(R.string.remove_offer),
            onConfirm = { infoDialog?.dismiss(); removeOffer() },
            isCancelable = true
        )
        infoDialog.show(supportFragmentManager, InfoDialog.TAG)
    }

    private fun removeOffer() {
        viewModel.deleteOfferSubscription().observe(this) {
            if (it) {
                showSuccessMsg(getString(R.string.offer_subscription_deleted_successfully))
                finish()
            }
        }
    }
}