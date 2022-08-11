package com.safetysource.appadmin.ui.offers.offers_lists

import androidx.activity.viewModels
import androidx.core.widget.NestedScrollView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.safetysource.appadmin.databinding.ActivityOffersListsBinding
import com.safetysource.core.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OffersListsActivity : BaseActivity<ActivityOffersListsBinding, OffersListsViewModel>() {

    override val viewModel: OffersListsViewModel by viewModels()
    override val binding by viewBinding(ActivityOffersListsBinding::inflate)

    private lateinit var adapter: PricingViewPagerAdapter

    override fun onActivityCreated() {
        initViews()
        initObservers()
        viewModel.getData()
    }

    private fun initViews() {

    }

    private fun initObservers() {

    }

    private fun setUpViewPager() {
        adapter = PricingViewPagerAdapter(this)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabsLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.online_visit)
                else -> getString(R.string.clinic_visit)
            }
        }.attach()
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.nScrollView.fullScroll(NestedScrollView.FOCUS_UP)
                super.onPageSelected(position)
            }
        })
    }

    fun isChanged(): Boolean {
        val currentItem = binding.viewPager.currentItem
        val currentPage = childFragmentManager.findFragmentByTag("f${currentItem}")
        return when (currentItem) {
            PricingViewPagerAdapter.CLINIC_VISITS ->
                (currentPage as ClinicVisitPricingFragment).isChanged()
            PricingViewPagerAdapter.ONLINE_VISITS ->
                (currentPage as OnlineVisitPricingFragment).isChanged()
            else ->
                throw Exception("current item = $currentItem not found")
        }
    }
}