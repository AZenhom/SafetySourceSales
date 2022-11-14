package com.safetysource.admin.ui.product_items.item_details

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.safetysource.admin.databinding.ActivityProductItemDetailsBinding
import com.safetysource.core.R
import com.safetysource.core.base.BaseActivity
import com.safetysource.core.ui.adapters.TransactionsAdapter
import com.safetysource.core.ui.dialogs.InfoDialog
import com.safetysource.core.utils.toString
import com.safetysource.data.model.*
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductItemDetailsActivity :
    BaseActivity<ActivityProductItemDetailsBinding, ProductItemDetailsViewModel>() {

    companion object {
        const val PRODUCT_MODEL = "PRODUCT_MODEL"
        const val PRODUCT_ITEM_MODEL = "PRODUCT_ITEM_MODEL"

        fun getIntent(
            context: Context,
            productModel: ProductModel,
            productItemModel: ProductItemModel
        ) =
            Intent(context, ProductItemDetailsActivity::class.java).apply {
                putExtra(PRODUCT_MODEL, productModel)
                putExtra(PRODUCT_ITEM_MODEL, productItemModel)
            }
    }

    override val viewModel: ProductItemDetailsViewModel by viewModels()
    override val binding by viewBinding(ActivityProductItemDetailsBinding::inflate)

    private lateinit var adapter: TransactionsAdapter

    override fun onActivityCreated() {
        initViews()
        getTransactions()
    }

    private fun initViews() {
        adapter = TransactionsAdapter {
            if (it.type == TransactionType.UNSELLING && it.isUnsellingApproved == false)
                showUnsellingApprovalDialog(it)
        }
        with(binding) {
            registerToolBarOnBackPressed(toolbar)
            rvTransactions.adapter = adapter
            tvFilterSummary.text =
                viewModel.transactionFilterModel.toString(this@ProductItemDetailsActivity)
        }

    }

    private fun showUnsellingApprovalDialog(transactionModel: TransactionModel) {
        var infoDialog: InfoDialog? = null
        infoDialog = InfoDialog(
            context = this,
            imageRes = R.drawable.warning,
            message = getString(R.string.approve_unselling_question),
            confirmText = getString(R.string.approve),
            onConfirm = { infoDialog?.dismiss(); approveUnsellingProcess(transactionModel) },
            isCancelable = true
        )
        infoDialog.show(supportFragmentManager, InfoDialog.TAG)
    }

    private fun getTransactions() {
        adapter.submitList(emptyList())
        viewModel.getTransactions().observe(this) {
            adapter.submitList(it)
            bindDataToViews()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindDataToViews() {
        with(binding) {
            // product image
            if (!viewModel.productModel?.imgUrl.isNullOrEmpty())
                Picasso.get()
                    .load(viewModel.productModel?.imgUrl)
                    .error(R.drawable.ic_image_placeholder)
                    .into(ivProductImage)

            // Name
            tvProductName.text = viewModel.productModel?.name

            // Price
            tvWholesomePrice.text =
                "${viewModel.productModel?.wholesalePrice} ${getString(R.string.egyptian_pound)}"

            // Commission
            tvCommission.text =
                "${viewModel.productModel?.commissionValue} ${getString(R.string.egyptian_pound)}"

            // Product Item Status
            tvProductItemStatus.text = when (viewModel.productItemModel?.state) {
                ProductItemState.NOT_SOLD_YET -> getString(R.string.not_sold_yet)
                ProductItemState.SOLD -> getString(R.string.sold)
                ProductItemState.PENDING_UNSELLING -> getString(R.string.pending_unselling)
                else -> ""
            }

            // Serial
            tvSerial.text = viewModel.productItemModel?.serial
        }
    }

    private fun approveUnsellingProcess(transactionModel: TransactionModel) {
        viewModel.approveUnsellTransaction(transactionModel).observe(this) {
            getTransactions()
        }
    }
}