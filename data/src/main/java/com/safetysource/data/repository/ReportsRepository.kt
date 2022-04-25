package com.safetysource.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.safetysource.data.Constants
import com.safetysource.data.base.BaseRepository
import com.safetysource.data.model.RetailerReportModel
import com.safetysource.data.model.TeamReportModel
import com.safetysource.data.model.response.ErrorModel
import com.safetysource.data.model.response.StatefulResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReportsRepository @Inject constructor(
    private val fireStoreDB: FirebaseFirestore,
) : BaseRepository() {

    suspend fun createUpdateTeamReport(teamReportModel: TeamReportModel): StatefulResult<Unit> {
        if (teamReportModel.teamId.isNullOrEmpty())
            return StatefulResult.Error(ErrorModel.Unknown)
        return try {
            val categoryRef = fireStoreDB
                .collection(Constants.COLLECTION_TEAM_REPORT)
                .document(teamReportModel.teamId!!)
            categoryRef.set(teamReportModel).await()
            StatefulResult.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun createUpdateRetailerReport(retailerReportModel: RetailerReportModel): StatefulResult<Unit> {
        if (retailerReportModel.retailerId.isNullOrEmpty())
            return StatefulResult.Error(ErrorModel.Unknown)
        return try {
            val categoryRef = fireStoreDB
                .collection(Constants.COLLECTION_RETAILER_REPORT)
                .document(retailerReportModel.retailerId!!)
            categoryRef.set(retailerReportModel).await()
            StatefulResult.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun getTeamReportById(teamId: String): StatefulResult<TeamReportModel> {
        return try {
            val document =
                fireStoreDB.collection(Constants.COLLECTION_TEAM_REPORT).document(teamId).get().await()
            StatefulResult.Success(document.toObject(TeamReportModel::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun getRetailerReportById(retailerId: String): StatefulResult<RetailerReportModel> {
        return try {
            val document =
                fireStoreDB.collection(Constants.COLLECTION_RETAILER_REPORT).document(retailerId).get().await()
            StatefulResult.Success(document.toObject(RetailerReportModel::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }
}