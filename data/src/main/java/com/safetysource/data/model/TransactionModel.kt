package com.safetysource.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class TransactionModel(
    val id: String? = null,
    val type: String? = null,
    val sellerId: String? = null,
    val teamId: String? = null,
    val productId: String? = null,
    val serial: String? = null,
    val commissionAppliedOrRemoved: Float? = null,
    val isUnsellingApproved: Boolean? = null,
    val unsellingApprovedByAdminId: String? = null,
    @ServerTimestamp
    val updatedAt: Date? = null,
) : Serializable

enum class TransactionType : Serializable {
    SELLING,
    UNSELLING
}