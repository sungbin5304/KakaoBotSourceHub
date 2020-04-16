package com.sungbin.autoreply.bot.three.utils

import android.content.Context
import com.sungbin.sungbintool.DataUtils

object BotPowerUtils {
    fun getIsOn(context: Context, name: String): Boolean{
        return DataUtils.readData(context, name, "false").toBoolean()
    }

    fun setOnOff(context: Context, name: String, isOn: Boolean){
        DataUtils.saveData(context, name, isOn.toString())
    }
}