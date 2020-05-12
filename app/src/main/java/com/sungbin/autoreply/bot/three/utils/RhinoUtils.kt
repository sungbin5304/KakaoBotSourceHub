package com.sungbin.autoreply.bot.three.utils

import com.balsikandar.crashreporter.CrashReporter
import com.faendir.rhino_android.RhinoAndroidHelper
import com.sungbin.autoreply.bot.three.api.*
import org.mozilla.javascript.Context
import org.mozilla.javascript.ScriptableObject


class RhinoUtils constructor(ctx: android.content.Context) {

    init {
        AppData.init(ctx)
        Api.init(ctx)
        Api.init(ctx)
        Device.init(ctx)
        Utils.init(ctx)
        Black.init(ctx)
        ApiClass.setContext(ctx)
    }

    fun runJs(source: String): String{
        return try {
            val rhino = RhinoAndroidHelper().enterContext()
            rhino.languageVersion =  Context.VERSION_ES6
            rhino.optimizationLevel = -1

            val scope = rhino.initStandardObjects()
            ScriptableObject.defineClass(scope, ApiClass.Log::class.java, false, true)
            ScriptableObject.defineClass(scope, ApiClass.AppData::class.java, false, true)
            ScriptableObject.defineClass(scope, ApiClass.Api::class.java, false, true)
            ScriptableObject.defineClass(scope, ApiClass.Device::class.java, false, true)
            ScriptableObject.defineClass(scope, ApiClass.Scope::class.java, false, true)
            ScriptableObject.defineClass(scope, ApiClass.File::class.java, false, true)
            ScriptableObject.defineClass(scope, ApiClass.Black::class.java, false, true)
            ScriptableObject.defineClass(scope, ApiClass.DataBase::class.java, false, true)
            ScriptableObject.defineClass(scope, ApiClass.Utils::class.java, false, true)

            val result: Any = rhino.evaluateString(scope, source, "sandbox", 1, null)
            Context.exit()
            result.toString()
        } catch (e: Exception) {
            CrashReporter.logException(e)
            e.toString()
        }
    }
}