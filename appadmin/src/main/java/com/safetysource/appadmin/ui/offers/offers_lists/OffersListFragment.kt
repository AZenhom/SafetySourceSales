package com.safetysource.appadmin.ui.offers.offers_lists

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.safetysource.appadmin.R
import com.safetysource.appadmin.databinding.FragmentOfferListBinding
import com.safetysource.core.base.BaseFragment
import com.safetysource.core.ui.adapters.OffersAdapters
import com.safetysource.data.model.OfferModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OffersListFragment :
    BaseFragment<FragmentOfferListBinding, OffersListsViewModel>(R.layout.fragment_offer_list) {

    enum class OffersFragmentMode(val index: Int) {
        UPCOMING_OFFERS(0),
        AVAILABLE_OFFERS(1),
        HISTORY_OFFERS(2);

        companion object {
            fun fromInt(value: Int) =
                values().first { it.index == value }
        }
    }

    companion object {
        private const val FRAGMENT_MODE = "FRAGMENT_MODE"
        fun newInstance(offersFragmentMode: OffersFragmentMode): OffersListFragment {
            val offersListFragment = OffersListFragment()
            offersListFragment.arguments = Bundle().apply {
                putSerializable(FRAGMENT_MODE, offersFragmentMode)
            }
            return offersListFragment
        }
    }

    override val viewModel: OffersListsViewModel by activityViewModels()
    override val binding by viewBinding(FragmentOfferListBinding::bind)

    private lateinit var fragmentMode: OffersFragmentMode
    private lateinit var offersAdapters: OffersAdapters

    override fun onViewCreated() {
        fragmentMode = (arguments?.getSerializable(FRAGMENT_MODE) as OffersFragmentMode?)
            ?: OffersFragmentMode.AVAILABLE_OFFERS
        initViews()
        initObservers()
    }

    private fun initViews() {
        offersAdapters = OffersAdapters(
            onItemClicked = { },
        )
        with(binding) {
            rvOffers.adapter = offersAdapters
        }
    }

    private fun initObservers() {
        when (fragmentMode) {
            OffersFragmentMode.UPCOMING_OFFERS -> viewModel.upcomingOffersLiveData.observe(
                viewLifecycleOwner,
                this::submitOffersList
            )
            OffersFragmentMode.AVAILABLE_OFFERS -> viewModel.availableOffersLiveData.observe(
                viewLifecycleOwner,
                this::submitOffersList
            )
            OffersFragmentMode.HISTORY_OFFERS -> viewModel.historyOffersLiveData.observe(
                viewLifecycleOwner,
                this::submitOffersList
            )
        }
    }

    private fun submitOffersList(offersList: List<OfferModel>) {
        offersAdapters.submitList(offersList)
    }
}