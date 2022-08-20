package com.safetysource.data.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class OfferModel(
    val id: String? = null,
    val imgUrl: String? = null,
    val text: String? = null,
    var productCategoryId: String? = null,
    var productId: String? = null,
    val neededSellCount: Int? = null,
    val canRepeat: Boolean? = null,
    val valPerRepeat: Int? = null,
    var startsAt: Date? = null,
    var expiresAt: Date? = null,
    @ServerTimestamp
    val updatedAt: Date? = null,

    @get:Exclude
    var subscribedOfferModel: SubscribedOfferModel? = null,
) : Serializable {
    companion object {
        const val STARTS_AT = "startsAt"
        const val EXPIRES_AT = "expiresAt"
    }
}
