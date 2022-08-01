package com.safetysource.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class SubscribedOfferModel(
    val id: String? = null,
    val offerId: String? = null,
    val retailerId: String? = null,
    val teamId: String? = null,
    val claimedOrRemoved: Boolean? = null,
    @ServerTimestamp
    val updatedAt: Date? = null,
) : Serializable {
    companion object {
        const val RETAILER_ID = "retailerId"
        const val CLAIMED_OR_REMOVED = "claimedOrRemoved"
    }
}
