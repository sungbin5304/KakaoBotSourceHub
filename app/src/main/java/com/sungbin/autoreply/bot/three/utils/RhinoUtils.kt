package com.sungbin.autoreply.bot.three.utils

import android.annotation.SuppressLint
import com.sungbin.autoreply.bot.three.api.*
import com.sungbin.autoreply.bot.three.api.Utils
import com.sungbin.autoreply.bot.three.listener.KakaoTalkListener
import com.sungbin.sungbintool.StorageUtils
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.annotations.JSStaticFunction
import java.text.SimpleDateFormat
import java.util.*

class RhinoUtils constructor(context: android.content.Context) {

    companion object{
        lateinit var ctx: android.content.Context
    }

    init {
        ctx = context
        AppData.init(ctx)
        Api.init(ctx)
        Api.init(ctx)
        Device.init(ctx)
        Utils.init(ctx)
        Black.init(ctx)
    }

    fun runJs(source: String): String{
        return try {
            val rhino: Context = Context.enter()
            rhino.languageVersion =  Context.VERSION_ES6
            rhino.optimizationLevel = -1
            val scope: Scriptable = rhino.initSafeStandardObjects()
            ScriptableObject.defineClass(scope, ApiClass.Log::class.java, false, true)
            ScriptableObject.defineClass(scope, ApiClass.AppData::class.java, false, true)
            ScriptableObject.defineClass(scope, ApiClass.Api::class.java, false, true)
            ScriptableObject.defineClass(scope, ApiClass.Clock::class.java, false, true)
            ScriptableObject.defineClass(scope, ApiClass.Device::class.java, false, true)
            ScriptableObject.defineClass(scope, ApiClass.Bridge::class.java, false, true)
            ScriptableObject.defineClass(scope, ApiClass.FileStream::class.java, false, true)
            ScriptableObject.defineClass(scope, ApiClass.Black::class.java, false, true)
            ScriptableObject.defineClass(scope, ApiClass.Utils::class.java, false, true)
            val result: Any = rhino.evaluateString(scope, source, "sandbox", 1, null)
            result.toString()
        } catch (e: Exception) {
            e.toString()
        }
    }
}