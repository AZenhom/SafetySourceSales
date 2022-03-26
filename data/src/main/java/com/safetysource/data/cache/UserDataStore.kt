package com.safetysource.data.cache

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.safetysource.data.model.AdminModel
import com.safetysource.data.model.RetailerModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "userDataStore")

class UserDataStore @Inject constructor(@ApplicationContext val context: Context) {

    private val isPhoneSignedInPref = booleanPreferencesKey("isPhoneSignedInPref")
    private val userIdPref = stringPreferencesKey("userIdPref")
    private val userSigningInPhonePref = stringPreferencesKey("userSigningInPhonePref")

    private val adminModelPref = stringPreferencesKey("adminModel")
    private val retailerModelPref = stringPreferencesKey("retailerModel")

    /** Common **/

    suspend fun removeAllPref() {
        context.userDataStore.edit {
            it.clear()
        }
    }

    suspend fun setPhoneSignedIn(value: Boolean) {
        context.userDataStore.edit {
            it[isPhoneSignedInPref] = value
        }
    }

    suspend fun isPhoneSignedIn(): Boolean {
        return context.userDataStore.data.map {
            it[isPhoneSignedInPref] ?: false
        }.first()
    }

    suspend fun setUserId(value: String) {
        context.userDataStore.edit {
            it[userIdPref] = value
        }
    }

    suspend fun getUserId(): String? {
        return context.userDataStore.data.map {
            it[userIdPref]
        }.first()
    }

    suspend fun setUserSigningInPhone(value: String) {
        context.userDataStore.edit {
            it[userSigningInPhonePref] = value
        }
    }

    suspend fun getUserSigningInPhone(): String? {
        return context.userDataStore.data.map {
            it[userSigningInPhonePref]
        }.first()
    }

    /** Admin App **/

    suspend fun setCurrentAdminModel(model: AdminModel?) {
        context.userDataStore.edit {
            val json: String = Gson().toJson(model, AdminModel::class.java)
            it[adminModelPref] = json
        }
    }

    suspend fun getCurrentAdminModel(): AdminModel? {
        return context.userDataStore.data.map {
            val json = it[adminModelPref]
            return@map if (json == null)
                null
            else
                Gson().fromJson(json, AdminModel::class.java)
        }.first()
    }

    /** Retailer App **/

    suspend fun setCurrentRetailerModel(model: RetailerModel?) {
        context.userDataStore.edit {
            val json: String = Gson().toJson(model, RetailerModel::class.java)
            it[retailerModelPref] = json
        }
    }

    suspend fun getCurrentRetailerModel(): RetailerModel? {
        return context.userDataStore.data.map {
            val json = it[retailerModelPref]
            return@map if (json == null)
                null
            else
                Gson().fromJson(json, RetailerModel::class.java)
        }.first()
    }
}