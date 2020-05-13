package com.sungbin.autoreply.bot.three.api

import android.content.Context
import com.github.kimkevin.hangulparser.HangulParser
import com.sungbin.autoreply.bot.three.api.game.chosung.ChosungType
import com.sungbin.autoreply.bot.three.utils.bot.BotPathManager
import com.sungbin.autoreply.bot.three.utils.bot.LogUtils
import com.sungbin.autoreply.bot.three.utils.bot.StackUtils
import com.sungbin.sungbintool.StorageUtils
import org.mozilla.javascript.NativeArray
import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.annotations.JSStaticFunction
import java.text.SimpleDateFormat
import java.util.*

object ApiClass{

    private var context: Context? = null
    fun setContext(ctx: Context){
        context = ctx
    }

    class Log : ScriptableObject() {
        override fun getClassName(): String {
            return "Log"
        }

        companion object {
            @JvmStatic
            @JSStaticFunction
            fun d(name: String, content: String) {
                val now = System.currentTimeMillis()
                val date = Date(now)
                val sdf = SimpleDateFormat("hh:mm:ss", Locale.getDefault())
                val time = sdf.format(date)

                LogUtils.save(name, content, time, LogUtils.Type.DEBUG)
            }

            @JvmStatic
            @JSStaticFunction
            fun e(name: String, content: String) {
                val now = System.currentTimeMillis()
                val date = Date(now)
                val sdf = SimpleDateFormat("hh:mm:ss", Locale.getDefault())
                val time = sdf.format(date)

                LogUtils.save(name, content, time, LogUtils.Type.ERROR)
            }

            @JvmStatic
            @JSStaticFunction
            fun i(name: String, content: String) {
                val now = System.currentTimeMillis()
                val date = Date(now)
                val sdf = SimpleDateFormat("hh:mm:ss", Locale.getDefault())
                val time = sdf.format(date)

                LogUtils.save(name, content, time, LogUtils.Type.INFO)
            }

            @JvmStatic
            @JSStaticFunction
            fun s(name: String, content: String) {
                val now = System.currentTimeMillis()
                val date = Date(now)
                val sdf = SimpleDateFormat("hh:mm:ss", Locale.getDefault())
                val time = sdf.format(date)

                LogUtils.save(name, content, time, LogUtils.Type.SUCCESS)
            }

        }
   }

    class AppData : ScriptableObject() {
        override fun getClassName(): String {
            return "AppData"
        }

        companion object {
            @JvmStatic
            @JSStaticFunction
            fun putInt(name: String, value: Int) {
                com.sungbin.autoreply.bot.three.api.AppData.putInt(name, value)
            }

            @JvmStatic
            @JSStaticFunction
            fun putString(name: String, value: String) {
                com.sungbin.autoreply.bot.three.api.AppData.putString(name, value)
            }

            @JvmStatic
            @JSStaticFunction
            fun putBoolean(name: String, value: Boolean) {
                com.sungbin.autoreply.bot.three.api.AppData.putBoolean(name, value)
            }

            @JvmStatic
            @JSStaticFunction
            fun getInt(name: String, value: Int): Int {
                return com.sungbin.autoreply.bot.three.api.AppData.getInt(name, value)
            }

            @JvmStatic
            @JSStaticFunction
            fun getString(name: String, value: String): String? {
                return com.sungbin.autoreply.bot.three.api.AppData.getString(name, value)
            }

            @JvmStatic
            @JSStaticFunction
            fun getBoolean(name: String, value: Boolean): Boolean {
                return com.sungbin.autoreply.bot.three.api.AppData.getBoolean(name, value)
            }

            @JvmStatic
            @JSStaticFunction
            fun clear() {
                com.sungbin.autoreply.bot.three.api.AppData.clear()
            }

            @JvmStatic
            @JSStaticFunction
            fun remove(name: String) {
                com.sungbin.autoreply.bot.three.api.AppData.remove(name)
            }
        }
    }

    class Api : ScriptableObject(){
        override fun getClassName(): String {
            return "Api"
        }

        companion object {
            @JvmStatic
            @JSStaticFunction
            fun getContext(): Context{
                return context!!
            }

            @JvmStatic
            @JSStaticFunction
            fun replyRoom(room: String, msg: String): Boolean {
                return com.sungbin.autoreply.bot.three.api.Api.replyRoom(room, msg)
            }

            @JvmStatic
            @JSStaticFunction
            fun replyRoomShowAll(room: String, msg1: String, msg2: String): Boolean{
                return com.sungbin.autoreply.bot.three.api.Api.replyRoomShowAll(room, msg1, msg2)
            }
        }
    }

    class Game : ScriptableObject(){
        override fun getClassName(): String {
            return "Game"
        }

        companion object {
            @JvmStatic
            @JSStaticFunction
            fun getRandomChosungQuiz(): ArrayList<Any> {
                return com.sungbin.autoreply.bot.three.api.Game.chosungQuiz(ChosungType.getRandom())
            }

            @JvmStatic
            @JSStaticFunction
            fun getChosungQuiz(type: Int): ArrayList<Any> {
                return com.sungbin.autoreply.bot.three.api.Game.chosungQuiz(type)
            }
        }
    }

    class Korean : ScriptableObject(){
        override fun getClassName(): String {
            return "Korean"
        }

        companion object {
            @JvmStatic
            @JSStaticFunction
            fun split(string: String): List<String> {
                return HangulParser.disassemble(string)
            }
        }
    }

    class Device : ScriptableObject() {
        override fun getClassName(): String {
            return "Device"
        }

        companion object {
            @JvmStatic
            @JSStaticFunction
            fun getBattery(): Int {
                return com.sungbin.autoreply.bot.three.api.Device.battery
            }

            @JvmStatic
            @JSStaticFunction
            fun getPhoneModel(): String {
                return com.sungbin.autoreply.bot.three.api.Device.phoneModel
            }

            @JvmStatic
            @JSStaticFunction
            fun getAndroidSDKVersion(): String {
                return com.sungbin.autoreply.bot.three.api.Device.androidVersion
            }

            @JvmStatic
            @JSStaticFunction
            fun getAndroidVersion(): String {
                return com.sungbin.autoreply.bot.three.api.Device.androidVersion
            }

            @JvmStatic
            @JSStaticFunction
            fun getIsCharging(): Boolean {
                return com.sungbin.autoreply.bot.three.api.Device.isCharging
            }
        }
    }

    class Scope : ScriptableObject() {
        override fun getClassName(): String {
            return "Bridge"
        }

        companion object {
            @JvmStatic
            @JSStaticFunction
            fun get(name: String): ScriptableObject? {
                return StackUtils.jsScope[name]
            }
        }
    }

    class DataBase : ScriptableObject() {
        override fun getClassName(): String {
            return "DataBase"
        }

        companion object {
            @JvmStatic
            @JSStaticFunction
            fun read(name: String): String? {
                return StorageUtils.read("${BotPathManager.DATABASE}/$name", null)
            }

            @JvmStatic
            @JSStaticFunction
            fun save(name: String, content: String) {
                StorageUtils.save("${BotPathManager.DATABASE}/$name", content)
            }

            @JvmStatic
            @JSStaticFunction
            fun remove(name: String) {
                StorageUtils.delete("${BotPathManager.DATABASE}/$name")
            }
        }
    }

    class File : ScriptableObject() {
        override fun getClassName(): String {
            return "File"
        }

        companion object {
            @JvmStatic
            @JSStaticFunction
            fun read(path: String, _null: String?): String? {
                return StorageUtils.read(path, _null)
            }

            @JvmStatic
            @JSStaticFunction
            fun save(path: String, content: String): Boolean {
                return StorageUtils.save(path, content)
            }

            @JvmStatic
            @JSStaticFunction
            fun append(path: String, content: String): Boolean {
                val string = "${StorageUtils.read(path, "")}$content"
                return save(path, string)
            }

            @JvmStatic
            @JSStaticFunction
            fun delete(path: String): Boolean{
                return StorageUtils.delete(path)
            }

            @JvmStatic
            @JSStaticFunction
            fun deleteAll(path: String): Boolean{
                return StorageUtils.deleteAll(path)
            }
        }
    }

    class Black : ScriptableObject() {
        override fun getClassName(): String {
            return "Black"
        }

        companion object {
            @JvmStatic
            @JSStaticFunction
            fun getSender(): String {
                return com.sungbin.autoreply.bot.three.api.Black.readSender()
            }

            @JvmStatic
            @JSStaticFunction
            fun getRoom(): String {
                return com.sungbin.autoreply.bot.three.api.Black.readRoom()
            }

            @JvmStatic
            @JSStaticFunction
            fun addRoom(room: String) {
                com.sungbin.autoreply.bot.three.api.Black.addRoom(room)
            }

            @JvmStatic
            @JSStaticFunction
            fun addSender(sender: String) {
                com.sungbin.autoreply.bot.three.api.Black.addSender(sender)
            }

            @JvmStatic
            @JSStaticFunction
            fun removeRoom(room: String) {
                com.sungbin.autoreply.bot.three.api.Black.removeRoom(room)
            }

            @JvmStatic
            @JSStaticFunction
            fun removeSender(sender: String) {
                com.sungbin.autoreply.bot.three.api.Black.removeSender(sender)
            }
        }
    }

    class Utils : ScriptableObject() {
        override fun getClassName(): String {
            return "Utils"
        }

        companion object {
            @JvmStatic
            @JSStaticFunction
            fun makeToast(content: String) {
                com.sungbin.autoreply.bot.three.api.Utils.makeToast(content)
            }

            @JvmStatic
            @JSStaticFunction
            fun delay(time: Int) {
                Thread.sleep((time * 1000).toLong())
            }

            @JvmStatic
            @JSStaticFunction
            fun makeNoti(title: String, content: String) {
                com.sungbin.autoreply.bot.three.api.Utils.makeNoti(title, content)
            }

            @JvmStatic
            @JSStaticFunction
            fun getHtml(link: String, fromJsoup: Boolean = true): String? {
                return if(!fromJsoup){
                    com.sungbin.autoreply.bot.three.api.Api.getHtmlFromJava(link)
                }
                else com.sungbin.autoreply.bot.three.api.Api.getHtmlFromJsoup(link)
            }

            @JvmStatic
            @JSStaticFunction
            fun post(address: String, postName: NativeArray, postData: NativeArray): String{
                return com.sungbin.autoreply.bot.three.api.Api.post(address, postName, postData)
            }

            @JvmStatic
            @JSStaticFunction
            fun showAll(): String{
                return "\u200b".repeat(500)
            }

            @JvmStatic
            @JSStaticFunction
            fun deleteHtml(html: String): String {
                return com.sungbin.autoreply.bot.three.api.Api.deleteHtml(html)
            }

            @JvmStatic
            @JSStaticFunction
            fun makeVibration(time: Int) {
                com.sungbin.autoreply.bot.three.api.Utils.makeVibration(time)
            }

            @JvmStatic
            @JSStaticFunction
            fun copy(content: String) {
                com.sungbin.autoreply.bot.three.api.Utils.copy(content)
            }
        }
    }
}