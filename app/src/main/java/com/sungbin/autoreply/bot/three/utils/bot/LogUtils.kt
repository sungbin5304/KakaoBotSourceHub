package com.sungbin.autoreply.bot.three.utils.bot

import com.sungbin.sungbintool.StorageUtils
import java.io.File

object LogUtils {
    object Type {
        const val INFO = 0
        const val SUCCESS = 1
        const val ERROR = 2
        const val DEBUG = 3
    }

    private fun getPath(name: String, type: String): String{
        StorageUtils.createFolder("Android/data/com.sungbin.autoreply.bot.three/KakaoTalkBotHub/Log/$name")
        return "Android/data/com.sungbin.autoreply.bot.three/KakaoTalkBotHub/Log/$name/$type.log"
    }

    fun save(name: String, content: String, time: String, type: Int){
        var path = getPath(name, "content")
        StorageUtils.save(path, content)

        path = getPath(name, "time")
        StorageUtils.save(path, time)

        path = getPath(name, "type")
        StorageUtils.save(path, type.toString())
    }

    fun get(name: String, type: String): String{
        return StorageUtils.read(
            getPath(name, type), ""
        )
    }

    fun remove(name: String, type: String){
        StorageUtils.delete(getPath(name, type))
    }

}