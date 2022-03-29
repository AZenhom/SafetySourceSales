package com.safetysource.appadmin.ui.reatailers

import androidx.fragment.app.viewModels
import com.safetysource.appadmin.R
import com.safetysource.appadmin.databinding.FragmentRetailersBinding
import com.safetysource.appadmin.databinding.FragmentTeamsBinding
import com.safetysource.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RetailersFragment :
    BaseFragment<FragmentRetailersBinding, RetailersViewModel>(R.layout.fragment_retailers) {

    override val viewModel: RetailersViewModel by viewModels()
    override val binding by viewBinding(FragmentRetailersBinding::bind)

    override fun onViewCreated() {

    }
}