package com.sungbin.autoreply.bot.three.view.activity

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sungbin.autoreply.bot.three.R
import com.sungbin.sungbintool.DataUtils
import com.sungbin.sungbintool.PermissionUtils
import kotlinx.android.synthetic.main.content_permission.*

class PermissionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_permission)

        DataUtils.saveData(applicationContext, "logined", "true")

        btn_request_storage.setOnClickListener {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                1000
            )
        }

        btn_request_notification.setOnClickListener {
            PermissionUtils.requestReadNotification(this)
        }

        btn_continue.setOnClickListener {
            finish()
            startActivity(Intent(this, DashboardActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}