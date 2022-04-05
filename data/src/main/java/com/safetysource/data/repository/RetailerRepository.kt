package com.safetysource.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.safetysource.data.Constants
import com.safetysource.data.base.BaseRepository
import com.safetysource.data.cache.UserDataStore
import com.safetysource.data.model.RetailerModel
import com.safetysource.data.model.response.ErrorModel
import com.safetysource.data.model.response.StatefulResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RetailerRepository @Inject constructor(
    private val userDataStore: UserDataStore,
    private val fireStoreDB: FirebaseFirestore,
) : BaseRepository() {

    fun getNewRetailerId(): String {
        return fireStoreDB
            .collection(Constants.COLLECTION_RETAILER)
            .document()
            .id
    }

    suspend fun getRetailerById(retailerId: String): StatefulResult<RetailerModel> {
        return try {
            val document =
                fireStoreDB.collection(Constants.COLLECTION_RETAILER).document(retailerId).get()
                    .await()
            StatefulResult.Success(document.toObject(RetailerModel::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun getRetailerByPhoneNumber(phoneNumber: String): StatefulResult<RetailerModel> {
        return try {
            val document =
                fireStoreDB.collection(Constants.COLLECTION_RETAILER)
                    .whereEqualTo(RetailerModel.PHONE_NUMBER, phoneNumber)
                    .get().await().firstOrNull()
            StatefulResult.Success(document?.toObject(RetailerModel::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun getTeamRetailers(teamId: String): StatefulResult<List<RetailerModel>> {
        return try {
            val documents =
                fireStoreDB.collection(Constants.COLLECTION_RETAILER)
                    .whereEqualTo(RetailerModel.TEAM_ID, teamId)
                    .orderBy(Constants.CREATED_AT, Query.Direction.DESCENDING)
                    .get().await()
            val products = documents.toObjects(RetailerModel::class.java)
            StatefulResult.Success(products)
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun createUpdateRetailer(retailerModel: RetailerModel): StatefulResult<RetailerModel> {
        if (retailerModel.id.isNullOrEmpty())
            return StatefulResult.Error(ErrorModel.Unknown)
        return try {
            val retailerRef = fireStoreDB
                .collection(Constants.COLLECTION_RETAILER)
                .document(retailerModel.id)
            retailerRef.set(retailerModel).await()
            StatefulResult.Success(retailerRef.get().await().toObject(RetailerModel::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun getCurrentRetailerModel(): RetailerModel? = execute {
        return@execute userDataStore.getCurrentRetailerModel()
    }

    suspend fun setCurrentRetailerModel(value: RetailerModel?) = execute {
        userDataStore.setCurrentRetailerModel(value)
    }
}