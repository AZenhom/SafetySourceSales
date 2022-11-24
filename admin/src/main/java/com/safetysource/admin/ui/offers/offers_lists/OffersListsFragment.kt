package com.safetysource.admin.ui.offers.offers_lists

import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.safetysource.admin.databinding.FragmentOffersListBinding
import com.safetysource.admin.ui.offers.create_edit_offer.CreateEditOfferActivity
import com.safetysource.core.R
import com.safetysource.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OffersListsFragment :
    BaseFragment<FragmentOffersListBinding, OffersListsViewModel>(com.safetysource.admin.R.layout.fragment_offers_list) {

    override val viewModel: OffersListsViewModel by activityViewModels()
    override val binding by viewBinding(FragmentOffersListBinding::bind)

    private lateinit var adapter: OffersViewPagerAdapter

    override fun onViewCreated() {
        initViews()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getData()
    }

    private fun initViews() {
        setUpViewPager()
        binding.fabAdd.setOnClickListener {
            startActivity(CreateEditOfferActivity.getIntent(requireContext()))
        }
    }

    private fun setUpViewPager() {
        adapter = OffersViewPagerAdapter(this)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                OffersListFragment.OffersFragmentMode.AVAILABLE_OFFERS.index -> getString(R.string.available)
                OffersListFragment.OffersFragmentMode.UPCOMING_OFFERS.index -> getString(R.string.upcoming)
                OffersListFragment.OffersFragmentMode.HISTORY_OFFERS.index -> getString(R.string.history)
                else -> ""
            }
        }.attach()
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.offscreenPageLimit = 1
    }
}