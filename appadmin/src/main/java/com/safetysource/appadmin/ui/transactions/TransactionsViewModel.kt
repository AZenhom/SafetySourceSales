package com.safetysource.appadmin.ui.transactions

import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.ProductItemState
import com.safetysource.data.model.TransactionModel
import com.safetysource.data.model.TransactionType
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.ProductItemRepository
import com.safetysource.data.repository.TransactionsRepository
import com.safetysource.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val userRepository: UserRepository,
    private val productItemRepository: ProductItemRepository,
) : BaseViewModel() {

    fun getTransactions(
        teamId: String? = null,
        retailerId: String? = null,
        productId: String? = null,
        transactionType: TransactionType? = null
    ): LiveData<List<TransactionModel>> {
        showLoading()
        val liveData = LiveEvent<List<TransactionModel>>()
        safeLauncher {
            val result = transactionsRepository.getTransactions(
                teamId,
                retailerId,
                productId,
                transactionType
            )
            if (result is StatefulResult.Success)
                liveData.value = result.data ?: listOf()
            else
                handleError(result.errorModel)
            hideLoading()
        }
        return liveData
    }

    fun approveUnsellTransaction(transactionModel: TransactionModel): LiveData<Boolean> {
        showLoading()
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            transactionModel.isUnsellingApproved = true
            transactionModel.unsellingApprovedByAdminId = userRepository.getUserId()
            transactionModel.updatedAt = null

            val transactionResult = transactionsRepository.createUpdateTransaction(transactionModel)
            if (transactionResult is StatefulResult.Success) {
                val productItemModel =
                    productItemRepository.getProductItemBySerial(transactionModel.serial ?: "").data
                if (productItemModel != null) {
                    productItemModel.state = ProductItemState.NOT_SOLD_YET
                    productItemModel.updatedAt = null

                    val productItemResult =
                        productItemRepository.createUpdateProductItem(productItemModel)

                    if (productItemResult is StatefulResult.Success)
                        liveData.value = true
                    else
                        handleError(productItemResult.errorModel)
                }
            } else
                handleError(transactionResult.errorModel)

            hideLoading()
        }
        return liveData
    }
}