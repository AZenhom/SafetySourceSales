package com.safetysource.admin.ui.offers.offers_lists

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.safetysource.admin.R
import com.safetysource.admin.databinding.FragmentOffersListModeBinding
import com.safetysource.admin.ui.offers.create_edit_offer.CreateEditOfferActivity
import com.safetysource.admin.ui.offers.offer_details.OfferDetailsActivity
import com.safetysource.core.base.BaseFragment
import com.safetysource.core.ui.adapters.OffersAdapters
import com.safetysource.core.ui.dialogs.InfoDialog
import com.safetysource.data.model.OfferModel
import com.safetysource.data.model.TransactionModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class OffersListFragment :
    BaseFragment<FragmentOffersListModeBinding, OffersListsViewModel>(R.layout.fragment_offers_list_mode) {

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
    override val binding by viewBinding(FragmentOffersListModeBinding::bind)

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
            onItemClicked = { startActivity(OfferDetailsActivity.getIntent(requireContext(), it)) },
            onEditClicked = {
                startActivity(CreateEditOfferActivity.getIntent(requireContext(), it))
            },
            onDeleteClicked =
            if (fragmentMode == OffersFragmentMode.HISTORY_OFFERS) null
            else {
                { deleteOffer(it) }
            }
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

    private fun deleteOffer(offerModel: OfferModel) {
        val dateNow = Calendar.getInstance().time
        if (offerModel.expiresAt?.after(dateNow) == true)
            showDeleteOfferDialog(offerModel)
        else
            showErrorMsg(getString(com.safetysource.core.R.string.history_offers_cannot_be_deleted))
    }

    private fun showDeleteOfferDialog(offerModel: OfferModel) {
        var infoDialog: InfoDialog? = null
        infoDialog = InfoDialog(
            context = requireContext(),
            imageRes = com.safetysource.core.R.drawable.warning,
            message = getString(com.safetysource.core.R.string.delete_offer_question),
            confirmText = getString(com.safetysource.core.R.string.delete),
            onConfirm = {
                infoDialog?.dismiss()
                viewModel.deleteOffer(offerModel).observe(viewLifecycleOwner) {
                    showSuccessMsg(getString(com.safetysource.core.R.string.offer_deleted_successfully))
                }
            },
            isCancelable = true
        )
        infoDialog.show(parentFragmentManager, InfoDialog.TAG)
    }
}