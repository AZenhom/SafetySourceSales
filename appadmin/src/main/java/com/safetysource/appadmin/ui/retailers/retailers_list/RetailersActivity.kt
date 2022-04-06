package com.safetysource.appadmin.ui.retailers.retailers_list

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.safetysource.appadmin.databinding.ActivityRetailersBinding
import com.safetysource.appadmin.ui.retailers.create_edit_retailer.CreateEditRetailerActivity
import com.safetysource.appadmin.ui.retailers.retailer_details.RetailerDetailsActivity
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

    private fun initViews() {
        adapter = RetailersAdapter(
            onItemClicked = {
                startActivity(
                    RetailerDetailsActivity.getIntent(
                        this, viewModel.teamModel ?: return@RetailersAdapter, it
                    )
                )
            },
            onEditClicked = {}
        )
        with(binding) {
            rvRetailers.adapter = adapter
            toolbar.setNavigationOnClickListener { onBackPressed() }
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
        adapter.submitList(emptyList())
        viewModel.getRetailers().observe(this) {
            adapter.submitList(it)
        }
        viewModel.getTeamReport().observe(this) {
            with(binding) {
                tvTeamName.text = viewModel.teamModel?.name
                tvDueCommission.text = "${it?.dueCommissionValue} EGP"
                tvTotalRedeemed.text = "${it?.totalRedeemed} EGP"
            }
        }
    }
}