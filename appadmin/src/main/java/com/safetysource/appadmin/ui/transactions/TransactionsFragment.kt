package com.safetysource.appadmin.ui.transactions

import androidx.fragment.app.viewModels
import com.safetysource.appadmin.R
import com.safetysource.appadmin.databinding.FragmentTransactionsBinding
import com.safetysource.core.base.BaseFragment
import com.safetysource.core.ui.dialogs.InfoDialog
import com.safetysource.core.ui.adapters.TransactionsAdapter
import com.safetysource.data.model.TransactionModel
import com.safetysource.data.model.TransactionType
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
        adapter = TransactionsAdapter {
            if (it.type == TransactionType.UNSELLING && it.isUnsellingApproved == false)
                showUnsellingApprovalDialog(it)
        }
        binding.rvTransactions.adapter = adapter
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
        }
    }

    private fun approveUnsellingProcess(transactionModel: TransactionModel) {
        viewModel.approveUnsellTransaction(transactionModel).observe(viewLifecycleOwner) {
            getTransactions()
        }
    }
}