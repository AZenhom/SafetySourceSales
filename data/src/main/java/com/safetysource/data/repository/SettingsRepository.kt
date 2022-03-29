package com.safetysource.data.repository

import com.safetysource.data.base.BaseRepository
import com.safetysource.data.cache.SettingsDataStore
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : BaseRepository() {

    suspend fun getCurrentLanguage(): String = execute {
        return@execute settingsDataStore.getLanguage()
    }

    suspend fun setCurrentLanguage(value: String) = execute {
        settingsDataStore.setLanguage(value)
    }
}