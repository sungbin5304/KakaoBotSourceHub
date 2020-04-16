package com.sungbin.autoreply.bot.three.view.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sungbin.autoreply.bot.three.R
import kotlinx.android.synthetic.main.content_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_login)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.colorWhite)
        }

        btn_login.setOnClickListener {
            finish()
            startActivity(Intent(this, PermissionActivity::class.java))
        }
    }
}