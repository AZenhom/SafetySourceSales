package com.safetysource.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.safetysource.data.Constants
import com.safetysource.data.base.BaseRepository
import com.safetysource.data.model.*
import com.safetysource.data.model.response.ErrorModel
import com.safetysource.data.model.response.StatefulResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SubscribedOfferRepository @Inject constructor(
    private val fireStoreDB: FirebaseFirestore,
) : BaseRepository() {

    fun getNewSubscribedOfferId(): String {
        return fireStoreDB
            .collection(Constants.COLLECTION_SUBSCRIBED_OFFER)
            .document()
            .id
    }

    suspend fun createUpdateSubscribedOffer(subscribedOfferModel: SubscribedOfferModel): StatefulResult<Unit> {
        if (subscribedOfferModel.id.isNullOrEmpty())
            return StatefulResult.Error(ErrorModel.Unknown)
        return try {
            val productRef = fireStoreDB
                .collection(Constants.COLLECTION_SUBSCRIBED_OFFER)
                .document(subscribedOfferModel.id)
            productRef.set(subscribedOfferModel).await()
            StatefulResult.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun getRetailerSubscribedOffers(retailerId: String): StatefulResult<List<SubscribedOfferModel>> {
        return try {
            val documents =
                fireStoreDB.collection(Constants.COLLECTION_SUBSCRIBED_OFFER)
                    .whereEqualTo(SubscribedOfferModel.RETAILER_ID, retailerId)
                    .whereEqualTo(SubscribedOfferModel.CLAIMED_OR_REMOVED, false)
                    .orderBy(Constants.UPDATED_AT, Query.Direction.DESCENDING)
                    .get().await()
            StatefulResult.Success(documents.toObjects(SubscribedOfferModel::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun claimSubscribedOffer(
        subscribedOfferModel: SubscribedOfferModel,
        transactionModel: TransactionModel,
        retailerReportModel: RetailerReportModel,
        teamReportModel: TeamReportModel
    ): StatefulResult<Unit> {
        if (subscribedOfferModel.id.isNullOrEmpty() || transactionModel.id.isNullOrEmpty())
            return StatefulResult.Error(ErrorModel.Unknown)
        return try {
            fireStoreDB.runBatch {
                // Subscribed Offer
                val subOfferRef = fireStoreDB
                    .collection(Constants.COLLECTION_SUBSCRIBED_OFFER)
                    .document(subscribedOfferModel.id)
                it.set(subOfferRef, subscribedOfferModel)

                // Transaction
                val transactionRef = fireStoreDB
                    .collection(Constants.COLLECTION_TRANSACTION)
                    .document(transactionModel.id!!)
                it.set(transactionRef, transactionModel)

                // Retailer Report
                val retailerReportRef = fireStoreDB
                    .collection(Constants.COLLECTION_RETAILER_REPORT)
                    .document(retailerReportModel.retailerId!!)
                it.set(retailerReportRef, retailerReportModel)

                // Team Report
                val teamReportRef = fireStoreDB
                    .collection(Constants.COLLECTION_TEAM_REPORT)
                    .document(teamReportModel.teamId!!)
                it.set(teamReportRef, teamReportModel)
            }.await()
            StatefulResult.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }
}