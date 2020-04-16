package com.sungbin.autoreply.bot.three.utils

import android.app.Notification
import org.mozilla.javascript.Function
import org.mozilla.javascript.ScriptableObject
import java.util.HashMap

object StackUtils {
    val sessions = HashMap<String?, Notification.Action>()
    val jsScripts = HashMap<String, Function>()
    val jsScope = HashMap<String, ScriptableObject>()
}