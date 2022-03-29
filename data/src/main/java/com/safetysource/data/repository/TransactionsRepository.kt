package com.safetysource.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.safetysource.data.Constants
import com.safetysource.data.base.BaseRepository
import com.safetysource.data.model.TransactionModel
import com.safetysource.data.model.TransactionType
import com.safetysource.data.model.response.ErrorModel
import com.safetysource.data.model.response.StatefulResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TransactionsRepository @Inject constructor(
    private val fireStoreDB: FirebaseFirestore,
) : BaseRepository() {

    suspend fun createUpdateTransaction(transactionModel: TransactionModel): StatefulResult<Unit> {
        if (transactionModel.id.isNullOrEmpty())
            return StatefulResult.Error(ErrorModel.Unknown)
        return try {
            val categoryRef = fireStoreDB
                .collection(Constants.COLLECTION_TRANSACTION)
                .document(transactionModel.id)
            categoryRef.set(transactionModel).await()
            StatefulResult.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun getTransactions(
        teamId: String? = null,
        retailerId: String? = null,
        productId: String? = null,
        transactionType: TransactionType? = null
    ): StatefulResult<List<TransactionModel>> {
        return try {
            val reference =
                fireStoreDB.collection(Constants.COLLECTION_TRANSACTION)
            var query: Query? = null

            teamId?.let {
                query = reference.whereEqualTo(TransactionModel.TEAM_ID, teamId)
            }
            retailerId?.let {
                query = reference.whereEqualTo(TransactionModel.RETAILER_ID, retailerId)
            }
            productId?.let {
                query = reference.whereEqualTo(TransactionModel.PRODUCT_ID, productId)
            }
            transactionType?.let {
                query = reference.whereEqualTo(TransactionModel.TYPE, transactionType)
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