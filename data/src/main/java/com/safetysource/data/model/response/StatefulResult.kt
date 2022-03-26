package com.safetysource.data.model.response

sealed class StatefulResult<out T>(
    val data: T? = null,
    val code: Int? = null,
    val message: String? = null,
    val messageRes: Int? = null,
    var otherData: Any? = null,
    var errorModel: ErrorModel? = null,
) {
    class Success<T>(
        data: T?,
        code: Int? = null,
        message: String? = null,
        messageRes: Int? = null,
        otherData: Any? = null
    ) : StatefulResult<T>(data, code, message, messageRes, otherData)

    class Error<T>(
        errorModel: ErrorModel? = null,
        otherData: Any? = null,
    ) : StatefulResult<T>(errorModel = errorModel, otherData = otherData)

    class Loading<T> : StatefulResult<T>()
}