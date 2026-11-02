package com.example.ui.components

import java.text.DecimalFormat

object PersianNumberHelper {
    private val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    fun toPersianDigits(text: String): String {
        val sb = StringBuilder(text.length)
        for (ch in text) {
            if (ch in '0'..'9') {
                sb.append(persianDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun toPersianDigits(number: Long): String {
        return toPersianDigits(number.toString())
    }

    fun toPersianDigits(number: Int): String {
        return toPersianDigits(number.toString())
    }

    fun toPersianDigits(number: Double, decimals: Int = 1): String {
        val formatStr = if (decimals == 0) "#,##0" else "#,##0." + "0".repeat(decimals)
        val df = DecimalFormat(formatStr)
        return toPersianDigits(df.format(number))
    }

    fun formatToman(amount: Long): String {
        val df = DecimalFormat("#,###")
        return toPersianDigits(df.format(amount)) + " تومان"
    }

    fun formatWeightKg(weight: Double): String {
        val df = DecimalFormat("#,##0.0")
        return toPersianDigits(df.format(weight)) + " کیلوگرم"
    }

    fun formatRfid(code: String): String {
        return toPersianDigits(code)
    }

    fun formatDistance(km: Int): String {
        return toPersianDigits(km) + " کیلومتر"
    }
}
