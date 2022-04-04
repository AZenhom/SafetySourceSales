package com.safetysource.appretailer.ui.transactions

import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.TransactionModel
import com.safetysource.data.model.TransactionType
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.TransactionsRepository
import com.safetysource.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val userRepository: UserRepository,
) : BaseViewModel() {

    fun getTransactions(
        transactionType: TransactionType? = null
    ): LiveData<List<TransactionModel>> {
        showLoading()
        val liveData = LiveEvent<List<TransactionModel>>()
        safeLauncher {
            val result = transactionsRepository.getTransactions(
                retailerId = userRepository.getUserId(),
                transactionType = transactionType,
            )
            if (result is StatefulResult.Success)
                liveData.value = result.data ?: listOf()
            else
                handleError(result.errorModel)
            hideLoading()
        }
        return liveData
    }
}