package com.safetysource.appretailer.ui.profile

import androidx.fragment.app.viewModels
import com.safetysource.appretailer.R
import com.safetysource.appretailer.databinding.FragmentProfileBinding
import com.safetysource.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<FragmentProfileBinding, ProfileViewModel>(R.layout.fragment_profile) {

    override val viewModel: ProfileViewModel by viewModels()
    override val binding by viewBinding(FragmentProfileBinding::bind)

    override fun onViewCreated() {

    }
}