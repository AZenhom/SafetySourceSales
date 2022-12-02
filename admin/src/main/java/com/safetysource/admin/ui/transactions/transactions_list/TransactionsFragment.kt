package com.safetysource.admin.ui.transactions.transactions_list

import android.app.Activity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.safetysource.admin.R
import com.safetysource.admin.databinding.FragmentTransactionsBinding
import com.safetysource.admin.ui.product_items.item_details.ProductItemDetailsActivity
import com.safetysource.admin.ui.transactions.transactions_filter.TransactionsFilterActivity
import com.safetysource.core.base.BaseFragment
import com.safetysource.core.ui.dialogs.InfoDialog
import com.safetysource.core.ui.adapters.TransactionsAdapter
import com.safetysource.core.ui.sheets.OfferSerialsSheet
import com.safetysource.core.utils.toString
import com.safetysource.data.model.OfferSerial
import com.safetysource.data.model.TransactionFilterModel
import com.safetysource.data.model.TransactionModel
import com.safetysource.data.model.TransactionType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionsFragment :
    BaseFragment<FragmentTransactionsBinding, TransactionsViewModel>(R.layout.fragment_transactions) {

    override val viewModel: TransactionsViewModel by viewModels()
    override val binding by viewBinding(FragmentTransactionsBinding::bind)

    private lateinit var adapter: TransactionsAdapter

    private val transactionFilterIntentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getSerializableExtra(TransactionsFilterActivity.TRANSACTION_FILTER_MODEL)
                    ?.let {
                        viewModel.transactionFilterModel = it as TransactionFilterModel
                        getTransactions()
                    }
            }
        }

    override fun onViewCreated() {
        initViews()
        getTransactions()
    }

    private fun initViews() {
        binding.btnFilter.setOnClickListener {
            transactionFilterIntentLauncher.launch(
                TransactionsFilterActivity.getIntent(
                    requireContext(), viewModel.transactionFilterModel
                )
            )
        }
        adapter = TransactionsAdapter(
            onItemClicked = {
                if (it.type == TransactionType.UNSELLING && it.isUnsellingApproved == false)
                    showUnsellingApprovalDialog(it)
            },
            onMultipleSerialsClicked = { showOfferSerialsSheet(it) }
        )
        binding.rvTransactions.adapter = adapter
    }

    private fun showOfferSerialsSheet(offerSerials: List<OfferSerial>) {
        OfferSerialsSheet(offerSerials) { offerSerial ->
            viewModel.getProductItemBySerial(offerSerial.serial ?: return@OfferSerialsSheet)
                .observe(viewLifecycleOwner) { productItem ->
                    productItem?.let {
                        startActivity(
                            ProductItemDetailsActivity.getIntent(
                                requireContext(),
                                it.first ?: return@observe,
                                it.second ?: return@observe
                            )
                        )
                    }
                }
        }.show(parentFragmentManager, "OfferSerialsSheet")
    }

    private fun showUnsellingApprovalDialog(transactionModel: TransactionModel) {
        var infoDialog: InfoDialog? = null
        infoDialog = InfoDialog(
            context = requireContext(),
            imageRes = com.safetysource.core.R.drawable.warning,
            message = getString(com.safetysource.core.R.string.approve_unselling_question),
            confirmText = getString(com.safetysource.core.R.string.approve),
            onConfirm = { infoDialog?.dismiss(); approveUnsellingProcess(transactionModel) },
            isCancelable = true
        )
        infoDialog.show(parentFragmentManager, InfoDialog.TAG)
    }

    private fun getTransactions() {
        adapter.submitList(emptyList())
        viewModel.getTransactions().observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.tvFilterSummary.text =
                viewModel.transactionFilterModel.toString(requireContext())
        }
    }

    private fun approveUnsellingProcess(transactionModel: TransactionModel) {
        viewModel.approveUnsellTransaction(transactionModel).observe(viewLifecycleOwner) {
            getTransactions()
        }
    }
}