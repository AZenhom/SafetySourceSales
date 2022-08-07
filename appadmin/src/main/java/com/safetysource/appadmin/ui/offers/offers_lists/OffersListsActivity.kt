package com.safetysource.appadmin.ui.offers.offers_lists

import androidx.activity.viewModels
import com.safetysource.appadmin.databinding.ActivityOffersListsBinding
import com.safetysource.core.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OffersListsActivity : BaseActivity<ActivityOffersListsBinding, OffersListsViewModel>() {

    override val viewModel: OffersListsViewModel by viewModels()
    override val binding by viewBinding(ActivityOffersListsBinding::inflate)

    override fun onActivityCreated() {
        initViews()
        initObservers()
        viewModel.getData()
    }

    private fun initViews() {

    }

    private fun initObservers() {

    }
}