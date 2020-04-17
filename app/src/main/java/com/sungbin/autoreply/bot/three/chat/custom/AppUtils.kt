package com.sungbin.autoreply.bot.three.chat.custom

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

object AppUtils {
    fun showToast(
        context: Context,
        @StringRes text: Int,
        isLong: Boolean
    ) {
        showToast(context, context.getString(text), isLong)
    }

    fun showToast(
        context: Context?,
        text: String?,
        isLong: Boolean
    ) {
        Toast.makeText(context, text, if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
    }
}