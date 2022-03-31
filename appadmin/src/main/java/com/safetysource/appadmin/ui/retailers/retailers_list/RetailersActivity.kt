package com.safetysource.appadmin.ui.retailers.retailers_list

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.safetysource.appadmin.databinding.ActivityRetailersBinding
import com.safetysource.core.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RetailersActivity : BaseActivity<ActivityRetailersBinding, RetailersViewModel>() {

    companion object {
        const val TEAM_ID = "TEAM_ID"
        fun getIntent(
            context: Context,
            teamId: String
        ) =
            Intent(context, RetailersActivity::class.java).apply {
                putExtra(TEAM_ID, teamId)
            }
    }

    override val viewModel: RetailersViewModel by viewModels()
    override val binding by viewBinding(ActivityRetailersBinding::inflate)

    override fun onActivityCreated() {

    }
}