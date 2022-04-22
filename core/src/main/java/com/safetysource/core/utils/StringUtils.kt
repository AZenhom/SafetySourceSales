package com.safetysource.core.utils

import android.content.Context
import com.safetysource.core.R
import com.safetysource.data.model.TransactionFilterModel


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

fun TransactionFilterModel.toString(context: Context): String = with(context) {
    val comma = getLocalizedComma(this)
    var text = ""
    teamId?.let { text += (getString(R.string.team) + comma) }
    retailer?.let { text += (getString(R.string.retailer) + comma) }
    category?.let { text += (getString(R.string.product_category) + comma) }
    product?.let { text += (getString(R.string.product) + comma) }
    serial?.let { text += (getString(R.string.serial) + comma) }
    transactionType?.let { text += (getString(R.string.transaction_type) + comma) }
    dateFrom?.let { text += (getString(R.string.date_from) + " " + it.time.getDateText("dd-MM-yyyy") + comma) }
    dateTo?.let { text += (getString(R.string.date_to) + " " + it.time.getDateText("dd-MM-yyyy") + comma) }
    return if (text.isEmpty()) text
    else getString(R.string.transactions_filtered_by) + " " + text.substring(0, text.length - 2)
}