package com.safetysource.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.safetysource.data.Constants
import com.safetysource.data.base.BaseRepository
import com.safetysource.data.model.ProductItemModel
import com.safetysource.data.model.response.ErrorModel
import com.safetysource.data.model.response.StatefulResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProductItemRepository @Inject constructor(
    private val fireStoreDB: FirebaseFirestore,
) : BaseRepository() {

    suspend fun createUpdateProductItem(productItemModel: ProductItemModel): StatefulResult<Unit> {
        if (productItemModel.serial.isNullOrEmpty())
            return StatefulResult.Error(ErrorModel.Unknown)
        return try {
            val categoryRef = fireStoreDB
                .collection(Constants.COLLECTION_PRODUCT_ITEM)
                .document(productItemModel.serial!!)
            categoryRef.set(productItemModel).await()
            StatefulResult.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun getProductItemBySerial(serial: String): StatefulResult<ProductItemModel> {
        return try {
            val document =
                fireStoreDB.collection(Constants.COLLECTION_PRODUCT_ITEM).document(serial).get().await()
            StatefulResult.Success(document.toObject(ProductItemModel::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun getProductItems(productId: String): StatefulResult<List<ProductItemModel>> {
        return try {
            val documents =
                fireStoreDB.collection(Constants.COLLECTION_PRODUCT_ITEM)
                    .whereEqualTo(ProductItemModel.PRODUCT_ID, productId)
                    .orderBy(Constants.UPDATED_AT, Query.Direction.DESCENDING)
                    .get().await()
            val products = documents.toObjects(ProductItemModel::class.java)
            StatefulResult.Success(products)
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }
}