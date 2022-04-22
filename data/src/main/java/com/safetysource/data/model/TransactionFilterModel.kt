package com.safetysource.data.model

import java.io.Serializable
import java.util.*

data class TransactionFilterModel(
    var teamId: String? = null,
    var retailer: RetailerModel? = null,
    var category: ProductCategoryModel? = null,
    var product: ProductModel? = null,
    var serial: String? = null,
    var transactionType: TransactionType? = null,
    var dateFrom: Date? = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time,
    var dateTo: Date? = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.time
) : Serializable
