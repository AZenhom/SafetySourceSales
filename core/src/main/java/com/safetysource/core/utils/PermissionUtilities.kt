package com.safetysource.core.utils

import android.Manifest
import android.content.Context
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.safetysource.core.R

fun AppCompatActivity.registerPermissions(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: (errorMessages: List<String>) -> Unit
) = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
) { permissions ->
    if (permissions.entries.isPermissionsGranted(this))
        onPermissionGranted()
    else
        onPermissionDenied(permissions.entries.getDeniedPermissionsMessages(this))
}

fun Fragment.registerPermissions(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: (errorMessages: List<String>) -> Unit
) = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
) { permissions ->
    if (permissions.entries.isPermissionsGranted(requireContext()))
        onPermissionGranted()
    else
        onPermissionDenied(permissions.entries.getDeniedPermissionsMessages(requireContext()))
}

fun <K, V> Set<Map.Entry<K, V>>.isPermissionsGranted(context: Context): Boolean {
    var granted = true
    this.forEach {
        if (it.value == false)
            granted = false
    }
    return granted
}

fun <K, V> Set<Map.Entry<K, V>>.getDeniedPermissionsMessages(context: Context): List<String> {
    val errorMessage: MutableList<String> = mutableListOf()
    this.forEach {
        if (it.value == false) {
            errorMessage.addIfNotExistAndNotNull(
                with(context) {
                    when (it.key) {
                        Manifest.permission.CAMERA ->
                            getString(R.string.error_missing_camera_permission)

                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE ->
                            getString(R.string.error_missing_storage_permission)
                        Manifest.permission.RECORD_AUDIO ->
                            getString(R.string.error_missing_microphone_permission)
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION ->
                            getString(R.string.error_missing_location_permission)
                        Manifest.permission.READ_CONTACTS ->
                            getString(R.string.error_missing_contact_permission)
                        else -> getString(R.string.error_missing_permission)
                    }
                }

            )
        }
    }
    return errorMessage.toList()
}