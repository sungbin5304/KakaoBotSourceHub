package com.sungbin.autoreply.bot.three.view.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.utils.chat.ChatModuleUtils
import com.sungbin.autoreply.bot.three.view.activity.fragment.AddFragment
import com.sungbin.autoreply.bot.three.view.activity.fragment.DashboardFragment
import com.sungbin.autoreply.bot.three.view.activity.fragment.SandboxFragment
import com.sungbin.autoreply.bot.three.view.activity.fragment.SettingFragment
import com.sungbin.autoreply.bot.three.view.hub.MainActivity
import com.sungbin.sungbintool.StorageUtils
import kotlinx.android.synthetic.main.content_dashboard.*


@Suppress("DEPRECATION")
class DashboardActivity  : AppCompatActivity() {

    val fragmentManager: FragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.content_dashboard)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.colorWhite)
            }

            StorageUtils.createFolder("AutoReply Bot/Bots/AutoReply")
            StorageUtils.createFolder("AutoReply Bot/Bots/JavaScript")
            StorageUtils.createFolder("AutoReply Bot/Bots/Log")

            Log.d("데이터!", ChatModuleUtils.getUser(ChatModuleUtils.getDeviceId(applicationContext))!!.name)

            val title = findViewById<TextView>(R.id.tv_dashboard)
            fragmentManager.beginTransaction().add(R.id.framelayout, DashboardFragment()).commit()
            bottombar.onItemSelected = {
                val fragmentTransaction = fragmentManager.beginTransaction()
                when (it) {
                    0 -> { //대쉬보드
                        title.text = getString(R.string.string_dashboard)
                        fragmentTransaction.replace(R.id.framelayout, DashboardFragment()).commit()
                    }
                    1 -> { //센드박스
                        title.text = getString(R.string.string_sandbox)
                        fragmentTransaction.replace(R.id.framelayout, SandboxFragment()).commit()
                    }
                    2 -> { //스크립트 추가
                        title.text = getString(R.string.add_bot)
                        fragmentTransaction.replace(
                            R.id.framelayout,
                            AddFragment(fragmentManager, R.id.framelayout, bottombar)
                        ).commit()
                    }
                    3 -> { //카톡봇 허브
                        fragmentTransaction.replace(R.id.framelayout, DashboardFragment()).commit()
                        bottombar.setActiveItem(0)
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    4 -> { //설정
                        title.text = getString(R.string.string_setting)
                        fragmentTransaction.replace(R.id.framelayout, SettingFragment()).commit()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Error", e.toString())
        }
    }

}