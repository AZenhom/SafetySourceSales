package com.safetysource.appadmin.ui.retailers.retailer_details

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.safetysource.appadmin.databinding.ActivityRetailerDetailsBinding
import com.safetysource.core.R
import com.safetysource.core.base.BaseActivity
import com.safetysource.core.ui.adapters.TransactionsAdapter
import com.safetysource.core.ui.dialogs.InfoDialog
import com.safetysource.core.utils.toString
import com.safetysource.data.model.RetailerModel
import com.safetysource.data.model.TeamModel
import com.safetysource.data.model.TransactionModel
import com.safetysource.data.model.TransactionType
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
    }

    private fun getData() {
        getTransactions()
        getRetailerReport()
    }

    private fun initViews() {
        adapter = TransactionsAdapter {
            if (it.type == TransactionType.UNSELLING && it.isUnsellingApproved == false)
                showUnsellingApprovalDialog(it)
        }
        with(binding) {
            rvTransactions.adapter = adapter
            tvFilterSummary.text =
                viewModel.transactionFilterModel.toString(this@RetailerDetailsActivity)
            toolbar.setNavigationOnClickListener { onBackPressed() }
            fabRedeem.setOnClickListener { showRetailerRedeemDialog() }
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

    private fun showRetailerRedeemDialog() {
        var infoDialog: InfoDialog? = null
        infoDialog = InfoDialog(
            context = this,
            imageRes = R.drawable.warning,
            message = getString(R.string.redeem_commision_question),
            confirmText = getString(R.string.redeem),
            onConfirm = { infoDialog?.dismiss(); redeemCommissions() },
            isCancelable = true
        )
        infoDialog.show(supportFragmentManager, InfoDialog.TAG)
    }

    @SuppressLint("SetTextI18n")
    private fun getRetailerReport() {
        viewModel.getRetailerReport().observe(this) {
            with(binding) {
                tvRetailerName.text = viewModel.retailerModel?.name
                tvDueCommission.text = "${it?.dueCommissionValue} EGP"
                tvTotalRedeemed.text = "${it?.totalRedeemed} EGP"
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

    private fun redeemCommissions() {
        viewModel.redeemRetailerCommission().observe(this) {
            if (it) {
                showSuccessMsg(getString(R.string.commission_redeemed_successfully))
                getData()
            } else
                showErrorMsg(getString(R.string.something_went_wrong))
        }
    }
}