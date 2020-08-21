package com.sungbin.autoreply.bot.three.kakao

import android.app.Activity
import android.app.Application
import com.kakao.auth.KakaoSDK

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        globalApplicationContext = this
        KakaoSDK.init(KakaoSDKAdapter())
    }

    companion object {
        @Volatile
        var globalApplicationContext: GlobalApplication? = null
            private set

        @Volatile
        var currentActivity: Activity? = null
    }
}