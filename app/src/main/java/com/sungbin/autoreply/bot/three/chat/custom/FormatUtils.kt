package com.sungbin.autoreply.bot.three.chat.custom

import java.text.SimpleDateFormat
import java.util.*

class FormatUtils private constructor() {
    companion object {
        fun getDurationString(seconds: Int): String {
            val date = Date(seconds * 1000.toLong())
            val formatter =
                SimpleDateFormat(if (seconds >= 3600) "HH:mm:ss" else "mm:ss", Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("GMT")
            return formatter.format(date)
        }
    }

    init {
        throw AssertionError()
    }
}