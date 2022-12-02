package com.safetysource.data.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class TeamModel(
    val id: String? = null,
    val name: String? = null,
    @ServerTimestamp
    val createdAt: Date? = null,

    @get:Exclude
    var teamReportModel: TeamReportModel? = null
) : Serializable {
    companion object {
        const val NAME = "name"
        const val TEAM_LESS = "TEAMLESS"
    }
}