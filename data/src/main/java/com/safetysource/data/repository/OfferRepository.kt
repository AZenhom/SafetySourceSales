package com.safetysource.data.repository

import android.net.Uri
import androidx.core.net.toFile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.safetysource.data.Constants
import com.safetysource.data.base.BaseRepository
import com.safetysource.data.model.OfferModel
import com.safetysource.data.model.response.ErrorModel
import com.safetysource.data.model.response.StatefulResult
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class OfferRepository @Inject constructor(
    private val fireStoreDB: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) : BaseRepository() {

    fun getNewOfferId(): String {
        return fireStoreDB
            .collection(Constants.COLLECTION_OFFER)
            .document()
            .id
    }

    suspend fun getOfferById(offerId: String): StatefulResult<OfferModel> {
        return try {
            val document =
                fireStoreDB.collection(Constants.COLLECTION_OFFER).document(offerId).get()
                    .await()
            StatefulResult.Success(document.toObject(OfferModel::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun uploadOfferImageAndGetUrl(
        offerId: String,
        fileUri: Uri
    ): StatefulResult<String> {
        val extension = fileUri.toFile().extension
        if (offerId.isEmpty() || extension.isEmpty())
            return StatefulResult.Error(ErrorModel.Unknown)
        return try {
            val downloadUrl = firebaseStorage.reference
                .child(Constants.FOLDER_OFFERS)
                .child("$offerId.$extension")
                .putFile(fileUri).await()
                .storage.downloadUrl.await().toString()
            StatefulResult.Success(downloadUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun createUpdateOffer(offerModel: OfferModel): StatefulResult<Unit> {
        if (offerModel.id.isNullOrEmpty())
            return StatefulResult.Error(ErrorModel.Unknown)
        return try {
            val productRef = fireStoreDB
                .collection(Constants.COLLECTION_OFFER)
                .document(offerModel.id)
            productRef.set(offerModel).await()
            StatefulResult.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun getAllAvailableOffers(): StatefulResult<List<OfferModel>> {
        return try {
            val dateNow = Calendar.getInstance().time
            val documents =
                fireStoreDB.collection(Constants.COLLECTION_OFFER)
                    .whereLessThan(OfferModel.STARTS_AT, dateNow)
                    .whereGreaterThan(OfferModel.EXPIRES_AT, dateNow)
                    .orderBy(Constants.UPDATED_AT, Query.Direction.DESCENDING)
                    .get().await()
            StatefulResult.Success(documents.toObjects(OfferModel::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun getAllUpcomingOffers(): StatefulResult<List<OfferModel>> {
        return try {
            val dateNow = Calendar.getInstance().time
            val documents =
                fireStoreDB.collection(Constants.COLLECTION_OFFER)
                    .whereGreaterThan(OfferModel.STARTS_AT, dateNow)
                    .orderBy(Constants.UPDATED_AT, Query.Direction.DESCENDING)
                    .get().await()
            StatefulResult.Success(documents.toObjects(OfferModel::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun getAllHistoryOffers(): StatefulResult<List<OfferModel>> {
        return try {
            val dateNow = Calendar.getInstance().time
            val documents =
                fireStoreDB.collection(Constants.COLLECTION_OFFER)
                    .whereLessThan(OfferModel.EXPIRES_AT, dateNow)
                    .orderBy(Constants.UPDATED_AT, Query.Direction.DESCENDING)
                    .get().await()
            StatefulResult.Success(documents.toObjects(OfferModel::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }
}