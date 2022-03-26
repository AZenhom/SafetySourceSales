package com.safetysource.data.base

import com.safetysource.data.model.response.ErrorModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

abstract class BaseRepository {

    suspend fun <T> execute(job: suspend () -> T): T = withContext(Dispatchers.IO) {
        try {
            return@withContext job()
        } catch (e: Exception) {
            when (e) {
                is IOException -> throw ErrorModel.Connection
                //is HttpException -> throw getHttpErrorMessage(e)
            }
            throw e
        }
    }
}