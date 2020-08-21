package com.sungbin.autoreply.bot.three.utils.bot

import android.graphics.Bitmap.CompressFormat
import com.sungbin.autoreply.bot.three.dto.bot.DebugMessageItem
import com.sungbin.autoreply.bot.three.utils.Base64Utils
import com.sungbin.autoreply.bot.three.utils.bot.BotPathManager.ROOM
import com.sungbin.autoreply.bot.three.utils.bot.BotPathManager.SENDER
import com.sungbin.sungbintool.StorageUtils
import com.sungbin.sungbintool.StorageUtils.sdcard
import java.io.File
import java.io.FileOutputStream

object DebugUtils {

    private val messages = HashMap<String, ArrayList<DebugMessageItem>>()

    fun getMessges(room: String): ArrayList<DebugMessageItem>? {
        return messages[room]
    }

    fun addMessage(room: String, message: DebugMessageItem) {
        if (!messages.containsKey(room)) saveRoom(room)
        val messages = getMessges(room)
        if (messages == null) {
            val array = ArrayList<DebugMessageItem>()
            array.add(message)
            this.messages[room] = array
        } else {
            messages.add(message)
            this.messages[room] = messages
        }
    }

    fun saveSender(name: String, base64: String) {
        StorageUtils.createFolder("$SENDER/$name")
        StorageUtils.save("$SENDER/$name/profile.base64", base64)
        saveProfileImage(name, base64)
    }

    fun getSenderList(): ArrayList<String> {
        val array = ArrayList<String>()
        File("$sdcard/$SENDER").listFiles()?.map {
            array.add(it.name)
        }
        return array
    }

    fun saveRoom(name: String) {
        StorageUtils.createFolder("$ROOM/$name")
    }

    fun getRoomList(): ArrayList<String> {
        val array = ArrayList<String>()
        File("$sdcard/$ROOM").listFiles()?.map {
            array.add(it.name)
        }
        return array
    }

    fun getProfileBase64(name: String): String {
        return StorageUtils.read("$SENDER/$name/profile.base64", "").toString()
    }

    fun getProfileImagePath(name: String): String {
        return "$sdcard/$SENDER/$name/profile.png"
    }

    private fun saveProfileImage(name: String, base64: String) {
        val bitmap = Base64Utils.base642bitmap(base64)
        val path = getProfileImagePath(name)
        File(path).createNewFile()
        val fos = FileOutputStream(File(path))
        bitmap.compress(CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()
    }

}