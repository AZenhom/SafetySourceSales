package com.safetysource.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class AdminModel(
    val id: String? = null,
    val name: String? = null,
    val role: AdminRole? = null,
    val phoneNo: String? = null,
    @ServerTimestamp
    val createdAt: Date? = null,
) : Serializable {
    companion object {
        const val PHONE_NUMBER = "phoneNo"
    }
}

enum class AdminRole : Serializable {
    SUPER_ADMIN,
    SECONDARY_ADMIN,
}
