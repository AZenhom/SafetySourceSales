package com.safetysource.core.utils

import android.content.Context
import android.util.Patterns
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber

class PhoneNumberUtils {
    companion object PhoneNumberUtils{
        fun getPhoneIfValid(
            context: Context,
            countryCodeIso: String,
            phone: String
        ): Phonenumber.PhoneNumber? {
            val phoneUtil = PhoneNumberUtil.createInstance(context)
            if (countryCodeIso.isBlank() || phone.isBlank()) return null
            return try {
                when {
                    !Patterns.PHONE.matcher(phone).matches() -> null
                    phoneUtil == null -> return null
                    phone.length > 15 -> return null
                    else -> {
                        val phoneNumber = phoneUtil.parse(phone, countryCodeIso)
                        return if (phoneUtil.isValidNumberForRegion(
                                phoneNumber,
                                countryCodeIso
                            )
                        ) phoneNumber else null
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun Phonenumber.PhoneNumber?.isNull() = this == null
    }
}