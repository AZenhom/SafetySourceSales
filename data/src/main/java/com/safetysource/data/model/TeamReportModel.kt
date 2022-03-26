package com.safetysource.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class TeamReportModel(
    val teamId: String? = null,
    val totalCommissionValue: Float? = null,
    val totalRedeemed: Float? = null,
    @ServerTimestamp
    val updatedAt: Date? = null,
) : Serializable