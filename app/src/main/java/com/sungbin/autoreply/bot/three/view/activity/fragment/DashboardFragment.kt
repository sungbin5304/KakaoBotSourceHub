@file:Suppress("DEPRECATION")

package com.sungbin.autoreply.bot.three.view.activity.fragment

import android.animation.Animator
import android.graphics.Rect
import android.os.Bundle
import android.os.Environment
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.adapter.DatabaseListAdapter
import com.sungbin.autoreply.bot.three.adapter.LogListAdapter
import com.sungbin.autoreply.bot.three.adapter.ScriptListAdapter
import com.sungbin.autoreply.bot.three.dto.bot.DatabaseListItem
import com.sungbin.autoreply.bot.three.dto.bot.LogListItem
import com.sungbin.autoreply.bot.three.dto.bot.ScriptListItem
import com.sungbin.autoreply.bot.three.utils.bot.BotPowerUtils
import com.sungbin.autoreply.bot.three.utils.bot.LogUtils
import kotlinx.android.synthetic.main.fragment_dashboard.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class DashboardFragment : Fragment() {

    var searchEt: EditText? = null
    var searchBtn: Button? = null
    var scriptsRc: RecyclerView? = null
    var logViewAllBtn: Button? = null
    var logsRc: RecyclerView? = null
    var databasesRc: RecyclerView? = null
    var databasesViewAllBtn: Button? = null
    var scriptsNoneTv: TextView? = null
    var logsNoneTv: TextView? = null
    var databasesNoneTv: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        searchBtn = view.findViewById(R.id.btn_script_search)
        searchEt = view.findViewById(R.id.et_search)
        scriptsRc = view.findViewById(R.id.rc_script)
        logViewAllBtn = view.findViewById(R.id.btn_logs_viewall)
        logsRc = view.findViewById(R.id.rc_logs)
        databasesRc = view.findViewById(R.id.rc_databases)
        databasesViewAllBtn = view.findViewById(R.id.btn_databases_viewall)
        scriptsNoneTv = view.findViewById(R.id.tv_script_none)
        logsNoneTv = view.findViewById(R.id.tv_log_none)
        databasesNoneTv = view.findViewById(R.id.tv_database_none)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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

        val sdcard = Environment.getExternalStorageDirectory().absolutePath
        val layoutManagerHorizontal =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val layoutManagerVertical =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        val layoutManagerVertical2 =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        val scriptItem = ArrayList<ScriptListItem>()
        val logItem = ArrayList<LogListItem>()
        val databaseItem = ArrayList<DatabaseListItem>()

        val jsPath = "$sdcard/AutoReply Bot/Bots/JavaScript/"
        val simplePath = "$sdcard/AutoReply Bot/Bots/AutoReply/"
        val logPath = "$sdcard/AutoReply Bot/Log/"

        val jsList = File(jsPath).listFiles()
        val simpleList = File(simplePath).listFiles()
        val logList = File(logPath).listFiles()

        if (simpleList != null) {
            for (element in simpleList) {
                val name = element.name
                val onOff = BotPowerUtils.getIsOn(context!!, name)
                scriptItem.add(
                    ScriptListItem(
                        name,
                        onOff,
                        1,
                        "마지막작동: ??",
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
                        "마지막작동: ??",
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
                Log.d("SIZE", i.toString())
            }
        }

        if (scriptItem.isEmpty()) {
            scriptsNoneTv!!.visibility = View.VISIBLE
            scriptsRc!!.visibility = View.INVISIBLE
        }
        else {
            scriptsNoneTv!!.visibility = View.INVISIBLE
            scriptsRc!!.visibility = View.VISIBLE
        }
        if (logItem.isEmpty()) {
            logsNoneTv!!.visibility = View.VISIBLE
            logsRc!!.visibility = View.INVISIBLE
        }
        else {
            logsNoneTv!!.visibility = View.INVISIBLE
            logsRc!!.visibility = View.VISIBLE
        }
        if (databaseItem.isEmpty()){
            databasesNoneTv!!.visibility = View.VISIBLE
            databasesRc!!.visibility = View.INVISIBLE
        }
        else {
            databasesNoneTv!!.visibility = View.INVISIBLE
            databasesRc!!.visibility = View.VISIBLE
        }

        val scriptListAdapter = ScriptListAdapter(scriptItem, activity!!)
        scriptsRc!!.layoutManager = layoutManagerHorizontal
        scriptsRc!!.adapter = scriptListAdapter
        scriptsRc!!.addItemDecoration(ListDecoration()) //아이템 간격

        val logListAdapter = LogListAdapter(logItem, activity!!)
        logsRc!!.layoutManager = layoutManagerVertical
        logsRc!!.adapter = logListAdapter
        logsRc!!.addItemDecoration(DividerItemDecoration(activity!!, 1)) //아이템 구분선

        val databaseListAdapter = DatabaseListAdapter(databaseItem, activity!!)
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