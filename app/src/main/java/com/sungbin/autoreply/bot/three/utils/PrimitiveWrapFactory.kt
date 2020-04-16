package com.sungbin.autoreply.bot.three.utils

import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.WrapFactory

class PrimitiveWrapFactory : WrapFactory() {

    override fun wrap(cx: Context?, scope: Scriptable?, obj: Any?, staticType: Class<*>?): Any? {
        if (obj is String || obj is Number ||
                obj is Boolean) {
            return obj
        } else if (obj is Char) {
            val a = charArrayOf(obj)
            return String(a)
        }
        return super.wrap(cx, scope, obj, staticType)
    }

}