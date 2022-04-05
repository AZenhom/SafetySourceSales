package com.safetysource.core.utils

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.safetysource.core.R

fun <T> MutableList<T>.addIfNotExist(item: T) {
    if (!this.contains(item)) this.add(item)
}

fun <T> MutableList<T>.addIfNotExistAndNotNull(item: T?) {
    if (!this.contains(item) && item != null) this.add(item)
}

fun <T> MutableList<T>.addIfNotExist(item: T, index: Int) {
    if (!this.contains(item)) this.add(index, item)
}

fun <T> MutableList<T>.removeIfExist(item: T?) {
    if (item != null && this.contains(item)) this.remove(item)
}

fun <T> MutableLiveData<T>.mutation(actions: (MutableLiveData<T>) -> Unit) {
    actions(this)
    this.value = this.value
}

fun <T> MutableList<T>.containsOnly(item: T): Boolean = this.size == 1 && this.contains(item)

fun <T> MutableList<T>?.contains(item: T): Boolean = !this.isNullOrEmpty() && this.contains(item)

fun List<String>.getJoinedText(context: Context, limit: Int): String {
    var shownText = ""
    var charCount: Int
    when {
        this.size == 1 -> shownText = this[0]
        this.size > 1 -> {
            shownText = this[0]
            charCount = shownText.length
            if (this.size > 1) {
                for (i in 1 until this.size) {
                    val item: String = this[i]
                    if (charCount + item.length <= limit) {
                        shownText = "$shownText${getLocalizedComma(context)} $item"
                        charCount += item.length
                    } else {
                        shownText += context.getString(
                            R.string.plus_count_more,
                            (this.size - i).toString()
                        )
                        break
                    }
                }
            }
        }
        else -> shownText = ""
    }
    return shownText
}

fun String?.indexesOf(word: String): List<Int> =
    word.toRegex(setOf(RegexOption.IGNORE_CASE))
        .findAll(this ?: "")
        .map { it.range.first }
        .toList()
