package com.safetysource.admin.ui.retailers.retailer_details

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.InputType
import androidx.activity.viewModels
import com.safetysource.admin.databinding.ActivityRetailerDetailsBinding
import com.safetysource.admin.ui.product_items.item_details.ProductItemDetailsActivity
import com.safetysource.core.R
import com.safetysource.core.base.BaseActivity
import com.safetysource.core.ui.adapters.TransactionsAdapter
import com.safetysource.core.ui.dialogs.EditTextDialog
import com.safetysource.core.ui.dialogs.InfoDialog
import com.safetysource.core.ui.makeVisible
import com.safetysource.core.ui.sheets.OfferSerialsSheet
import com.safetysource.core.utils.convertArabicNumbersIfExist
import com.safetysource.core.utils.getDigit
import com.safetysource.core.utils.toString
import com.safetysource.data.model.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RetailerDetailsActivity :
    BaseActivity<ActivityRetailerDetailsBinding, RetailerDetailsViewModel>() {

    companion object {
        const val TEAM_MODEL = "TEAM_MODEL"
        const val RETAILER_MODEL = "RETAILER_MODEL"
        fun getIntent(
            context: Context,
            teamModel: TeamModel,
            retailerModel: RetailerModel
        ) =
            Intent(context, RetailerDetailsActivity::class.java).apply {
                putExtra(TEAM_MODEL, teamModel)
                putExtra(RETAILER_MODEL, retailerModel)
            }
    }

    override val viewModel: RetailerDetailsViewModel by viewModels()
    override val binding by viewBinding(ActivityRetailerDetailsBinding::inflate)

    private lateinit var adapter: TransactionsAdapter

    override fun onActivityCreated() {
        initViews()
        getData()
        initObservers()
    }

    private fun getData() {
        getTransactions()
        getRetailerReport()
    }

    private fun initViews() {
        adapter = TransactionsAdapter(
            onItemClicked = {
                if (it.type == TransactionType.UNSELLING && it.isUnsellingApproved == false)
                    showUnsellingApprovalDialog(it)
            },
            onMultipleSerialsClicked = { showOfferSerialsSheet(it) }
        )
        with(binding) {
            registerToolBarOnBackPressed(toolbar)
            rvTransactions.adapter = adapter
            tvFilterSummary.text =
                viewModel.transactionFilterModel.toString(this@RetailerDetailsActivity)
            fabRedeem.setOnClickListener { getRetailerReport(true) }
        }
    }

    private fun showOfferSerialsSheet(offerSerials: List<OfferSerial>) {
        OfferSerialsSheet(offerSerials) { offerSerial ->
            viewModel.getProductItemBySerial(offerSerial.serial ?: return@OfferSerialsSheet)
                .observe(this) { productItem ->
                    productItem?.let {
                        ProductItemDetailsActivity.getIntent(
                            this,
                            it.first ?: return@observe,
                            it.second ?: return@observe
                        )
                    }
                }
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

    private fun showRetailerRedeemDialog(valueToRedeem: Int) {
        var editTextDialog: EditTextDialog? = null
        editTextDialog = EditTextDialog(
            context = this,
            message = getString(R.string.redeem_commission_question),
            editTextHint = valueToRedeem.toString(),
            editTextInputStyle = InputType.TYPE_CLASS_NUMBER,
            confirmText = getString(R.string.redeem),
            onConfirm = {
                editTextDialog?.dismiss()
                val claimValue = try {
                    it.convertArabicNumbersIfExist().getDigit().toInt()
                } catch (e: Exception) {
                    e.printStackTrace(); -1
                }
                if (claimValue <= 0) {
                    showErrorMsg(getString(R.string.invalid_claim_value))
                    return@EditTextDialog
                }
                redeemCommissions(claimValue)
            },
            isCancelable = true
        )
        editTextDialog.show(supportFragmentManager, EditTextDialog.TAG)
    }

    @SuppressLint("SetTextI18n")
    private fun getRetailerReport(proceedToRedeemDialog: Boolean = false) {
        viewModel.getRetailerReport().observe(this) {
            with(binding) {
                tvRetailerName.text = viewModel.retailerModel?.name
                tvDueCommission.text =
                    "${it?.dueCommissionValue} ${getString(R.string.egyptian_pound)}"
                tvTotalRedeemed.text =
                    "${it?.totalRedeemed} ${getString(R.string.egyptian_pound)}"
                if (proceedToRedeemDialog)
                    showRetailerRedeemDialog(it?.dueCommissionValue?.toInt() ?: 0)
            }
        }
    }

    private fun getTransactions() {
        adapter.submitList(emptyList())
        viewModel.getTransactions().observe(this) {
            adapter.submitList(it)
        }
    }

    private fun approveUnsellingProcess(transactionModel: TransactionModel) {
        viewModel.approveUnsellTransaction(transactionModel).observe(this) {
            getData()
        }
    }

    private fun redeemCommissions(valueToRedeem: Int) {
        viewModel.redeemRetailerCommission(valueToRedeem).observe(this) {
            if (it) {
                showSuccessMsg(getString(R.string.commission_redeemed_successfully))
                getData()
            } else
                showErrorMsg(getString(R.string.something_went_wrong))
        }
    }

    private fun initObservers() {
        viewModel.productsLiveData.observe(this) { productsList ->
            if (productsList.isEmpty())
                return@observe
            val restrictedProductsText = productsList.mapNotNull { it.name }.joinToString { it }
            with(binding) {
                lblRestrictedProducts.makeVisible()
                tvRestrictedProductsSummary.makeVisible()
                tvRestrictedProductsSummary.text = restrictedProductsText
            }
        }
    }
}