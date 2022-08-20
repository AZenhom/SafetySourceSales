package com.safetysource.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class RetailerModel(
    val id: String? = null,
    var teamId: String? = null,
    var name: String? = null,
    val phoneNo: String? = null,
    val contactNo: String? = null,
    var allowedProductIds: List<String>? = null,
    @ServerTimestamp
    val createdAt: Date? = null,
) : Serializable {
    companion object {
        const val TEAM_ID = "teamId"
        const val PHONE_NUMBER = "phoneNo"
    }
}