package com.safetysource.retailer.ui.offers.offer_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    private val productRepository: ProductRepository,
    private val productCategoryRepository: ProductCategoryRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    var offerModel: OfferModel? =
        savedStateHandle[OfferDetailsActivity.OFFER_MODEL]

    private lateinit var retailerModel: RetailerModel

    private val _sellingCountLiveData = MutableLiveData<Int>()
    val sellingCountLiveData: LiveData<Int> get() = _sellingCountLiveData

    private val _exclusiveCategoryLiveData = MutableLiveData<ProductCategoryModel?>()
    val exclusiveCategoryLiveData: LiveData<ProductCategoryModel?> get() = _exclusiveCategoryLiveData

    private val _exclusiveProductLiveData = MutableLiveData<ProductModel?>()
    val exclusiveProductLiveData: LiveData<ProductModel?> get() = _exclusiveProductLiveData

    init {
        safeLauncher {
            retailerModel = retailerRepository.getCurrentRetailerModel() ?: return@safeLauncher
            getOfferRelatedTransactions()
            getProductCategory()
            getProduct()
        }
    }

    private fun getTransactionFilterInstance(): TransactionFilterModel {
        val transactionFilterModel = TransactionFilterModel()
        retailerModel.let {
            transactionFilterModel.retailer = it
        }
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

    private fun getProduct() {
        if (offerModel?.productId == null)
            return
        safeLauncher {
            val result =
                productRepository.getProductById(offerModel?.productId ?: "")
            if (result is StatefulResult.Success)
                _exclusiveProductLiveData.value = result.data
            else
                handleError(result.errorModel)
        }
    }

    private fun getProductCategory() {
        if (offerModel?.productCategoryId == null)
            return
        safeLauncher {
            val result =
                productCategoryRepository
                    .getProductCategoryById(offerModel?.productCategoryId ?: "")
            if (result is StatefulResult.Success)
                _exclusiveCategoryLiveData.value = result.data
            else
                handleError(result.errorModel)
        }
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
                retailerId = retailerModel.id,
                teamId = retailerModel.teamId,
                claimedOrRemoved = false,
            )
            val response =
                subscribedOfferRepository.createUpdateSubscribedOffer(subscribedOfferModel)
            hideLoading()
            if (response is StatefulResult.Success) {
                offerModel?.subscribedOfferModel = subscribedOfferModel
                liveData.value = true
            } else
                handleError(response.errorModel)
        }
        return liveData
    }

    fun deleteOfferSubscription(): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        if (offerModel?.subscribedOfferModel == null) {
            showErrorMsg(R.string.something_went_wrong)
            liveData.value = false
            return liveData
        }
        safeLauncher {
            showLoading()
            offerModel!!.subscribedOfferModel!!.apply {
                claimedOrRemoved = true
                updatedAt = null
            }
            val response =
                subscribedOfferRepository.createUpdateSubscribedOffer(offerModel!!.subscribedOfferModel!!)
            hideLoading()
            if (response is StatefulResult.Success)
                liveData.value = true
            else
                handleError(response.errorModel)
        }
        return liveData
    }

    fun claimOffer(): LiveData<Float> {
        val liveData = MutableLiveData<Float>()
        // valueToClaim Calculation
        val valueToClaim = if (offerModel?.canRepeat == true) {
            ((sellingCountLiveData.value ?: 0)
                    / (offerModel?.neededSellCount ?: 0)) * (offerModel?.valPerRepeat ?: 0f)
        } else {
            if ((sellingCountLiveData.value ?: 0) >=
                (offerModel?.neededSellCount ?: 0)
            ) (offerModel?.valPerRepeat ?: 0f)
            else 0f
        }

        // Return if no value to claim
        if (valueToClaim <= 0f) {
            liveData.value = 0f
            return liveData
        }

        safeLauncher {
            showLoading()

            val teamReport = reportsRepository.getTeamReportById(retailerModel.teamId ?: "").data
            val retailerReport =
                reportsRepository.getRetailerReportById(retailerModel.id ?: "").data

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
                retailerId = retailerModel.id,
                teamId = retailerModel.teamId,
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
            liveData.value = valueToClaim
            hideLoading()
        }
        return liveData
    }
}