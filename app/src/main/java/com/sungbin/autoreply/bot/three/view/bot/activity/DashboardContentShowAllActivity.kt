package com.sungbin.autoreply.bot.three.view.bot.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.adapter.bot.DatabaseListAdapter
import com.sungbin.autoreply.bot.three.adapter.bot.LogListAdapter
import com.sungbin.autoreply.bot.three.dto.bot.DatabaseListItem
import com.sungbin.autoreply.bot.three.dto.bot.LogListItem
import com.sungbin.autoreply.bot.three.utils.bot.BotPathManager
import com.sungbin.autoreply.bot.three.utils.bot.LogUtils
import com.sungbin.sungbintool.StorageUtils
import com.sungbin.sungbintool.StorageUtils.sdcard
import kotlinx.android.synthetic.main.activity_database_view.*
import kotlinx.android.synthetic.main.content_dashboard_content_viewall.*
import java.io.File
import java.util.*


/**
 * Created by SungBin on 2020-05-13.
 */

@Suppress("NAME_SHADOWING")
class DashboardContentShowAllActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_content_viewall)

        val type = intent!!.getStringExtra("type")!!
        toolbar_title.text = type

        val logItem = ArrayList<LogListItem>()
        val databaseItem = ArrayList<DatabaseListItem>()
        val logPath = "$sdcard/${BotPathManager.LOG}"
        val databasePath = "$sdcard/${BotPathManager.DATABASE}"
        val logList = File(logPath).listFiles()
        val databaseList = File(databasePath).listFiles()

        rv_view.layoutManager = LinearLayoutManager(this)
        if(type == "전체 로그"){
            if (logList!!.isNotEmpty()) {
                Collections.sort(logList.asList(), kotlin.Comparator { t, t2 ->
                    return@Comparator t.lastModified().compareTo(t2.lastModified())
                })

                for (element in logList) {
                    val name = element.name
                    val content = LogUtils.get(name, "content")
                    val time = LogUtils.get(name, "time")
                    val type = LogUtils.get(name, "type")
                    val item = LogListItem(
                        name,
                        time,
                        content,
                        type
                    )
                    logItem.add(item)
                }
                rv_view.adapter = LogListAdapter(logItem, this)
            }
            else {
                rv_view.visibility = View.GONE
                tv_view.visibility = View.VISIBLE
                lav_view.visibility = View.VISIBLE
            }
        }
        else {
            if (databaseList!!.isNotEmpty()) {
                Collections.sort(databaseList.asList(), kotlin.Comparator { t, t2 ->
                    return@Comparator t.lastModified().compareTo(t2.lastModified())
                })

                for (element in databaseList) {
                    val item = DatabaseListItem(
                        element.name,
                        StorageUtils.getFileSize(element)
                    )
                    databaseItem.add(item)
                }

                rv_view.adapter = DatabaseListAdapter(databaseItem, this)
            }
            else {
                rv_view.visibility = View.GONE
                tv_view.visibility = View.VISIBLE
                lav_view.visibility = View.VISIBLE
            }
        }
    }
}