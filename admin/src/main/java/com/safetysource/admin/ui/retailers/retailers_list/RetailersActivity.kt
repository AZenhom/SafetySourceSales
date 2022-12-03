package com.safetysource.admin.ui.retailers.retailers_list

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.safetysource.admin.databinding.ActivityRetailersBinding
import com.safetysource.admin.ui.retailers.create_edit_retailer.CreateEditRetailerActivity
import com.safetysource.admin.ui.retailers.retailer_details.RetailerDetailsActivity
import com.safetysource.core.R
import com.safetysource.core.base.BaseActivity
import com.safetysource.core.ui.dialogs.InfoDialog
import com.safetysource.core.ui.sheets.SelectListSheet
import com.safetysource.data.model.TeamModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RetailersActivity : BaseActivity<ActivityRetailersBinding, RetailersViewModel>() {

    companion object {
        const val TEAM_MODEL = "TEAM_MODEL"
        fun getIntent(
            context: Context,
            teamModel: TeamModel
        ) =
            Intent(context, RetailersActivity::class.java).apply {
                putExtra(TEAM_MODEL, teamModel)
            }
    }

    override val viewModel: RetailersViewModel by viewModels()
    override val binding by viewBinding(ActivityRetailersBinding::inflate)

    private lateinit var adapter: RetailersAdapter

    override fun onActivityCreated() {
        initViews()
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        adapter = RetailersAdapter(
            onItemClicked = {
                startActivity(
                    RetailerDetailsActivity.getIntent(
                        this, viewModel.teamModel ?: return@RetailersAdapter, it
                    )
                )
            },
            onEditClicked = {
                startActivity(
                    CreateEditRetailerActivity.getIntent(
                        this, viewModel.teamModel ?: return@RetailersAdapter, it
                    )
                )
            },
            onRemoveClicked = if (viewModel.teamModel?.id == TeamModel.TEAM_LESS) null
            else ({ retailer ->
                viewModel.removeRetailerFromTeam(retailer).observe(this) { if (it) getData() }
            })
        )
        with(binding) {
            registerToolBarOnBackPressed(toolbar)
            tvTeamName.text = viewModel.teamModel?.name
            tvDueCommission.text =
                "${viewModel.teamModel?.teamReportModel?.dueCommissionValue} ${getString(R.string.egyptian_pound)}"
            tvTotalRedeemed.text =
                "${viewModel.teamModel?.teamReportModel?.totalRedeemed} ${getString(R.string.egyptian_pound)}"

            rvRetailers.adapter = adapter

            fabAdd.setOnClickListener {
                if (viewModel.teamModel?.id == TeamModel.TEAM_LESS) openRegisterNewRetailerActivity()
                else showAddToTeamDialog()
            }
        }
    }

    private fun showAddToTeamDialog() {
        var infoDialog: InfoDialog? = null
        infoDialog = InfoDialog(
            context = this,
            imageRes = R.drawable.warning,
            message = getString(R.string.add_retailer_to_team),
            confirmText = getString(R.string.new_retailer),
            cancelText = getString(R.string.existing_retailer),
            onConfirm = {
                infoDialog?.dismiss()
                openRegisterNewRetailerActivity()
            },
            onCancel = {
                infoDialog?.dismiss()
                viewModel.getTeamLessRetailers().observe(this) { retailers ->
                    SelectListSheet(
                        itemsList = retailers.toMutableList(),
                        sheetTitle = getString(R.string.add_retailer_to_team),
                        sheetSubTitle = getString(R.string.select_teamless_retailer)
                    ) { retailer ->
                        viewModel.addExistingRetailerToTeam(retailer ?: return@SelectListSheet)
                            .observe(this) { if (it) getData() }
                    }.show(supportFragmentManager, SelectListSheet.TAG)
                }
            },
            isCancelable = true
        )
        infoDialog.show(supportFragmentManager, InfoDialog.TAG)
    }

    private fun openRegisterNewRetailerActivity() {
        startActivity(
            CreateEditRetailerActivity.getIntent(
                this@RetailersActivity, viewModel.teamModel ?: return
            )
        )
    }

    @SuppressLint("SetTextI18n")
    private fun getData() {
        // Team Details
        viewModel.getTeamReport().observe(this) { teamReportModel ->
            binding.tvDueCommission.text =
                "${teamReportModel?.dueCommissionValue} ${getString(R.string.egyptian_pound)}"
            binding.tvTotalRedeemed.text =
                "${teamReportModel?.totalRedeemed} ${getString(R.string.egyptian_pound)}"

        }
        // Team Retailers
        adapter.submitList(emptyList())
        viewModel.getTeamRetailers().observe(this) {
            adapter.submitList(it)
        }
    }
}