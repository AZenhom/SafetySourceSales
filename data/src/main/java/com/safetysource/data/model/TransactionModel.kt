package com.safetysource.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class TransactionModel(
    val id: String? = null,
    val type: TransactionType? = null,
    val retailerId: String? = null,
    val teamId: String? = null,
    val productId: String? = null,
    val serial: String? = null,
    val commissionAppliedOrRemoved: Float? = null,
    val isUnsellingApproved: Boolean? = null,
    val unsellingApprovedByAdminId: String? = null,
    @ServerTimestamp
    val updatedAt: Date? = null,
) : Serializable {
    companion object {
        const val RETAILER_ID = "retailerId"
        const val TEAM_ID = "teamId"
        const val TYPE = "type"
    }
}

enum class TransactionType : Serializable {
    SELLING,
    UNSELLING
}