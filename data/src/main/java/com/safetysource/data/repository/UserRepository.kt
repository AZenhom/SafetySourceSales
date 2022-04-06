package com.safetysource.data.repository

import com.safetysource.data.base.BaseRepository
import com.safetysource.data.cache.UserDataStore
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDataStore: UserDataStore
) : BaseRepository() {
    suspend fun isSignedIn(): Boolean = execute {
        return@execute userDataStore.isPhoneSignedIn()
    }

    suspend fun setSignedIn(value: Boolean) = execute {
        userDataStore.setPhoneSignedIn(value)
    }

    suspend fun getUserId(): String? = execute {
        return@execute userDataStore.getUserId()
    }

    suspend fun setUserId(value: String?) = execute {
        userDataStore.setUserId(value ?: return@execute)
    }

    suspend fun getUserSigningInPhone(): String? = execute {
        return@execute userDataStore.getUserSigningInPhone()
    }

    suspend fun setUserSigningInPhone(value: String) = execute {
        userDataStore.setUserSigningInPhone(value)
    }

    suspend fun removeAllPref() = execute {
        return@execute userDataStore.removeAllPref()
    }
}