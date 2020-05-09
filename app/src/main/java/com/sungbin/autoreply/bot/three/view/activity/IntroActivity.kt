package com.sungbin.autoreply.bot.three.view.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.view.chat.DialogsActivity
import com.sungbin.sungbintool.DataUtils
import com.sungbin.sungbintool.PermissionUtils
import java.lang.Exception
import java.security.MessageDigest

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (DataUtils.readData(applicationContext, "logined", "false").toBoolean()) {
            finish()
            startActivity(Intent(this, SplashActivity::class.java))
        }
        else {
            setContentView(R.layout.content_intro)

            val fadein = AlphaAnimation(0.0f, 1.0f)
            fadein.duration = 1000
            fadein.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationStart(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    findViewById<TextView>(R.id.desc).visibility = View.VISIBLE
                    val fadein2 = AlphaAnimation(0.0f, 1.0f)
                    fadein2.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationRepeat(animation: Animation?) {
                        }

                        override fun onAnimationStart(animation: Animation?) {
                        }

                        override fun onAnimationEnd(animation: Animation?) {
                            findViewById<Button>(R.id.btn_continue).visibility = View.VISIBLE
                            val fadein3 = AlphaAnimation(0.0f, 1.0f)
                            fadein3.duration = 500
                            findViewById<Button>(R.id.btn_continue).animation = fadein3
                        }

                    })
                    fadein2.duration = 1000
                    findViewById<TextView>(R.id.desc).animation = fadein2
                }

            })
            findViewById<TextView>(R.id.title).animation = fadein

            findViewById<Button>(R.id.btn_continue).setOnClickListener {
                finish()
                startActivity(Intent(this, LoginActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
    }
}