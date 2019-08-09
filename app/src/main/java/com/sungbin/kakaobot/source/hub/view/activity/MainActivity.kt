package com.sungbin.kakaobot.source.hub.view.activity

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.sungbin.kakaobot.source.hub.R
import com.sungbin.kakaobot.source.hub.view.fragment.BoardList
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var fm: FragmentManager? = null
    private var fragmentTransaction: FragmentTransaction? = null
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_board -> {
                fragmentTransaction = fm!!.beginTransaction().apply {
                    replace(R.id.page, BoardList())
                    commit()
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_rank -> {
                fragmentTransaction = fm!!.beginTransaction().apply {
                    replace(R.id.page, BoardList())
                    commit()
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                fragmentTransaction = fm!!.beginTransaction().apply {
                    replace(R.id.page, BoardList())
                    commit()
                }
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar.title = ""
        setSupportActionBar(toolbar)
        navigation_view.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        fm = supportFragmentManager
        fragmentTransaction = fm!!.beginTransaction().apply {
            replace(R.id.page, BoardList())
            commit()
        }

    }
}
