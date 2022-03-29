package com.safetysource.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.safetysource.data.Constants
import com.safetysource.data.base.BaseRepository
import com.safetysource.data.model.TeamModel
import com.safetysource.data.model.response.ErrorModel
import com.safetysource.data.model.response.StatefulResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TeamRepository @Inject constructor(
    private val fireStoreDB: FirebaseFirestore,
) : BaseRepository() {

    suspend fun createUpdateTeam(teamModel: TeamModel): StatefulResult<Unit> {
        if (teamModel.id.isNullOrEmpty())
            return StatefulResult.Error(ErrorModel.Unknown)
        return try {
            val categoryRef = fireStoreDB
                .collection(Constants.COLLECTION_TEAM)
                .document(teamModel.id)
            categoryRef.set(teamModel).await()
            StatefulResult.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun getTeams(): StatefulResult<List<TeamModel>> {
        return try {
            val documents =
                fireStoreDB.collection(Constants.COLLECTION_TEAM)
                    .orderBy(TeamModel.NAME, Query.Direction.ASCENDING)
                    .get().await()
            StatefulResult.Success(documents.toObjects(TeamModel::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }

    suspend fun getTeamById(teamId: String): StatefulResult<TeamModel> {
        return try {
            val document =
                fireStoreDB.collection(Constants.COLLECTION_TEAM).document(teamId).get().await()
            StatefulResult.Success(document.toObject(TeamModel::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            StatefulResult.Error(ErrorModel.Unknown)
        }
    }
}