package com.safetysource.data.repository

import android.net.Uri
import androidx.core.net.toFile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.safetysource.data.Constants
import com.safetysource.data.base.BaseRepository
import com.safetysource.data.model.ProductCategoryModel
import com.safetysource.data.model.response.ErrorModel
import com.safetysource.data.model.response.StatefulResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProductCategoryRepository @Inject constructor(
    private val fireStoreDB: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) : BaseRepository() {

    fun getNewProductCategoryId(): String {
        return fireStoreDB
            .collection(Constants.COLLECTION_PRODUCT_CATEGORY)
            .document()
            .id
    }

    suspend fun uploadProductCategoryImageAndGetUrl(
        categoryId: String,
        fileUri: Uri
    ): StatefulResult<String> {
        val extension = fileUri.toFile().extension
        if (categoryId.isEmpty() || extension.isEmpty())
            return StatefulResult.Error(ErrorModel.Unknown)
        return try {
            val downloadUrl = firebaseStorage.reference
                .child(Constants.FOLDER_PRODUCT_CATEGORIES)
                .child("$categoryId.$extension")
                .putFile(fileUri).await()
                .storage.downloadUrl.await().toString()
            StatefulResult.Success(downloadUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun createUpdateProductCategory(productCategoryModel: ProductCategoryModel): StatefulResult<Unit> {
        if (productCategoryModel.id.isNullOrEmpty())
            return StatefulResult.Error(ErrorModel.Unknown)
        return try {
            val categoryRef = fireStoreDB
                .collection(Constants.COLLECTION_PRODUCT_CATEGORY)
                .document(productCategoryModel.id)
            categoryRef.set(productCategoryModel).await()
            StatefulResult.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun getProductCategories(): StatefulResult<List<ProductCategoryModel>> {
        return try {
            val documents =
                fireStoreDB.collection(Constants.COLLECTION_PRODUCT_CATEGORY)
                    .orderBy(ProductCategoryModel.RANK, Query.Direction.DESCENDING)
                    .orderBy(Constants.CREATED_AT, Query.Direction.DESCENDING)
                    .get().await()
            StatefulResult.Success(documents.toObjects(ProductCategoryModel::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun getProductCategoryById(categoryId: String): StatefulResult<ProductCategoryModel> {
        return try {
            val document =
                fireStoreDB.collection(Constants.COLLECTION_PRODUCT_CATEGORY).document(categoryId).get().await()
            StatefulResult.Success(document.toObject(ProductCategoryModel::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }
}