package com.safetysource.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.safetysource.data.Constants
import com.safetysource.data.base.BaseRepository
import com.safetysource.data.model.TransactionFilterModel
import com.safetysource.data.model.TransactionModel
import com.safetysource.data.model.response.ErrorModel
import com.safetysource.data.model.response.StatefulResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TransactionsRepository @Inject constructor(
    private val fireStoreDB: FirebaseFirestore,
) : BaseRepository() {

    fun getNewTransactionId(): String {
        return fireStoreDB
            .collection(Constants.COLLECTION_TRANSACTION)
            .document()
            .id
    }

    suspend fun createUpdateTransaction(transactionModel: TransactionModel): StatefulResult<Unit> {
        if (transactionModel.id.isNullOrEmpty())
            return StatefulResult.Error(ErrorModel.Unknown)
        return try {
            val categoryRef = fireStoreDB
                .collection(Constants.COLLECTION_TRANSACTION)
                .document(transactionModel.id!!)
            categoryRef.set(transactionModel).await()
            StatefulResult.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun getTransactions(transactionFilterModel: TransactionFilterModel): StatefulResult<List<TransactionModel>> {
        return try {
            val reference =
                fireStoreDB.collection(Constants.COLLECTION_TRANSACTION)
            var query: Query? = null

            with(transactionFilterModel) {
                teamId?.let {
                    query = (query ?: reference).whereEqualTo(TransactionModel.TEAM_ID, teamId)
                }
                retailer?.let {
                    query = (query ?: reference).whereEqualTo(TransactionModel.RETAILER_ID, it.id)
                }
                category?.let {
                    query = (query ?: reference).whereEqualTo(TransactionModel.CATEGORY_ID, it.id)
                }
                product?.let {
                    query = (query ?: reference).whereEqualTo(TransactionModel.PRODUCT_ID, it.id)
                }
                serial?.let {
                    query = (query ?: reference).whereEqualTo(TransactionModel.SERIAL, it)
                }
                transactionType?.let {
                    query = (query ?: reference).whereEqualTo(TransactionModel.TYPE, it)
                }
                dateFrom?.let {
                    query = (query ?: reference).whereGreaterThanOrEqualTo(Constants.UPDATED_AT, it)
                }
                dateTo?.let {
                    query = (query ?: reference).whereLessThanOrEqualTo(Constants.UPDATED_AT, it)
                }
            }

            val documents =
                (query ?: reference).orderBy(Constants.UPDATED_AT, Query.Direction.DESCENDING)
                    .get().await()

            StatefulResult.Success(documents.toObjects(TransactionModel::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun getTransactionById(transactionId: String): StatefulResult<TransactionModel> {
        return try {
            val document =
                fireStoreDB.collection(Constants.COLLECTION_TRANSACTION).document(transactionId)
                    .get().await()
            StatefulResult.Success(document.toObject(TransactionModel::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }
}