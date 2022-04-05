package com.safetysource.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class ProductCategoryModel(
    val id: String? = null,
    val name: String? = null,
    val imgUrl: String? = null,
    val rank: Int? = null,
    @ServerTimestamp
    val updatedAt: Date? = null,
) : Serializable{
    companion object {
        const val RANK = "rank"
    }
}