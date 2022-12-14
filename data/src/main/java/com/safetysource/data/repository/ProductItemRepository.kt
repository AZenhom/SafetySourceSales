package com.safetysource.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.safetysource.data.Constants
import com.safetysource.data.base.BaseRepository
import com.safetysource.data.model.ProductItemModel
import com.safetysource.data.model.RetailerReportModel
import com.safetysource.data.model.TeamReportModel
import com.safetysource.data.model.TransactionModel
import com.safetysource.data.model.response.ErrorModel
import com.safetysource.data.model.response.StatefulResult
import kotlinx.coroutines.tasks.asDeferred
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

    suspend fun createUpdateMultipleProductItems(productItemModels: List<ProductItemModel>): StatefulResult<Unit> {
        productItemModels.forEach { productItemModel ->
            if (productItemModel.serial.isNullOrEmpty())
                return StatefulResult.Error(ErrorModel.Unknown)
        }
        return try {
            fireStoreDB.runBatch { batch ->
                productItemModels.forEach { productItemModel ->
                    val categoryRef = fireStoreDB
                        .collection(Constants.COLLECTION_PRODUCT_ITEM)
                        .document(productItemModel.serial!!)
                    batch.set(categoryRef, productItemModel)
                }
            }.await()
            StatefulResult.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun getProductItemBySerial(serial: String): StatefulResult<ProductItemModel> {
        return try {
            val document =
                fireStoreDB.collection(Constants.COLLECTION_PRODUCT_ITEM).document(serial).get()
                    .await()
            StatefulResult.Success(document.toObject(ProductItemModel::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun getProductItemsBySerials(serials: List<String>): StatefulResult<List<ProductItemModel>> {
        return try {
            val products = serials
                .chunked(10)
                .map {
                    fireStoreDB.collection(Constants.COLLECTION_PRODUCT_ITEM)
                        .whereIn(ProductItemModel.SERIAL, it)
                        .orderBy(Constants.UPDATED_AT, Query.Direction.DESCENDING)
                        .get().asDeferred()
                }
                .map { it.await().toObjects(ProductItemModel::class.java) }
                .flatten()
            StatefulResult.Success(products)
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

    suspend fun sellUnsellProductItem(
        productItemModel: ProductItemModel,
        transactionModel: TransactionModel,
        retailerReportModel: RetailerReportModel,
        teamReportModel: TeamReportModel
    ): StatefulResult<Unit> {
        if (productItemModel.serial.isNullOrEmpty())
            return StatefulResult.Error(ErrorModel.Unknown)
        return try {
            fireStoreDB.runBatch {
                // Product Item
                val categoryRef = fireStoreDB
                    .collection(Constants.COLLECTION_PRODUCT_ITEM)
                    .document(productItemModel.serial!!)
                it.set(categoryRef, productItemModel)

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