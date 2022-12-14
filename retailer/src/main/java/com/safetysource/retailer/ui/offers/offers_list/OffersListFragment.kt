package com.safetysource.retailer.ui.offers.offers_list

import androidx.fragment.app.viewModels
import com.safetysource.core.base.BaseFragment
import com.safetysource.core.ui.adapters.OffersAdapter
import com.safetysource.core.ui.setIsVisible
import com.safetysource.retailer.R
import com.safetysource.retailer.databinding.FragmentOffersListBinding
import com.safetysource.retailer.ui.offers.offer_details.OfferDetailsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OffersListFragment :
    BaseFragment<FragmentOffersListBinding, OffersListViewModel>(R.layout.fragment_offers_list) {

    override val viewModel: OffersListViewModel by viewModels()
    override val binding by viewBinding(FragmentOffersListBinding::bind)

    private lateinit var subscribedOffersAdapter: OffersAdapter
    private lateinit var availableOffersAdapter: OffersAdapter

    override fun onViewCreated() {
        initViews()
        initObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getData()
    }

    private fun initViews() {
        subscribedOffersAdapter = OffersAdapter(onItemClicked = {
            startActivity(OfferDetailsActivity.getIntent(requireContext(), it))
        })
        binding.rvSubscribedOffers.adapter = subscribedOffersAdapter

        availableOffersAdapter = OffersAdapter(onItemClicked = {
            startActivity(OfferDetailsActivity.getIntent(requireContext(), it))
        })
        binding.rvAvailableOffers.adapter = availableOffersAdapter
    }

    private fun initObservers() {
        with(viewModel) {
            subscribedOffersLiveData.observe(viewLifecycleOwner) {
                subscribedOffersAdapter.submitList(it)
                binding.lblSubscribedOffers.setIsVisible(it.isNotEmpty())
                binding.rvSubscribedOffers.setIsVisible(it.isNotEmpty())
            }
            availableOffersLiveData.observe(viewLifecycleOwner) {
                availableOffersAdapter.submitList(it)
                binding.lblAvailableOffers.setIsVisible(it.isNotEmpty())
                binding.rvAvailableOffers.setIsVisible(it.isNotEmpty())
            }
        }
    }
}