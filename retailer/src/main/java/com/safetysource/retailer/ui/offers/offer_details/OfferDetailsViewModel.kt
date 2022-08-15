package com.safetysource.retailer.ui.offers.offer_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.R
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.*
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class OfferDetailsViewModel @Inject constructor(
    private val subscribedOfferRepository: SubscribedOfferRepository,
    private val retailerRepository: RetailerRepository,
    private val reportsRepository: ReportsRepository,
    private val transactionsRepository: TransactionsRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    var offerModel: OfferModel? =
        savedStateHandle[OfferDetailsActivity.OFFER_MODEL]

    private var retailerModel: RetailerModel? = null

    private val _sellingCountLiveData = LiveEvent<Int>()
    val sellingCountLiveData: LiveData<Int> get() = _sellingCountLiveData

    init {
        safeLauncher {
            retailerModel = retailerRepository.getCurrentRetailerModel()
        }
        getOfferRelatedTransactions()
    }

    private fun getTransactionFilterInstance(): TransactionFilterModel {
        val transactionFilterModel = TransactionFilterModel()
        offerModel?.productId?.let {
            transactionFilterModel.product = ProductModel(id = it)
        }
        offerModel?.productCategoryId?.let {
            transactionFilterModel.category = ProductCategoryModel(id = it)
        }
        offerModel?.startsAt?.let {
            transactionFilterModel.dateFrom = it
        }
        offerModel?.expiresAt?.let {
            transactionFilterModel.dateTo = it
        }
        return transactionFilterModel
    }

    private fun getOfferRelatedTransactions() = safeLauncher {
        showLoading()
        val result1 = async {
            transactionsRepository.getTransactions(getTransactionFilterInstance().apply {
                transactionType = TransactionType.SELLING
            })
        }
        val result2 = async {
            transactionsRepository.getTransactions(getTransactionFilterInstance().apply {
                transactionType = TransactionType.UNSELLING
            })
        }
        val sellingTransactions = result1.await()
        val unsellingTransactions = result2.await()
        if (sellingTransactions is StatefulResult.Success && sellingTransactions.data != null
            && unsellingTransactions is StatefulResult.Success && unsellingTransactions.data != null
        ) {
            val totalSelling = sellingTransactions.data!!.size - unsellingTransactions.data!!.size
            _sellingCountLiveData.value = totalSelling
        }
        hideLoading()
    }

    fun createOfferSubscription(): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            showLoading()
            val subscribedOfferModel = SubscribedOfferModel(
                id = subscribedOfferRepository.getNewSubscribedOfferId(),
                offerId = offerModel?.id,
                retailerId = retailerModel?.id,
                teamId = retailerModel?.teamId,
                claimedOrRemoved = false,
            )
            val response =
                subscribedOfferRepository.createUpdateSubscribedOffer(subscribedOfferModel)
            hideLoading()
            if (response is StatefulResult.Success)
                liveData.value = true
            else
                handleError(response.errorModel)
        }
        return liveData
    }

    fun claimOffer(valueToClaim: Float): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            showLoading()

            val teamReport = reportsRepository.getTeamReportById(retailerModel?.teamId ?: "").data
            val retailerReport =
                reportsRepository.getRetailerReportById(retailerModel?.id ?: "").data

            if (teamReport == null || retailerReport == null || offerModel?.subscribedOfferModel == null) {
                showErrorMsg(R.string.something_went_wrong)
                hideLoading()
                return@safeLauncher
            }

            val subscribedOfferModel = offerModel!!.subscribedOfferModel!!
            subscribedOfferModel.claimedOrRemoved = true
            subscribedOfferModel.updatedAt = null


            val transactionModel = TransactionModel(
                id = transactionsRepository.getNewTransactionId(),
                type = TransactionType.OFFER_CLAIM,
                retailerId = retailerModel?.id,
                teamId = retailerModel?.teamId,
                productId = offerModel?.productId,
                categoryId = offerModel?.productCategoryId,
                commissionAppliedOrRemoved = valueToClaim,
                offerId = offerModel?.id
            )

            teamReport.dueCommissionValue =
                (teamReport.dueCommissionValue ?: 0f) + valueToClaim
            teamReport.updatedAt = null


            retailerReport.dueCommissionValue =
                (retailerReport.dueCommissionValue ?: 0f) + valueToClaim
            retailerReport.updatedAt = null

            subscribedOfferRepository.claimSubscribedOffer(
                subscribedOfferModel,
                transactionModel,
                retailerReport,
                teamReport
            )
            liveData.value = true
            hideLoading()
        }
        return liveData
    }
}