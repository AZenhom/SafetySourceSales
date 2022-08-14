package com.safetysource.appadmin.ui.offers.offers_lists

import androidx.activity.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.safetysource.appadmin.databinding.ActivityOffersListsBinding
import com.safetysource.appadmin.ui.offers.create_edit_offer.CreateEditOfferActivity
import com.safetysource.core.R
import com.safetysource.core.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OffersListsActivity : BaseActivity<ActivityOffersListsBinding, OffersListsViewModel>() {

    override val viewModel: OffersListsViewModel by viewModels()
    override val binding by viewBinding(ActivityOffersListsBinding::inflate)

    private lateinit var adapter: OffersViewPagerAdapter

    override fun onActivityCreated() {
        initViews()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getData()
    }

    private fun initViews() {
        setUpViewPager()
        binding.fabAdd.setOnClickListener {
            startActivity(CreateEditOfferActivity.getIntent(this))
        }
    }

    private fun setUpViewPager() {
        adapter = OffersViewPagerAdapter(this)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                OffersListFragment.OffersFragmentMode.AVAILABLE_OFFERS.index -> getString(R.string.available_offers)
                OffersListFragment.OffersFragmentMode.UPCOMING_OFFERS.index -> getString(R.string.upcoming_offers)
                OffersListFragment.OffersFragmentMode.HISTORY_OFFERS.index -> getString(R.string.history_offers)
                else -> ""
            }
        }.attach()
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.offscreenPageLimit = 1
        /*binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.nScrollView.fullScroll(NestedScrollView.FOCUS_UP)
                super.onPageSelected(position)
            }
        })*/
    }
}