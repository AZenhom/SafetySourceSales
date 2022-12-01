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
            }
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
                startActivity(
                    CreateEditRetailerActivity.getIntent(
                        this@RetailersActivity, viewModel.teamModel ?: return@setOnClickListener
                    )
                )
            }
        }
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
        viewModel.getRetailers().observe(this) {
            adapter.submitList(it)
        }
    }
}