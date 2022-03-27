package com.safetysource.data.repository

import com.google.firebase.firestore.FirebaseFirestore
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

    suspend fun getRetailerById(retailerId: String): StatefulResult<RetailerModel> {
        return try {
            val document =
                fireStoreDB.collection(Constants.COLLECTION_RETAILER).document(retailerId).get().await()
            StatefulResult.Success(document.toObject(RetailerModel::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun registerRetailer(retailerModel: RetailerModel): StatefulResult<RetailerModel> {
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