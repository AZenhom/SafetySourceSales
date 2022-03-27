package com.safetysource.core.utils

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.View
import androidx.fragment.app.FragmentActivity
import kotlin.math.roundToInt
import android.view.WindowInsets

import android.view.WindowMetrics

import android.os.Build

import android.app.Activity
import android.graphics.Insets


fun Context.isRTL(): Boolean {
    val configuration = resources.configuration
    return configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
}

@Suppress("DEPRECATION")
fun getScreenWidth(fragmentActivity: FragmentActivity): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = fragmentActivity.windowManager.currentWindowMetrics
        val insets: Insets = windowMetrics.windowInsets
            .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        windowMetrics.bounds.width() - insets.left - insets.right
    } else {
        val displayMetrics = DisplayMetrics()
        fragmentActivity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.widthPixels
    }
}