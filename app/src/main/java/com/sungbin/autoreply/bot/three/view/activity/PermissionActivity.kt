package com.sungbin.autoreply.bot.three.view.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sungbin.autoreply.bot.three.R
import kotlinx.android.synthetic.main.content_permission.*

class PermissionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_permission)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.colorWhite)
        }

        btn_continue.setOnClickListener {
            finish()
            startActivity(Intent(this, DashboardActivity::class.java))

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}