package com.safetysource.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class ProductModel(
    val id: String? = null,
    val name: String? = null,
    val imgUrl: String? = null,
    val wholesalePrice: Float? = null,
    val productCategoryId: String? = null,
    val commissionValue: Float? = null,
    val lastUpdatedByAdminId: String? = null,
    @ServerTimestamp
    val updatedAt: Date? = null,
) : Serializable, Filterable {
    companion object {
        const val CATEGORY_ID = "productCategoryId"
    }

    override fun getFilterableId(): String? = id

    override fun getFilterableName(): String? = name
}