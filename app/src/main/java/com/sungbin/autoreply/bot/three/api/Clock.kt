package com.sungbin.autoreply.bot.three.api

import android.annotation.SuppressLint
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

@Suppress("SENSELESS_COMPARISON")
object Clock {
    @SuppressLint("SimpleDateFormat")
    fun normal(isFull: Boolean): String {
        val sdf =
            SimpleDateFormat(if (isFull) "kk:mm" else "hh:mm")
        return sdf.format(Date(System.currentTimeMillis()))
    }

    private fun replaceLast(
        string: String,
        toReplace: String,
        replacement: String
    ): String {
        val pos = string.lastIndexOf(toReplace)
        return if (pos > -1) {
            string.substring(0, pos) + replacement + string.substring(
                pos + toReplace.length,
                string.length
            )
        } else {
            string
        }
    }

    val source: String
        get() = try {
            val url =
                URL("https://firebasestorage.googleapis.com/v0/b/new-auto-reply-bot.appspot.com/o/analog_clock.js?alt=media&token=609f0bf0-ecb0-44b4-81e3-e0a923268367")
            val con = url.openConnection()
            con.setRequestProperty("User-Agent", "Mozilla/5.0")
            val br =
                BufferedReader(InputStreamReader(con.getInputStream(), "UTF-8"))
            var str = br.readLine()
            var line = ""
            while ({ line = br.readLine(); line }() != null) {
                str += "\n" + line
            }
            br.close()
            str
        } catch (e: Exception) {
            e.toString()
        }

    fun analog(): String {
        return try {
            val rhino = Context.enter()
            rhino.optimizationLevel = -1
            val scope: Scriptable = rhino.initSafeStandardObjects()
            val result = rhino.evaluateString(
                scope,
                source,
                "javascript",
                1,
                null
            )
            result.toString()
        } catch (e: Exception) {
            e.toString()
        }
    }

    fun digital(isFull: Boolean): String {
        val digtalNumber =
            arrayOf(
                arrayOf("███", "█░█", "█░█", "█░█", "███"),
                arrayOf("░░█", "░░█", "░░█", "░░█", "░░█"),
                arrayOf("███", "░░█", "███", "█░░", "███"),
                arrayOf("███", "░░█", "███", "░░█", "███"),
                arrayOf("█░█", "█░█", "███", "░░█", "░░█"),
                arrayOf("███", "█░░", "███", "░░█", "███"),
                arrayOf("███", "█░░", "███", "█░█", "███"),
                arrayOf("███", "█░█", "░░█", "░░█", "░░█"),
                arrayOf("███", "█░█", "███", "█░█", "███"),
                arrayOf("███", "█░█", "███", "░░█", "███")
            )
        val dot = arrayOf("░", "█", "░", "█", "░")
        var data = ""
        val number = normal(isFull)
        for (i in 0..4) {
            var str = ""
            for (n in 0 until number.length) {
                val cash = number[n].toString() + ""
                str += if (cash != ":") { //숫자
                    val num = digtalNumber[cash.toInt()][i]
                    "$num "
                } else { //:
                    dot[i] + " "
                }
            }
            str = replaceLast(str, " ", "")
            data += "$str::"
        }
        return data.replace("::", "\n")
    }
}