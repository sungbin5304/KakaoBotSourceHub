package com.sungbin.autoreply.bot.three.view.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.utils.AppUtils
import com.sungbin.autoreply.bot.three.utils.AppUtils.getAppVersionName
import com.sungbin.autoreply.bot.three.utils.chat.ChatModuleUtils
import com.sungbin.autoreply.bot.three.view.activity.fragment.AddFragment
import com.sungbin.autoreply.bot.three.view.activity.fragment.DashboardFragment
import com.sungbin.autoreply.bot.three.view.activity.fragment.SandboxFragment
import com.sungbin.autoreply.bot.three.view.activity.fragment.SettingFragment
import com.sungbin.autoreply.bot.three.view.hub.MainActivity
import com.sungbin.sungbintool.DialogUtils
import com.sungbin.sungbintool.StorageUtils
import com.sungbin.sungbintool.ToastUtils
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

            AppUtils.loadFetch(applicationContext)
            StorageUtils.createFolder("AutoReply Bot/Bots/AutoReply")
            StorageUtils.createFolder("AutoReply Bot/Bots/JavaScript")
            StorageUtils.createFolder("AutoReply Bot/Bots/Log")

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

            val lastVersion = AppUtils.getConfigData("last_version")
            if(lastVersion != getAppVersionName(this)){
                DialogUtils.show(this, "앱 업데이트 필요",
                    "사용중이신 KakaoTalkBotHub의 버전이 낮아서 더 이상 사용하실 수 없습니다.\n계속해서 사용하시려면 업데이트를 해 주새요.",
                    DialogInterface.OnClickListener { _, _ -> finish() }, false)
            }

        } catch (e: Exception) {
            Log.e("Error", e.toString())
        }
    }

}