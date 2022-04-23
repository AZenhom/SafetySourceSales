package com.safetysource.appretailer.ui.product_item

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.safetysource.appretailer.databinding.ActivityProductItemDetailsBinding
import com.safetysource.core.R
import com.safetysource.core.base.BaseActivity
import com.safetysource.core.ui.makeVisible
import com.safetysource.data.model.ProductItemModel
import com.safetysource.data.model.ProductItemState
import com.safetysource.data.model.ProductModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class ProductItemDetailsActivity :
    BaseActivity<ActivityProductItemDetailsBinding, ProductItemDetailsViewModel>() {

    companion object {

        const val PRODUCT_ITEM_MODEL = "PRODUCT_ITEM_MODEL"

        fun getIntent(context: Context, productItemModel: ProductItemModel) =
            Intent(context, ProductItemDetailsActivity::class.java).apply {
                putExtra(PRODUCT_ITEM_MODEL, productItemModel)
            }
    }

    override val viewModel: ProductItemDetailsViewModel by viewModels()
    override val binding: ActivityProductItemDetailsBinding by viewBinding(
        ActivityProductItemDetailsBinding::inflate
    )

    private var productModel: ProductModel? = null

    override fun onActivityCreated() {
        initViews()
        getProductDetails()
    }

    private fun initViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
            btnSell.setOnClickListener {
                viewModel.sellUnsellProductItem(productModel ?: return@setOnClickListener, true)
                    .observe(this@ProductItemDetailsActivity) {
                        showSuccessMsg(getString(R.string.product_sold_successfully))
                        finish()
                    }
            }
            btnUnsell.setOnClickListener {
                viewModel.sellUnsellProductItem(productModel ?: return@setOnClickListener, false)
                    .observe(this@ProductItemDetailsActivity) {
                        showSuccessMsg(getString(R.string.product_unsold_successfully))
                        finish()
                    }
            }
        }
    }

    private fun getProductDetails() {
        viewModel.getProduct().observe(this) {
            productModel = it
            bindDataToViews()
        }
    }

    private fun bindDataToViews() {
        if (productModel == null)
            return

        with(binding) {
            // product image
            if (!productModel!!.imgUrl.isNullOrEmpty())
                Picasso.get()
                    .load(productModel!!.imgUrl)
                    .error(R.drawable.ic_image_placeholder)
                    .into(ivProductImage)

            // Name
            tvProductName.text = productModel!!.name

            // Price
            tvWholesomePrice.text = "${productModel!!.wholesalePrice} ${getString(R.string.egyptian_pound)}"

            // Commission
            tvCommission.text = "${productModel!!.commissionValue} ${getString(R.string.egyptian_pound)}"

            // Product Item Status
            tvProductItemStatus.text = when (viewModel.productItemModel?.state) {
                ProductItemState.NOT_SOLD_YET -> getString(R.string.not_sold_yet)
                ProductItemState.SOLD -> getString(R.string.sold)
                ProductItemState.PENDING_UNSELLING -> getString(R.string.pending_unselling)
                else -> ""
            }

            // Serial
            tvSerial.text = viewModel.productItemModel?.serial

            // Sell-UnSell Status
            if (viewModel.productItemModel?.state == ProductItemState.SOLD && viewModel.isEligibleForUnsell)
                btnUnsell.makeVisible()
            if (viewModel.productItemModel?.state == ProductItemState.NOT_SOLD_YET)
                btnSell.makeVisible()
        }
    }
}