package com.safetysource.retailer.ui.offers.offers_list

import androidx.fragment.app.viewModels
import com.safetysource.retailer.R
import com.safetysource.retailer.databinding.FragmentOffersListBinding
import com.safetysource.core.base.BaseFragment
import com.safetysource.core.ui.adapters.TransactionsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OffersListFragment:
    BaseFragment<FragmentOffersListBinding, OffersListViewModel>(R.layout.fragment_transactions) {

    override val viewModel: OffersListViewModel by viewModels()
    override val binding by viewBinding(FragmentOffersListBinding::bind)

    private lateinit var adapter: TransactionsAdapter

    override fun onViewCreated() {
    }
}