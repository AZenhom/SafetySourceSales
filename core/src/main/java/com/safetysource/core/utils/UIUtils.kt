package com.safetysource.core.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Insets
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowInsets
import androidx.fragment.app.FragmentActivity
import kotlin.math.roundToInt


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

fun convertDpToPixel(dp: Float): Int {
    val metrics = Resources.getSystem().displayMetrics
    val px = dp * (metrics.densityDpi / 160f)
    return px.roundToInt()
}