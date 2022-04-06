package com.safetysource.data.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class TransactionModel(
    var id: String? = null,
    var type: TransactionType? = null,
    var retailerId: String? = null,
    var teamId: String? = null,
    var productId: String? = null,
    var serial: String? = null,
    var commissionAppliedOrRemoved: Float? = null,
    var isUnsellingApproved: Boolean? = null,
    var unsellingApprovedByAdminId: String? = null,
    @ServerTimestamp
    var updatedAt: Date? = null,

    @get:Exclude
    var retailerModel: RetailerModel? = null,
    @get:Exclude
    var teamModel: TeamModel? = null,
    @get:Exclude
    var productModel: ProductModel? = null,
) : Serializable {
    companion object {
        const val RETAILER_ID = "retailerId"
        const val TEAM_ID = "teamId"
        const val PRODUCT_ID = "productId"
        const val SERIAL = "serial"
        const val TYPE = "type"
    }
}

enum class TransactionType : Serializable {
    SELLING,
    UNSELLING
}