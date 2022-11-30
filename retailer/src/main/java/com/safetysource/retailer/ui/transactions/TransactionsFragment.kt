package com.safetysource.retailer.ui.transactions

import androidx.fragment.app.viewModels
import com.safetysource.retailer.R
import com.safetysource.retailer.databinding.FragmentTransactionsBinding
import com.safetysource.core.base.BaseFragment
import com.safetysource.core.ui.adapters.TransactionsAdapter
import com.safetysource.core.ui.sheets.OfferSerialsSheet
import com.safetysource.data.model.OfferSerial
import com.safetysource.retailer.ui.product_item.ProductItemDetailsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionsFragment :
    BaseFragment<FragmentTransactionsBinding, TransactionsViewModel>(R.layout.fragment_transactions) {

    override val viewModel: TransactionsViewModel by viewModels()
    override val binding by viewBinding(FragmentTransactionsBinding::bind)

    private lateinit var adapter: TransactionsAdapter

    override fun onViewCreated() {
        initViews()
        getTransactions()
    }

    private fun initViews() {
        adapter = TransactionsAdapter(
            onMultipleSerialsClicked = { showOfferSerialsSheet(it) }
        )
        binding.rvTransactions.adapter = adapter
    }

    private fun showOfferSerialsSheet(offerSerials: List<OfferSerial>) {
        OfferSerialsSheet(offerSerials) { offerSerial ->
            viewModel.getProductItemBySerial(offerSerial.serial ?: return@OfferSerialsSheet)
                .observe(viewLifecycleOwner) { productItem ->
                    productItem?.let { ProductItemDetailsActivity.getIntent(requireContext(), it) }
                }
        }
    }

    private fun getTransactions() {
        adapter.submitList(emptyList())
        viewModel.getTransactions().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}