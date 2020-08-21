package com.sungbin.autoreply.bot.three.utils

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


object Base64Utils {

    fun bitmap2base64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.PNG, 100, baos)
        return Base64.encodeToString(baos.toByteArray(), 0)
    }

    fun base642bitmap(base64: String): Bitmap {
        val bais = ByteArrayInputStream(Base64.decode(base64, 0))
        return BitmapFactory.decodeStream(bais)
    }

}