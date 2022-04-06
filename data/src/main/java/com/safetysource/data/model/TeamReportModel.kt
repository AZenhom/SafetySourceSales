package com.safetysource.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class TeamReportModel(
    var teamId: String? = null,
    var dueCommissionValue: Float? = null,
    var totalRedeemed: Float? = null,
    @ServerTimestamp
    var updatedAt: Date? = null,
) : Serializable