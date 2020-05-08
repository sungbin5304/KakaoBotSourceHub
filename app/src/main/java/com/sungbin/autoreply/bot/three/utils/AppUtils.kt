package com.sungbin.autoreply.bot.three.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.sungbin.sungbintool.ToastUtils

object AppUtils {
    private val remoteConfig = FirebaseRemoteConfig.getInstance()

    fun getAppVersionName(act: Activity): String{
        return act.packageManager.getPackageInfo(act.packageName, 0).versionName
    }

    fun loadFetch(context: Context){
        remoteConfig.fetch(0).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                remoteConfig.fetchAndActivate()
            } else {
                ToastUtils.show(context,
                    "서버에서 데이터를 불러오는데 오류가 발생했습니다.\n\n${task.exception}",
                    ToastUtils.SHORT, ToastUtils.ERROR)
            }
        }
    }

    fun getConfigData(name: String): String{
        return remoteConfig.getString(name).replace("\"", "")
    }
}