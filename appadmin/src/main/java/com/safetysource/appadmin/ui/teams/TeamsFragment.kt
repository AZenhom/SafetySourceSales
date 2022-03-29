package com.safetysource.appadmin.ui.teams

import androidx.fragment.app.viewModels
import com.safetysource.appadmin.R
import com.safetysource.appadmin.databinding.FragmentTeamsBinding
import com.safetysource.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamsFragment :
    BaseFragment<FragmentTeamsBinding, TeamsViewModel>(R.layout.fragment_teams) {

    override val viewModel: TeamsViewModel by viewModels()
    override val binding by viewBinding(FragmentTeamsBinding::bind)

    override fun onViewCreated() {

    }
}