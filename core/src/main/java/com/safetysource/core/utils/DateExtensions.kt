package com.safetysource.core.utils

import com.yariksoffice.lingver.Lingver
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun Long.getDateText(format: String): String {
    val df: DateFormat = SimpleDateFormat(format, Locale(Lingver.getInstance().getLanguage()))
    return df.format(this)
}