package com.safetysource.core.utils

import android.content.Context


fun getLocalizedNumbers(englishNumber: String?, context: Context): String {
    if (englishNumber.isNullOrEmpty()) return ""
    return if (context.isRTL()) {
        //getArabic number
        englishNumber
            .replace("0", "٠")
            .replace("1", "١")
            .replace("2", "٢")
            .replace("3", "٣")
            .replace("4", "٤")
            .replace("5", "٥")
            .replace("6", "٦")
            .replace("7", "٧")
            .replace("8", "٨")
            .replace("9", "٩")
    } else {
        englishNumber
    }
}

fun getLocalizedComma(context: Context): String = if (context.isRTL()) "، " else ", "

fun String.convertArabicNumbersIfExist(): String {
    var arabicStr = this
    arabicStr = arabicStr.replace("٫", ".")
    val chArr = arabicStr.toCharArray()
    val sb = StringBuilder()
    chArr.forEach { ch ->
        if (Character.isDigit(ch))
            sb.append(Character.getNumericValue(ch))
        else sb.append(ch)
    }
    return sb.toString()
}

fun String.getDigit(): String {
    var quote = this
    val nonDigits: MutableList<Char> = quote.toCharArray()
        .filterNot { Character.isDigit(it) }
        .toMutableList()
    nonDigits.forEach { ch -> quote = quote.replace(ch.toString(), "") }
    return quote
}