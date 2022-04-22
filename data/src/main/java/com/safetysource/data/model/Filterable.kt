package com.safetysource.data.model

interface Filterable {
    fun getFilterableId(): String?
    fun getFilterableName(): String?
}