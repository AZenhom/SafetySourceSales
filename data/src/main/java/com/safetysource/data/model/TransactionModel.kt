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
    var categoryId: String? = null,
    var productId: String? = null,
    var serial: String? = null,
    var offerSerials: List<OfferSerial>? = null,
    var offerId: String? = null,
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
        const val CATEGORY_ID = "categoryId"
        const val PRODUCT_ID = "productId"
        const val SERIAL = "serial"
        const val OFFER_ID = "offerId"
        const val TYPE = "type"
    }
}

data class OfferSerial(
    var transactionId: String? = null,
    var serial: String? = null,
    var transactionType: TransactionType? = null,
) : Serializable

enum class TransactionType : Serializable {
    SELLING,
    UNSELLING,
    OFFER_CLAIM,
}