package com.safetysource.data.repository

import android.net.Uri
import androidx.core.net.toFile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.safetysource.data.Constants
import com.safetysource.data.base.BaseRepository
import com.safetysource.data.model.AdminModel
import com.safetysource.data.model.ProductModel
import com.safetysource.data.model.response.ErrorModel
import com.safetysource.data.model.response.StatefulResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val fireStoreDB: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) : BaseRepository() {

    fun getNewProductId(): String {
        return fireStoreDB
            .collection(Constants.COLLECTION_PRODUCT)
            .document()
            .id
    }

    suspend fun uploadProductImageAndGetUrl(
        productId: String,
        fileUri: Uri
    ): StatefulResult<String> {
        val extension = fileUri.toFile().extension
        if (productId.isEmpty() || extension.isEmpty())
            return StatefulResult.Error(ErrorModel.Unknown)
        return try {
            val downloadUrl = firebaseStorage.reference
                .child(Constants.FOLDER_PRODUCTS)
                .child("$productId.$extension")
                .putFile(fileUri).await()
                .storage.downloadUrl.await().toString()
            StatefulResult.Success(downloadUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun createUpdateProduct(productModel: ProductModel): StatefulResult<Unit> {
        if (productModel.id.isNullOrEmpty())
            return StatefulResult.Error(ErrorModel.Unknown)
        return try {
            val productRef = fireStoreDB
                .collection(Constants.COLLECTION_PRODUCT)
                .document(productModel.id)
            productRef.set(productModel).await()
            StatefulResult.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun getCategoryProducts(categoryId: String): StatefulResult<List<ProductModel>> {
        return try {
            val documents =
                fireStoreDB.collection(Constants.COLLECTION_PRODUCT)
                    .whereEqualTo(ProductModel.CATEGORY_ID, categoryId)
                    .orderBy(Constants.UPDATED_AT, Query.Direction.DESCENDING)
                    .get().await()
            val products = documents.toObjects(ProductModel::class.java)
            StatefulResult.Success(products)
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun getProductById(productId: String): StatefulResult<ProductModel> {
        return try {
            val document =
                fireStoreDB.collection(Constants.COLLECTION_PRODUCT).document(productId).get().await()
            StatefulResult.Success(document.toObject(ProductModel::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }
}