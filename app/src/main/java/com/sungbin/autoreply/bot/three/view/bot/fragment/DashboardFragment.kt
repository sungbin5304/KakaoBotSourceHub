@file:Suppress("DEPRECATION", "NAME_SHADOWING")

package com.sungbin.autoreply.bot.three.view.bot.fragment

import android.animation.Animator
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.adapter.bot.DatabaseListAdapter
import com.sungbin.autoreply.bot.three.adapter.bot.LogListAdapter
import com.sungbin.autoreply.bot.three.adapter.bot.ScriptListAdapter
import com.sungbin.autoreply.bot.three.dto.bot.DatabaseListItem
import com.sungbin.autoreply.bot.three.dto.bot.LogListItem
import com.sungbin.autoreply.bot.three.dto.bot.ScriptListItem
import com.sungbin.autoreply.bot.three.utils.bot.BotPathManager
import com.sungbin.autoreply.bot.three.utils.bot.BotPowerUtils
import com.sungbin.autoreply.bot.three.utils.bot.LogUtils
import com.sungbin.autoreply.bot.three.utils.bot.RunTimeUtils
import com.sungbin.autoreply.bot.three.view.bot.activity.DashboardContentShowAllActivity
import com.sungbin.sungbintool.StorageUtils
import com.sungbin.sungbintool.StorageUtils.sdcard
import kotlinx.android.synthetic.main.fragment_dashboard.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATED_IDENTITY_EQUALS", "UNUSED_VARIABLE",
    "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
)
class DashboardFragment : Fragment() {

    var searchEt: EditText? = null
    var searchBtn: Button? = null
    var scriptsRc: RecyclerView? = null
    var logViewAllBtn: Button? = null
    var logsRc: RecyclerView? = null
    var databasesRc: RecyclerView? = null
    var databasesViewAllBtn: Button? = null
    var botsNoneCl: ConstraintLayout? = null
    var botsNoneLav: LottieAnimationView? = null
    var logsNoneCl: ConstraintLayout? = null
    var logsNoneLav: LottieAnimationView? = null
    var databasesNoneCl: ConstraintLayout? = null
    var databasesNoneLav: LottieAnimationView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        searchBtn = view.findViewById(R.id.btn_script_search)
        searchEt = view.findViewById(R.id.et_search)
        scriptsRc = view.findViewById(R.id.rc_script)
        logViewAllBtn = view.findViewById(R.id.btn_logs_viewall)
        logsRc = view.findViewById(R.id.rc_logs)
        databasesRc = view.findViewById(R.id.rc_databases)
        databasesViewAllBtn = view.findViewById(R.id.btn_databases_viewall)
        botsNoneLav = view.findViewById(R.id.lav_bot_empty)
        botsNoneCl = view.findViewById(R.id.cl_bot_empty)
        logsNoneLav = view.findViewById(R.id.lav_logs_empty)
        logsNoneCl = view.findViewById(R.id.cl_logs_empty)
        databasesNoneLav = view.findViewById(R.id.lav_databases_empty)
        databasesNoneCl = view.findViewById(R.id.cl_databases_empty)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Logger.addLogAdapter(AndroidLogAdapter())

        btn_script_search.setOnClickListener {
            YoYo.with(Techniques.FadeOut)
                .withListener(object : Animator.AnimatorListener{
                    override fun onAnimationRepeat(p0: Animator?) {

                    }

                    override fun onAnimationEnd(p0: Animator?) {
                       btn_script_search.visibility = View.INVISIBLE
                    }

                    override fun onAnimationCancel(p0: Animator?) {

                    }

                    override fun onAnimationStart(p0: Animator?) {
                        YoYo.with(Techniques.FadeIn)
                            .withListener(object : Animator.AnimatorListener{
                                override fun onAnimationRepeat(p0: Animator?) {

                                }

                                override fun onAnimationEnd(p0: Animator?) {

                                }

                                override fun onAnimationCancel(p0: Animator?) {

                                }

                                override fun onAnimationStart(p0: Animator?) {
                                    et_search.visibility = View.VISIBLE
                                }
                            })
                            .duration(500)
                            .playOn(et_search)
                    }
                })
                .duration(300)
                .playOn(btn_script_search)
        }

        logViewAllBtn!!.setOnClickListener {
            startActivity(
                Intent(context, DashboardContentShowAllActivity::class.java)
                    .putExtra("type", "전체 로그")
            )
        }

        databasesViewAllBtn!!.setOnClickListener {
            startActivity(
                Intent(context, DashboardContentShowAllActivity::class.java)
                    .putExtra("type", "전체 데이터베이스")
            )
        }

        val layoutManagerHorizontal =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val layoutManagerVertical =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        val layoutManagerVertical2 =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        val jsPath = "$sdcard/${BotPathManager.JS}/"
        val simplePath = "$sdcard/${BotPathManager.SIMPLE}/"
        val logPath = "$sdcard/${BotPathManager.LOG}/"
        val databasePath = "$sdcard/${BotPathManager.DATABASE}/"

        val scriptItem = ArrayList<ScriptListItem>()
        val logItem = ArrayList<LogListItem>()
        val databaseItem = ArrayList<DatabaseListItem>()

        var jsList = File(jsPath).listFiles()
        var simpleList = File(simplePath).listFiles()
        var logList = File(logPath).listFiles()
        var databaseList = File(databasePath).listFiles()

        if (simpleList != null) {
            for (element in simpleList) {
                val name = element.name
                val onOff = BotPowerUtils.getIsOn(context!!, name)
                scriptItem.add(
                    ScriptListItem(
                        name,
                        onOff,
                        1,
                        RunTimeUtils.get(context!!, name),
                        R.drawable.ic_textsms_blue_24dp
                    )
                )
            }
        }
        if (jsList != null) {
            for (element in jsList) {
                val name = element.name
                val onOff = BotPowerUtils.getIsOn(context!!, name)
                scriptItem.add(
                    ScriptListItem(
                        name,
                        onOff,
                        0,
                        RunTimeUtils.get(context!!, name),
                        R.drawable.ic_javascript
                    )
                )
            }
        }
        if (logList != null) {
            Collections.sort(logList.asList(), kotlin.Comparator { t, t2 ->
                return@Comparator t.lastModified().compareTo(t2.lastModified())
            })

            for (i in logList.indices) {
                if(i >= 2) break
                val name = logList[i].name
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
        }
        if (databaseList != null) {
            Collections.sort(databaseList.asList(), kotlin.Comparator { t, t2 ->
                return@Comparator t.lastModified().compareTo(t2.lastModified())
            })

            for (i in databaseList.indices) {
                if(i >= 2) break
                val name = databaseList[i].name
                val item = DatabaseListItem(
                    name, StorageUtils.getFileSize(databaseList[i])
                )
                databaseItem.add(item)
            }
        }

        if (scriptItem.isEmpty()) {
            botsNoneCl!!.visibility = View.VISIBLE
            scriptsRc!!.visibility = View.INVISIBLE
            botsNoneLav!!.playAnimation()
        }
        else {
            botsNoneCl!!.visibility = View.INVISIBLE
            scriptsRc!!.visibility = View.VISIBLE
        }

        botsNoneLav!!.setOnClickListener {
            botsNoneLav!!.cancelAnimation()
            botsNoneLav!!.playAnimation()
        }

        if (logItem.isEmpty()) {
            logsNoneCl!!.visibility = View.VISIBLE
            logsRc!!.visibility = View.INVISIBLE
            logsNoneLav!!.playAnimation()
        }
        else {
            logsNoneCl!!.visibility = View.INVISIBLE
            logsRc!!.visibility = View.VISIBLE
        }

        logsNoneLav!!.setOnClickListener {
            logsNoneLav!!.cancelAnimation()
            logsNoneLav!!.playAnimation()
        }

        if (databaseItem.isEmpty()){
            databasesNoneCl!!.visibility = View.VISIBLE
            databasesRc!!.visibility = View.INVISIBLE
            databasesNoneLav!!.playAnimation()
        }
        else {
            databasesNoneCl!!.visibility = View.INVISIBLE
            databasesRc!!.visibility = View.VISIBLE
        }

        databasesNoneLav!!.setOnClickListener {
            databasesNoneLav!!.cancelAnimation()
            databasesNoneLav!!.playAnimation()
        }

        val scriptListAdapter = ScriptListAdapter(scriptItem, activity!!)
        scriptListAdapter.setOnScriptRemovedListener {
            scriptItem.clear()
            jsList = File(jsPath).listFiles()
            simpleList = File(simplePath).listFiles()
            if (simpleList != null) {
                for (element in simpleList) {
                    val name = element.name
                    val onOff = BotPowerUtils.getIsOn(context!!, name)
                    scriptItem.add(
                        ScriptListItem(
                            name,
                            onOff,
                            1,
                            RunTimeUtils.get(context!!, name),
                            R.drawable.ic_textsms_blue_24dp
                        )
                    )
                }
            }
            if (jsList != null) {
                for (element in jsList) {
                    val name = element.name
                    val onOff = BotPowerUtils.getIsOn(context!!, name)
                    scriptItem.add(
                        ScriptListItem(
                            name,
                            onOff,
                            0,
                            RunTimeUtils.get(context!!, name),
                            R.drawable.ic_javascript
                        )
                    )
                }
            }

            scriptListAdapter.notifyDataSetChanged()

            if (scriptItem.isEmpty()) {
                botsNoneCl!!.visibility = View.VISIBLE
                scriptsRc!!.visibility = View.INVISIBLE
                botsNoneLav!!.playAnimation()
            }
            else {
                botsNoneCl!!.visibility = View.INVISIBLE
                scriptsRc!!.visibility = View.VISIBLE
            }
        }

        val databaseListAdapter = DatabaseListAdapter(databaseItem, activity!!)
        databaseListAdapter.setOnDatabaseRemovedListener {
            databaseItem.clear()
            databaseList = File(databasePath).listFiles()
            if (databaseList != null) {
                Collections.sort(databaseList.asList(), kotlin.Comparator { t, t2 ->
                    return@Comparator t.lastModified().compareTo(t2.lastModified())
                })

                for (i in databaseList.indices) {
                    if(i >= 2) break
                    val name = databaseList[i].name
                    val item = DatabaseListItem(
                        name, StorageUtils.getFileSize(databaseList[i])
                    )
                    databaseItem.add(item)
                }
            }

            databaseListAdapter.notifyDataSetChanged()

            if (databaseItem.isEmpty()){
                databasesNoneCl!!.visibility = View.VISIBLE
                databasesRc!!.visibility = View.INVISIBLE
                databasesNoneLav!!.playAnimation()
            }
            else {
                databasesNoneCl!!.visibility = View.INVISIBLE
                databasesRc!!.visibility = View.VISIBLE
            }
        }

        val logListAdapter = LogListAdapter(logItem, activity!!)
        //TODO: 로그 아이템 삭제 이벤트

        scriptsRc!!.layoutManager = layoutManagerHorizontal
        scriptsRc!!.adapter = scriptListAdapter
        scriptsRc!!.addItemDecoration(ListDecoration()) //아이템 간격

        logsRc!!.layoutManager = layoutManagerVertical
        logsRc!!.adapter = logListAdapter
        logsRc!!.addItemDecoration(DividerItemDecoration(activity!!, 1)) //아이템 구분선

        databasesRc!!.layoutManager = layoutManagerVertical2
        databasesRc!!.adapter = databaseListAdapter

        et_search.imeOptions = EditorInfo.IME_ACTION_DONE
        et_search.setOnEditorActionListener { _, id, _ ->
            when(id){
                EditorInfo.IME_ACTION_DONE -> {
                    scriptListAdapter.sortSearch(et_search.text.toString())
                    scriptsRc!!.scrollToPosition(0)
                    YoYo.with(Techniques.FadeOut)
                        .withListener(object : Animator.AnimatorListener{
                            override fun onAnimationRepeat(p0: Animator?) {

                            }

                            override fun onAnimationEnd(p0: Animator?) {
                                et_search.visibility = View.INVISIBLE
                                et_search.text = SpannableStringBuilder("")
                            }

                            override fun onAnimationCancel(p0: Animator?) {

                            }

                            override fun onAnimationStart(p0: Animator?) {
                                YoYo.with(Techniques.FadeIn)
                                    .withListener(object : Animator.AnimatorListener{
                                        override fun onAnimationRepeat(p0: Animator?) {

                                        }

                                        override fun onAnimationEnd(p0: Animator?) {

                                        }

                                        override fun onAnimationCancel(p0: Animator?) {

                                        }

                                        override fun onAnimationStart(p0: Animator?) {
                                            btn_script_search.visibility = View.VISIBLE
                                        }
                                    })
                                    .duration(500)
                                    .playOn(btn_script_search)
                            }
                        })
                        .duration(300)
                        .playOn(et_search)
                    return@setOnEditorActionListener true
                }
                else -> return@setOnEditorActionListener false
            }
        }
    }

    class ListDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            if (parent.getChildAdapterPosition(view) != parent.adapter!!.itemCount - 1) {
                outRect.right = 30
            }
        }
    }

}