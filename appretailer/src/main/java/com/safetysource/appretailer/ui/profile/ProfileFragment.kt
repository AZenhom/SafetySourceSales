package com.safetysource.appretailer.ui.profile

import android.annotation.SuppressLint
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.safetysource.appretailer.databinding.FragmentProfileBinding
import com.safetysource.appretailer.ui.host.HostViewModel
import com.safetysource.core.R
import com.safetysource.core.base.BaseFragment
import com.safetysource.core.ui.dialogs.InfoDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<FragmentProfileBinding, ProfileViewModel>( com.safetysource.appretailer.R.layout.fragment_profile) {

    override val viewModel: ProfileViewModel by viewModels()
    override val binding by viewBinding(FragmentProfileBinding::bind)
    private val hostViewModel: HostViewModel by activityViewModels()

    override fun onViewCreated() {
        initViews()
        getProfile()
        getReport()
    }

    private fun getProfile() {
        viewModel.getUserProfile().observe(viewLifecycleOwner){
            binding.tvRetailerName.text = it.name
        }
        viewModel.getRetailerTeam().observe(viewLifecycleOwner){
            binding.tvTeamName.text = it?.name
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getReport(){
        viewModel.getRetailerReport().observe(viewLifecycleOwner){
            with(binding){
                tvDueCommissions.text = "${it?.dueCommissionValue} EGP"
                tvTotalRedeemed.text = "${it?.totalRedeemed} EGP"
            }
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
            imageRes = R.drawable.warning,
            message = getString(R.string.are_you_sure_you_want_to_log_out),
            confirmText = getString(R.string.logout),
            onConfirm = { infoDialog?.dismiss();logOut() },
            isCancelable = true
        )
        infoDialog.show(parentFragmentManager, InfoDialog.TAG)
    }

    private fun logOut() {
        hostViewModel.logout()
    }
}