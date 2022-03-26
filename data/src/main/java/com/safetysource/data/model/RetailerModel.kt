package com.safetysource.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class RetailerModel(
    val id: String? = null,
    val teamId: String? = null,
    val name: String? = null,
    val phoneNo: String? = null,
    val contactNo: String? = null,
    @ServerTimestamp
    val createdAt: Date? = null,
) : Serializable