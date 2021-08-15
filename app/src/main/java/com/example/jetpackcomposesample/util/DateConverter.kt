package com.example.jetpackcomposesample.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.TimeZone

object DateConverter {
    @SuppressLint("SimpleDateFormat")
    fun toJST(utc: String): String {
        val utcTimeZone = TimeZone.getTimeZone("UTC")
        val sourceFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val destFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        sourceFormat.timeZone = utcTimeZone
        val convertedDate = sourceFormat.parse(utc) ?: return ""
        return destFormat.format(convertedDate)
    }
}
