package com.safetysource.appadmin.ui.profile

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.safetysource.appadmin.R
import com.safetysource.appadmin.databinding.FragmentProfileBinding
import com.safetysource.appadmin.ui.host.HostViewModel
import com.safetysource.core.base.BaseFragment
import com.safetysource.core.ui.dialogs.InfoDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<FragmentProfileBinding, ProfileViewModel>(R.layout.fragment_profile) {

    override val viewModel: ProfileViewModel by viewModels()
    override val binding by viewBinding(FragmentProfileBinding::bind)
    private val hostViewModel: HostViewModel by activityViewModels()

    override fun onViewCreated() {
        initViews()
        getProfile()
    }

    private fun getProfile() {
        viewModel.getUserProfile().observe(viewLifecycleOwner){
            binding.tvAdminName.text = it.name
        }
    }

    private fun initViews() {
        with(binding) {
            btnLogout.setOnClickListener {
                showLogoutSheet()
            }
        }
    }

    private fun showLogoutSheet() {
        var infoDialog: InfoDialog? = null
        infoDialog = InfoDialog(
            context = requireContext(),
            imageRes = com.safetysource.core.R.drawable.warning,
            message = getString(com.safetysource.core.R.string.are_you_sure_you_want_to_log_out),
            confirmText = getString(com.safetysource.core.R.string.logout),
            onConfirm = { infoDialog?.dismiss();logOut() },
            isCancelable = true
        )
        infoDialog.show(parentFragmentManager, InfoDialog.TAG)
    }

    private fun logOut() {
        hostViewModel.logout()
    }
}