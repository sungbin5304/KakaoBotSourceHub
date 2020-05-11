package com.sungbin.autoreply.bot.three.listener

import android.app.Notification
import android.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.Spanned
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.text.HtmlCompat
import com.balsikandar.crashreporter.CrashReporter
import com.faendir.rhino_android.RhinoAndroidHelper
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.api.*
import com.sungbin.autoreply.bot.three.dto.bot.DebugMessageItem
import com.sungbin.autoreply.bot.three.dto.bot.ScriptListItem
import com.sungbin.autoreply.bot.three.utils.bot.*
import com.sungbin.autoreply.bot.three.utils.bot.StackUtils.jsScope
import com.sungbin.autoreply.bot.three.utils.bot.StackUtils.jsScripts
import com.sungbin.autoreply.bot.three.utils.bot.StackUtils.sessions
import com.sungbin.sungbintool.DataUtils
import com.sungbin.sungbintool.StorageUtils.sdcard
import com.sungbin.sungbintool.ToastUtils
import org.mozilla.javascript.Function
import org.mozilla.javascript.ScriptableObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileReader
import java.util.*

@Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING", "DEPRECATION", "UNUSED_VARIABLE")
class KakaoTalkListener : NotificationListenerService() {

    override fun onCreate() {
        super.onCreate()
        ctx = applicationContext
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder().permitNetwork().build()
        )
        ToastUtils.show(
            ctx!!,
            getString(R.string.power_on),
            ToastUtils.SHORT,
            ToastUtils.INFO
        )

        AppData.init(ctx)
        Api.init(ctx)
        Device.init(ctx)
        Utils.init(ctx)
        Black.init(ctx)

        ApiClass.setContext(ctx!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        ToastUtils.show(ctx!!, getString(R.string.power_off),
            ToastUtils.SHORT, ToastUtils.INFO)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        if(!DataUtils.readData(applicationContext, "BotOn", "true").toBoolean()) return
        var packages = DataUtils.readData(ctx!!, "packages", "com.kakao.talk").trim()
        if(packages.isBlank()) packages = "com.kakao.talk"
        if (packages.split("\n").contains(sbn.packageName)) {
            val wExt =
                Notification.WearableExtender(sbn.notification)
            for (act in wExt.actions) {
                if (act.remoteInputs != null && act.remoteInputs.isNotEmpty()) {
                    if (act.title.toString().toLowerCase(Locale.getDefault()).contains("reply")
                        || act.title.toString()
                            .toLowerCase(Locale.getDefault()).contains("답장")
                    ) {
                        val extras = sbn.notification.extras
                        var room: String?
                        var sender: String?
                        var msg: String?
                        var isGroupChat = false
                        val packageName = sbn.packageName

                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                            room = extras.getString("android.summaryText")
                            sender = extras.get("android.title")?.toString()
                            msg = extras.get("android.text")?.toString()
                            if(room == null){
                                room = sender
                                isGroupChat = false
                            }
                            else isGroupChat = true
                        }
                        else {
                            var kakaotalkVersion = 0L
                            var noKakaoTalk = false
                            try{
                                kakaotalkVersion =
                                    PackageInfoCompat.getLongVersionCode(
                                        packageManager.getPackageInfo("com.kakao.talk", 0))
                            }
                            catch(ignored: Exception){
                                noKakaoTalk = true
                            }

                            if(noKakaoTalk || packageName != "com.kakao.talk"
                             || kakaotalkVersion < 1907310) {
                                room = extras.getString("android.title")
                                if(extras.get("android.text") !is String) {
                                    val html = HtmlCompat.toHtml(extras.get("android.text")
                                            as Spanned, HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
                                    sender = HtmlCompat.fromHtml(html.split("<b>")[1].split("</b>")[0],
                                        HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
                                    msg = HtmlCompat.fromHtml(html.split("</b>")[1].split("</p>")[0]
                                        .substring(1), HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
                                }
                                else {
                                    sender = room
                                    msg = extras.get("android.text")?.toString()
                                }
                            }
                            else {
                                room = extras.getString("android.subText")
                                sender = extras.getString("android.title")
                                msg = extras.getString("android.text")
                                isGroupChat = room != null
                                if(room == null) room = sender
                            }
                        }

                        if (!sessions.containsKey(room)) sessions[room] = act
                        val blackRoom = DataUtils.readData(
                            applicationContext, "RoomBlackList", "")
                        val blackSender = DataUtils.readData(
                            applicationContext, "SenderBlackList", "")
                        if (!blackRoom.contains(room!!) ||
                            !blackSender.contains(sender!!)) {
                            chatHook(
                                sender!!, msg!!, room, isGroupChat, act,
                                ((sbn.notification.getLargeIcon())
                                    .loadDrawable(ctx) as BitmapDrawable).bitmap,
                                sbn.packageName,
                                ctx!!
                            )
                        }
                    }
                }
            }
        }
    }

    class Replier(private val session: Notification.Action?,
                  private val ctx: Context) {
        fun reply(msg1: String) {
            try {
                if (session == null) {
                    ToastUtils.show(
                        ctx,
                        ctx.getString(R.string.cant_load_session),
                        ToastUtils.LONG, ToastUtils.WARNING
                    )
                } else {
                    val sendIntent = Intent()
                    val msg = Bundle()

                    for (inputable in session.remoteInputs) {
                        msg.putCharSequence(inputable.resultKey, msg1)
                    }

                    RemoteInput.addResultsToIntent(
                        session.remoteInputs, sendIntent, msg
                    )

                    session.actionIntent.send(ctx, 0, sendIntent)
                }
            } catch (e: Exception) {
                ToastUtils.show(
                    ctx,
                    e.toString(),
                    ToastUtils.LONG, ToastUtils.ERROR
                )
            }
        }

        fun reply(room: String, msg1: String) {
            try {
                if (!sessions.containsKey(room)) {
                    ToastUtils.show(
                        ctx,
                        ctx.getString(R.string.cant_load_session),
                        ToastUtils.LONG,
                        ToastUtils.WARNING
                    )
                }
                else {
                    val sendIntent = Intent()
                    val msg = Bundle()

                    for (inputable in sessions[room]!!.remoteInputs) {
                        msg.putCharSequence(inputable.resultKey, msg1)
                    }

                    RemoteInput.addResultsToIntent(
                        sessions[room]!!.remoteInputs, sendIntent, msg
                    )

                    sessions[room]!!.actionIntent.send(ctx, 0, sendIntent)
                }
            } catch (e: Exception) {
                ToastUtils.show(
                    ctx,
                    e.toString(),
                    ToastUtils.LONG, ToastUtils.ERROR
                )
            }
        }

        fun replyShowAll(msg1: String, msg2: String) {
            try {
                if (session == null) {
                    ToastUtils.show(
                        ctx,
                        ctx.getString(R.string.cant_load_session),
                        ToastUtils.LONG, ToastUtils.WARNING
                    )
                } else {
                    val sendIntent = Intent()
                    val msg = Bundle()

                    for (inputable in session.remoteInputs) {
                        msg.putCharSequence(inputable.resultKey, "$msg1$showAll$msg2")
                    }

                    RemoteInput.addResultsToIntent(
                        session.remoteInputs, sendIntent, msg
                    )

                    session.actionIntent.send(ctx, 0, sendIntent)
                }
            } catch (e: Exception) {
                ToastUtils.show(
                    ctx,
                    e.toString(),
                    ToastUtils.LONG, ToastUtils.ERROR
                )
            }
        }

        fun replyShowAll(room: String, msg1: String, msg2: String) {
            try {
                if (!sessions.containsKey(room)) {
                    ToastUtils.show(
                        ctx,
                        ctx.getString(R.string.cant_load_session),
                        ToastUtils.LONG, ToastUtils.WARNING
                    )
                }
                else {
                    val sendIntent = Intent()
                    val msg = Bundle()

                    for (inputable in sessions[room]!!.remoteInputs) {
                        msg.putCharSequence(inputable.resultKey, "$msg1$showAll$msg2")
                    }

                    RemoteInput.addResultsToIntent(
                        sessions[room]!!.remoteInputs, sendIntent, msg
                    )

                    sessions[room]!!.actionIntent.send(ctx, 0, sendIntent)
                }
            } catch (e: Exception) {
                ToastUtils.show(
                    ctx,
                    e.toString(),
                    ToastUtils.LONG, ToastUtils.ERROR
                )
            }
        }

    }

    class DebugReplier(private val room: String,
                       private val sender: String) {
        fun reply(msg1: String) {
            DebugUtils.addMessage(this.room, DebugMessageItem(sender, msg1))
        }

        fun reply(room: String, msg1: String) {
            DebugUtils.addMessage(room, DebugMessageItem(sender, msg1))
        }

        fun replyShowAll(msg1: String, msg2: String) {
            DebugUtils.addMessage(this.room, DebugMessageItem(sender, "$msg1$showAll$msg2"))
        }

        fun replyShowAll(room: String, msg1: String, msg2: String) {
            DebugUtils.addMessage(room, DebugMessageItem(sender, "$msg1$showAll$msg2"))
        }

    }

    class ImageDB(bitmap: Bitmap?) {
        private val profileImage = bitmap

        fun getProfileImage(): String {
            if (profileImage == null) return "프로필 이미지가 없습니다."
            val baos = ByteArrayOutputStream()
            profileImage.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val bImage: ByteArray = baos.toByteArray()
            return Base64.encodeToString(bImage, 0)
        }

        fun getLastPicture(): String{
            return PicturePathManager.getLastPicture().toString()
        }
    }

    class DebugImageDB(private val name: String) {
        fun getProfileImage(): String {
            return DebugUtils.getProfileBase64(name)
        }

        fun getLastPicture(): String{
            return PicturePathManager.getLastPicture().toString()
        }
    }

    companion object{
        var showAll: String = "\u200b".repeat(500)
        private var ctx: Context? = null

        fun chatHook(
            sender: String,
            msg: String,
            room: String,
            isGroupChat: Boolean,
            session: Notification.Action?,
            profileImage: Bitmap?,
            packageName: String,
            context: Context,
            isDebugMode: Boolean = false
        ) {
            try {
                val jsPath = "$sdcard/${BotPathManager.JS}/"
                val simplePath = "$sdcard/${BotPathManager.SIMPLE}/"
                val jsList = File(jsPath).listFiles()
                val simpleList = File(simplePath).listFiles()
                val scriptList = ArrayList<ScriptListItem>()

                if (simpleList != null) {
                    for (i in simpleList.indices) { //간편 자동응답
                        val name = simpleList[i].name
                        if(BotPowerUtils.getIsOn(context, name)) {
                            callSimpleResponder(
                                name, sender, msg, room,
                                isGroupChat, session, profileImage,
                                packageName, isDebugMode,
                                context
                            )
                        }
                    }
                }
                if (jsList != null) {
                    for (i in jsList.indices) {
                        val name = jsList[i].name
                        if(BotPowerUtils.getIsOn(context, name)) {
                            callJsResponder(
                                name, sender, msg, room,
                                isGroupChat, session, profileImage,
                                packageName, isDebugMode,
                                context
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                ToastUtils.show(ctx!!, e.toString(),
                    ToastUtils.LONG, ToastUtils.ERROR)
            }
        }

        fun initializeJavaScript(string: String): String {
            val name = "$string.js"
            return try {
                val scriptFile = File("$sdcard/${BotPathManager.JS}/$name")
                if (!scriptFile.exists()) return ctx!!.getString(R.string.script_file_gone)
                val rhino = RhinoAndroidHelper().enterContext()
                rhino.languageVersion = org.mozilla.javascript.Context.VERSION_ES6
                rhino.optimizationLevel = -1

                val scope = rhino.initStandardObjects()
                ScriptableObject.defineClass(scope, ApiClass.Log::class.java, false, true)
                ScriptableObject.defineClass(scope, ApiClass.AppData::class.java, false, true)
                ScriptableObject.defineClass(scope, ApiClass.Api::class.java, false, true)
                ScriptableObject.defineClass(scope, ApiClass.Device::class.java, false, true)
                ScriptableObject.defineClass(scope, ApiClass.Scope::class.java, false, true)
                ScriptableObject.defineClass(scope, ApiClass.File::class.java, false, true)
                ScriptableObject.defineClass(scope, ApiClass.Black::class.java, false, true)
                ScriptableObject.defineClass(scope, ApiClass.Utils::class.java, false, true)

                val script = rhino.compileReader(FileReader(scriptFile), name, 0, null)
                script.exec(rhino, scope)

                val responder = scope["response", scope] as Function
                jsScripts[name] = responder
                jsScope[name] = scope

                org.mozilla.javascript.Context.exit()
                "true"
            }
            catch (e: Exception) {
                /*if (e.toString()
                        .contains("java.lang.String android.content.Context.getPackageName()' on a null object reference")
                ) return "리로드 오류"
                if (e.toString()
                        .contains("org.mozilla.javascript.UniqueTag cannot be cast to org.mozilla.javascript.Function")
                ) "리로드 오류" else e.message*/
                if(!DataUtils.readData(ctx!!, "KeepScope", "false").toBoolean()){
                    jsScripts.remove(name)
                    jsScope.remove(name)
                }
                CrashReporter.logException(e)
                e.toString()
            }
        }

        fun reply(session: Notification.Action, value: String) {
            val sendIntent = Intent()
            val msg = Bundle()
            for (inputable in session.remoteInputs) msg.putCharSequence(
                inputable.resultKey,
                value
            )
            RemoteInput.addResultsToIntent(session.remoteInputs, sendIntent, msg)
            try {
                session.actionIntent.send(ctx, 0, sendIntent)
            } catch (e: Exception) {
                ToastUtils.show(ctx!!, e.toString(),
                    ToastUtils.LONG, ToastUtils.ERROR)
            }
        }

        fun replyDebug(room: String, message: DebugMessageItem) {
            DebugUtils.addMessage(room, message)
        }

        fun callSimpleResponder(
            name: String,
            sender: String,
            msg: String,
            room: String,
            isGroupChat: Boolean,
            session: Notification.Action?,
            profileImage: Bitmap?,
            packageName: String,
            isDebugMode: Boolean,
            context: Context
        ){
            var simpleType = SimpleBotUtils.get(name, "type")
            var simpleRoom = SimpleBotUtils.get(name, "room")
            val simpleReply = SimpleBotUtils.get(name, "reply")
            var simpleSender = SimpleBotUtils.get(name, "sender")
            var simpleMessage = SimpleBotUtils.get(name, "message")

            if(simpleType == "null") simpleType = isGroupChat.toString()
            if(simpleRoom.isBlank()) simpleRoom = room
            if(simpleSender.isBlank()) simpleSender = sender
            if(simpleMessage.isBlank()) simpleMessage = msg

            if(room == simpleRoom && sender == simpleSender
                && msg == simpleMessage && isGroupChat.toString() == simpleType) {
                if(!isDebugMode) {
                    reply(session!!, simpleReply)
                    RunTimeUtils.save(context, name)
                }
                else {
                    replyDebug(room, DebugMessageItem(sender, simpleReply))
                }
            }
        }

        fun callJsResponder(
            name: String,
            sender: String,
            msg: String,
            room: String,
            isGroupChat: Boolean,
            session: Notification.Action?,
            profileImage: Bitmap?,
            packageName: String,
            isDebugMode: Boolean,
            context: Context
        ) {
            val parseContext = RhinoAndroidHelper().enterContext()
            parseContext.languageVersion = org.mozilla.javascript.Context.VERSION_ES6
            val responder = jsScripts[name]
            val execScope = jsScope[name]
            try {
                if (responder == null || execScope == null) {
                    org.mozilla.javascript.Context.exit()
                    ToastUtils.show(
                        ctx!!,
                        ctx!!.getString(R.string.reload_script_first).replace("{name}", name),
                        ToastUtils.SHORT,
                        ToastUtils.WARNING
                    )
                }
                else {
                    if(!isDebugMode) {
                        responder.call(
                            parseContext,
                            execScope,
                            execScope,
                            arrayOf(
                                room, msg, sender, isGroupChat,
                                Replier(session, ctx!!),
                                ImageDB(profileImage), packageName
                            )
                        )
                        RunTimeUtils.save(context, name)
                    }
                    else {
                        responder.call(
                            parseContext,
                            execScope,
                            execScope,
                            arrayOf(
                                room, msg, sender, isGroupChat,
                                DebugReplier(room, sender),
                                DebugImageDB(sender),
                                packageName
                            )
                        )
                    }
                    org.mozilla.javascript.Context.exit()
                }
            }
            catch (e: Exception) {
                ToastUtils.show(ctx!!,
                    "$name 작동에 오류가 발생했습니다.\n\n오류 내용 : $e",
                    ToastUtils.LONG,
                    ToastUtils.ERROR
                )
                if(DataUtils.readData(context, "ErrorBotOff", "false").toBoolean()){
                    BotPowerUtils.setOnOff(context, name, false)
                }
            }
        }

    }
}