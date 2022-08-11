package com.safetysource.appadmin.ui.offers.offers_lists

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class OffersViewPagerAdapter {
}

class PricingViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = OffersListFragment.OffersFragmentMode.values().size

    override fun createFragment(position: Int): Fragment {
        return OffersListFragment.newInstance(OffersListFragment.OffersFragmentMode.fromInt(position))
    }
}