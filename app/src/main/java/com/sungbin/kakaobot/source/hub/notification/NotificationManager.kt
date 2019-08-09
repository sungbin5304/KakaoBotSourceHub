package com.sungbin.kakaobot.source.hub.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.sungbin.kakaobot.source.hub.R
import com.sungbin.kakaobot.source.hub.view.activity.MainActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object NotificationManager {

    /**
     * Created by SungBin on 2018. 01. 07.
     */

    private var GROUP_NAME = "undefined"

    private val smallIcon: Int
        get() = R.drawable.icon

    fun setGroupName(name: String) {
        GROUP_NAME = name
    }

    fun createChannel(context: Context, name: String, description: String) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val group1 = NotificationChannelGroup(GROUP_NAME, GROUP_NAME)
            getManager(context).createNotificationChannelGroup(group1)

            val channelMessage =
                NotificationChannel(Channel.NAME, name, android.app.NotificationManager.IMPORTANCE_DEFAULT)
            channelMessage.description = description
            channelMessage.group = GROUP_NAME
            channelMessage.lightColor = R.color.colorAccent
            channelMessage.enableVibration(true)
            channelMessage.vibrationPattern = longArrayOf(0, 0)
            getManager(context).createNotificationChannel(channelMessage)
        }
    }

    private fun getManager(context: Context): android.app.NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
    }

    fun showNormalNotification(context: Context, id: Int, title: String, content: String) {

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val builder = Notification.Builder(context, Channel.NAME)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(smallIcon)
                .setShowWhen(true)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
            getManager(context).notify(id, builder.build())
        } else {
            val builder = Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(smallIcon)
                .setAutoCancel(true)
                .setShowWhen(true)
                .setContentIntent(pendingIntent)
            getManager(context).notify(id, builder.build())
        }
    }

    fun showInboxStyleNotification(context: Context, id: Int, title: String, content: String, boxText: Array<String>) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val builder = Notification.Builder(context, Channel.NAME)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(smallIcon)
                .setAutoCancel(true)
            val inboxStyle = Notification.InboxStyle()
            inboxStyle.setBigContentTitle(title)
            inboxStyle.setSummaryText(content)

            for (str in boxText) {
                inboxStyle.addLine(str)
            }

            builder.style = inboxStyle

            getManager(context).notify(id, builder.build())
        } else {
            val builder = Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(smallIcon)
                .setAutoCancel(true)
            val inboxStyle = Notification.InboxStyle()
            inboxStyle.setBigContentTitle(title)
            inboxStyle.setSummaryText(content)

            for (str in boxText) {
                inboxStyle.addLine(str)
            }

            builder.style = inboxStyle

            getManager(context).notify(id, builder.build())
        }
    }

    fun deleteNotification(context: Context, id: Int) {
        try {
            NotificationManagerCompat.from(context).cancel(id)
        }
        catch (e: java.lang.Exception){ }
    }

    annotation class Channel {
        companion object {
            const val NAME = "CHANNEL"
        }
    }

    private const val FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send"
    private const val SERVER_KEY =
        "AAAA_iXlJpM:APA91bENMty8XL3pcaElqVJBQHENktd2Ct6ZpOw5LR9lmooWEuiDrAjb3twEczdgxf9191ThDmEZHCiIGTI35gkSDC7fNnMw0DVk_yq3i86xZiXIwPtNMKBlsdsekKR-kwLUyArXg-7x"

    fun sendNotiToFcm(title: String, message: String, to: String) {
        Thread(Runnable {
            try {
                val root = JSONObject()
                val notification = JSONObject()
                notification.put("body", message)
                notification.put("title", title)
                root.put("data", notification)
                root.put("to", "/topics/$to")

                val url = URL(FCM_MESSAGE_URL)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.doInput = true
                conn.addRequestProperty("Authorization", "key=$SERVER_KEY")
                conn.setRequestProperty("Accept", "application/json")
                conn.setRequestProperty("Content-type", "application/json")
                val os = conn.outputStream
                os.write(root.toString().toByteArray(charset("utf-8")))
                os.flush()
                conn.responseCode
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).start()
    }
}
