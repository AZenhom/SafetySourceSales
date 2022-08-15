package com.safetysource.admin

import android.app.Application
import com.safetysource.data.cache.SettingsDataStore
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@HiltAndroidApp
@DelicateCoroutinesApi
class ApplicationClass: Application() {

    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch {
            try {
                Lingver.init(this@ApplicationClass, SettingsDataStore(this@ApplicationClass).getLanguage())
            } catch (e: Exception){
                // This catch is done to handle Lingver re-initiating in unit testing
            }
        }
    }
}