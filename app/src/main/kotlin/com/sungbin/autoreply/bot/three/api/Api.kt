@file:Suppress("DEPRECATION")

package com.sungbin.autoreply.bot.three.api

import android.content.Context
import android.os.StrictMode
import android.text.Html
import com.sungbin.autoreply.bot.three.listener.KakaoTalkListener
import com.sungbin.autoreply.bot.three.listener.KakaoTalkListener.Companion.showAll
import com.sungbin.autoreply.bot.three.utils.bot.StackUtils
import com.sungbin.sungbintool.DataUtils
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.jsoup.Jsoup
import org.mozilla.javascript.NativeArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

object Api {
    private var ctx: Context? = null
    private const val USER_AGENT =
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36"

    fun init(context: Context?) {
        ctx = context
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    fun getHtmlFromJava(address: String): String? {
        try {
            val url = URL(address)
            val con = url.openConnection()
            if (con != null) {
                con.connectTimeout = DataUtils.readData(ctx!!, "HtmlLimitTime", "5").toInt() * 1000
                con.useCaches = false
                val isr = InputStreamReader(con.getInputStream())
                val br = BufferedReader(isr)
                var str = br.readLine()
                var line: String? = ""
                while ({ line = br.readLine(); line }() != null) {
                    str += "\n" + line
                }
                br.close()
                isr.close()
                return str
            }
            return null
        } catch (e: Exception) {
            return e.toString()
        }
    }

    fun getHtmlFromJsoup(address: String): String {
        return Jsoup
            .connect(address)
            .timeout(DataUtils.readData(ctx!!, "HtmlLimitTime", "5").toInt() * 1000)
            .get()
            .toString()
    }

    fun replyRoom(room: String, msg: String): Boolean {
        return if (StackUtils.sessions.containsKey(room)) {
            KakaoTalkListener.reply(StackUtils.sessions[room]!!, msg)
            true
        } else false
    }

    fun replyRoomShowAll(
        room: String,
        msg1: String,
        msg2: String
    ): Boolean {
        return if (StackUtils.sessions.containsKey(room)) {
            KakaoTalkListener.reply(
                StackUtils.sessions[room]!!,
                msg1 + showAll + msg2
            )
            true
        } else false
    }

    fun deleteHtml(html: String): String {
        return Html.fromHtml(html).toString()
    }

    fun post(
        adress: String,
        postName: NativeArray,
        postData: NativeArray
    ): String {
        var result = "false"
        if (postName.size != postData.size) return "postName과 postData의 크기가 같아야 합니다!"
        else {
            Thread(Runnable {
                try {
                    val request = HttpPost(adress)
                    val data: ArrayList<NameValuePair> = ArrayList()
                    for (i in 0 until postName.size) {
                        data.add(
                            BasicNameValuePair(
                                postName[i].toString(),
                                postData[i].toString()
                            )
                        )
                    }
                    val entity = UrlEncodedFormEntity(data, "UTF-8")
                    request.entity = entity
                    val client = DefaultHttpClient()
                    client.execute(request)
                    result = "true"
                } catch (e: Exception) {
                    result = e.toString()
                }
            }).start()
        }
        return result
    }
}