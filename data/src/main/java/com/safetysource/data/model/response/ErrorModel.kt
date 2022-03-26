package com.safetysource.data.model.response

import java.lang.Exception

sealed class ErrorModel: Exception() {
    object Unknown: ErrorModel()
    object Connection: ErrorModel()
    class Network(val code: Int, val serverMessage: String?): ErrorModel()
}