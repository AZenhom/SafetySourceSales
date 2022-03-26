package com.safetysource.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class ProductItemModel(
    val serial: String? = null,
    val productId: String? = null,
    val state: ProductItemState? = null,
    @ServerTimestamp
    val updatedAt: Date? = null,
) : Serializable

enum class ProductItemState : Serializable {
    NOT_SOLD_YET,
    SOLD,
    PENDING_UNSELLING
}