package com.safetysource.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class TeamModel(
    val id: String? = null,
    val name: String? = null,
    @ServerTimestamp
    val created: Date? = null,
) : Serializable {
    companion object {
        const val NAME = "name"
    }
}