package com.safetysource.data.repository

import com.safetysource.data.base.BaseRepository
import com.safetysource.data.cache.SettingsDataStore
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : BaseRepository() {

    suspend fun showOnBoarding(): Boolean = execute {
        return@execute settingsDataStore.showOnBoarding()
    }

    suspend fun setShowOnBoarding(value: Boolean) = execute {
        settingsDataStore.setShowOnBoarding(value)
    }
}