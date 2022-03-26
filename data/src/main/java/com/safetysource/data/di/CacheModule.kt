package com.safetysource.data.di

import android.content.Context
import com.safetysource.data.cache.SettingsDataStore
import com.safetysource.data.cache.UserDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CacheModule {

    @Singleton
    @Provides
    fun getUserDataStore(@ApplicationContext context: Context) = UserDataStore(context)

    @Singleton
    @Provides
    fun getSettingsDataStore(@ApplicationContext context: Context) = SettingsDataStore(context)

}