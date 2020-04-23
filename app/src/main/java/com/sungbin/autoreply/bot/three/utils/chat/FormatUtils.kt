package com.sungbin.autoreply.bot.three.utils.chat

import java.text.SimpleDateFormat
import java.util.*

object FormatUtils {
    fun getDurationString(seconds: Int): String {
        val date = Date(seconds * 1000.toLong())
        val formatter =
            SimpleDateFormat(if (seconds >= 3600) "HH:mm:ss" else "mm:ss", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("GMT")
        return formatter.format(date)
    }

    fun createDate(date: Date): String{
        val formatter = SimpleDateFormat("a hh:mm", Locale.KOREA)
        formatter.timeZone = TimeZone.getTimeZone("GMT+0900")
        return formatter.format(date)
    }
}