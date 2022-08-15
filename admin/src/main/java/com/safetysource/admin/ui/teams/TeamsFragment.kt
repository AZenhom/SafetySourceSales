package com.safetysource.admin.ui.teams

import androidx.fragment.app.viewModels
import com.safetysource.admin.R
import com.safetysource.admin.databinding.FragmentTeamsBinding
import com.safetysource.admin.ui.retailers.retailers_list.RetailersActivity
import com.safetysource.core.base.BaseFragment
import com.safetysource.core.ui.dialogs.EditTextDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamsFragment :
    BaseFragment<FragmentTeamsBinding, TeamsViewModel>(R.layout.fragment_teams) {

    override val viewModel: TeamsViewModel by viewModels()
    override val binding by viewBinding(FragmentTeamsBinding::bind)

    private lateinit var teamsAdapter: TeamsAdapter

    override fun onViewCreated() {
        initViews()
        getTeams()
    }

    private fun initViews() {
        teamsAdapter = TeamsAdapter(
            onItemClicked = { startActivity(RetailersActivity.getIntent(requireContext(), it)) },
            onEditClicked = {}
        )
        with(binding) {
            rvTeams.adapter = teamsAdapter
            fabAdd.setOnClickListener {
                showCreateTeamDialog()
            }
        }
    }

    private fun showCreateTeamDialog() {
        var editTextDialog: EditTextDialog? = null
        editTextDialog = EditTextDialog(
            context = requireContext(),
            message = getString(com.safetysource.core.R.string.create_new_team),
            editTextHint = getString(com.safetysource.core.R.string.team_name_hint),
            confirmText = getString(com.safetysource.core.R.string.submit),
            onConfirm = { editTextDialog?.dismiss();createTeam(it) },
            isCancelable = true
        )
        editTextDialog.show(parentFragmentManager, EditTextDialog.TAG)
    }

    private fun getTeams() {
        teamsAdapter.submitList(emptyList())
        viewModel.getTeams().observe(viewLifecycleOwner) { teamsAdapter.submitList(it) }
    }

    private fun createTeam(teamName: String) {
        viewModel.createTeam(teamName).observe(viewLifecycleOwner) { getTeams() }
    }
}