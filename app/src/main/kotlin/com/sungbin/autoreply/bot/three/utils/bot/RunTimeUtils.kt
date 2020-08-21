package com.sungbin.autoreply.bot.three.utils.bot

import android.content.Context
import com.sungbin.sungbintool.DataUtils
import java.text.SimpleDateFormat
import java.util.*

object RunTimeUtils {
    private fun getTime(): String {
        val format = SimpleDateFormat("a h:m", Locale.KOREA)
        return format.format(Date())
    }

    fun save(context: Context, name: String) {
        DataUtils.saveData(context, "$name - runtime", getTime())
    }

    fun get(context: Context, name: String): String {
        return DataUtils.readData(context, "$name - runtime", "마지막 작동: 없음")
    }
}