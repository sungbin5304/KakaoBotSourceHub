package com.sungbin.autoreply.bot.three.api

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Vibrator
import android.widget.Toast
import com.sungbin.autoreply.bot.three.R
import com.sungbin.sungbintool.NotificationUtils
import com.sungbin.sungbintool.ToastUtils

@Suppress("DEPRECATION")
object Utils {
    private var ctx: Context? = null
    private var vibrator: Vibrator? = null
    fun init(context: Context?) {
        ctx = context!!
        vibrator =
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun makeToast(content: String?) {
        Toast.makeText(
            ctx,
            content,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun makeNoti(title: String, content: String) {
        NotificationUtils.setGroupName(ctx!!.getString(R.string.app_name))
        NotificationUtils.createChannel(ctx!!,
            ctx!!.getString(R.string.app_name), ctx!!.getString(R.string.app_name))
        NotificationUtils.showNormalNotification(ctx!!,
            1, title, content, R.drawable.icon)
    }

    fun makeVibration(time: Int) {
        vibrator!!.vibrate(time * 1000.toLong())
    }

    fun copy(content: String?) {
        val clipboardManager =
            ctx!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("label", content)
        clipboardManager.setPrimaryClip(clipData)
        ToastUtils.show(ctx!!, "클립보드에 복사되었습니다.",
            ToastUtils.SHORT, ToastUtils.SUCCESS)
    }
}