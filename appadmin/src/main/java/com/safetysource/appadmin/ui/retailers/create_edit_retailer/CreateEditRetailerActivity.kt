package com.safetysource.appadmin.ui.retailers.create_edit_retailer

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.safetysource.appadmin.databinding.ActivityCreateEditRetailerBinding
import com.safetysource.core.base.BaseActivity

class CreateEditRetailerActivity :
    BaseActivity<ActivityCreateEditRetailerBinding, CreateEditRetailerViewModel>() {

    companion object {
        const val TEAM_ID = "TEAM_ID"
        fun getIntent(
            context: Context,
            teamId: String
        ) =
            Intent(context, CreateEditRetailerActivity::class.java).apply {
                putExtra(TEAM_ID, teamId)
            }
    }

    override val viewModel: CreateEditRetailerViewModel by viewModels()
    override val binding by viewBinding(ActivityCreateEditRetailerBinding::inflate)

    override fun onActivityCreated() {

    }
}