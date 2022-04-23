package com.safetysource.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class ProductItemModel(
    var serial: String? = null,
    var productId: String? = null,
    var state: ProductItemState? = null,
    var lastSoldByRetailerId: String? = null,
    @ServerTimestamp
    var updatedAt: Date? = null,
) : Serializable{
    companion object{
        const val PRODUCT_ID = "productId"
    }
}

enum class ProductItemState : Serializable {
    NOT_SOLD_YET,
    SOLD,
    PENDING_UNSELLING
}