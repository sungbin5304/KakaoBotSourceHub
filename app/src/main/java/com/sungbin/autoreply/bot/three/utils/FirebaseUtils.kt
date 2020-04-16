package com.sungbin.autoreply.bot.three.utils

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import com.sungbin.autoreply.bot.three.notification.NotificationManager
import java.lang.Exception

object FirebaseUtils {
    fun subscribe(topic: String, ctx: Context){
        try {
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
        }
        catch (e: Exception) {
            Utils.error(ctx, e, "주제 구독")
        }
    }

    fun unSubscribe(topic: String, ctx: Context){
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
        }
        catch (e: Exception){
            Utils.error(ctx, e, "주제 구독 해제")
        }
    }

    fun showNoti(ctx: Context, title:String, content:String, topic: String){
        NotificationManager.sendNotiToFcm(ctx, title, content, topic)
    }
}