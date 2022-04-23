package com.safetysource.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.safetysource.data.Constants
import com.safetysource.data.base.BaseRepository
import com.safetysource.data.cache.UserDataStore
import com.safetysource.data.model.AdminModel
import com.safetysource.data.model.response.ErrorModel
import com.safetysource.data.model.response.StatefulResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AdminRepository @Inject constructor(
    private val userDataStore: UserDataStore,
    private val fireStoreDB: FirebaseFirestore
) : BaseRepository() {

    fun getNewAdminId(): String {
        return fireStoreDB
            .collection(Constants.COLLECTION_ADMIN)
            .document()
            .id
    }

    suspend fun getAdminById(adminId: String): StatefulResult<AdminModel> {
        return try {
            val document =
                fireStoreDB.collection(Constants.COLLECTION_ADMIN).document(adminId).get().await()
            StatefulResult.Success(document.toObject(AdminModel::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun getAdminByPhoneNumber(phoneNumber: String): StatefulResult<AdminModel> {
        return try {
            val document =
                fireStoreDB.collection(Constants.COLLECTION_ADMIN)
                    .whereEqualTo(AdminModel.PHONE_NUMBER, phoneNumber)
                    .get().await().firstOrNull()
            StatefulResult.Success(document?.toObject(AdminModel::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun createUpdateAdmin(adminModel: AdminModel): StatefulResult<AdminModel> {
        if (adminModel.id.isNullOrEmpty())
            return StatefulResult.Error(ErrorModel.Unknown)
        return try {
            val adminRef = fireStoreDB
                .collection(Constants.COLLECTION_ADMIN)
                .document(adminModel.id)
            adminRef.set(adminModel).await()
            StatefulResult.Success(adminRef.get().await().toObject(AdminModel::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun getCurrentAdminModel(): AdminModel? = execute {
        return@execute userDataStore.getCurrentAdminModel()
    }

    suspend fun setCurrentAdminModel(value: AdminModel?) = execute {
        userDataStore.setCurrentAdminModel(value)
    }
}