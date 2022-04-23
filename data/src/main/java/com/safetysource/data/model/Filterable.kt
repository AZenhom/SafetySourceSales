package com.safetysource.data.model

import com.google.firebase.firestore.Exclude

interface Filterable {
    @Exclude
    fun getFilterableId(): String?
    @Exclude
    fun getFilterableName(): String?
}