@file:Suppress("DEPRECATION", "NAME_SHADOWING")

package com.sungbin.autoreply.bot.three.view.bot.fragment

import android.animation.Animator
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.tiagohm.markdownview.MarkdownView
import br.tiagohm.markdownview.css.styles.Github
import com.airbnb.lottie.LottieAnimationView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
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
import com.sungbin.autoreply.bot.three.view.bot.activity.DashboardActivity
import com.sungbin.autoreply.bot.three.view.bot.activity.DashboardContentShowAllActivity
import com.sungbin.autoreply.bot.three.view.ui.bottombar.SmoothBottomBar
import com.sungbin.sungbintool.DataUtils
import com.sungbin.sungbintool.StorageUtils
import com.sungbin.sungbintool.StorageUtils.sdcard
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


@Suppress(
    "DEPRECATED_IDENTITY_EQUALS", "UNUSED_VARIABLE",
    "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
)
class DashboardFragment constructor(
    private val fragmentManage: FragmentManager,
    private val view: Int,
    private val bottombar: SmoothBottomBar,
    private val textview: TextView,
    private val isTutorialEnd: Boolean
) : Fragment() {

    private var searchEt: EditText? = null
    private var searchBtn: Button? = null
    private var scriptsRc: RecyclerView? = null
    private var logViewAllBtn: Button? = null
    private var logsRc: RecyclerView? = null
    private var databasesRc: RecyclerView? = null
    private var databasesViewAllBtn: Button? = null
    private var botsNoneCl: ConstraintLayout? = null
    private var botsNoneLav: LottieAnimationView? = null
    private var logsNoneCl: ConstraintLayout? = null
    private var logsNoneLav: LottieAnimationView? = null
    private var databasesNoneCl: ConstraintLayout? = null
    private var databasesNoneLav: LottieAnimationView? = null

    private lateinit var lavWelcome: LottieAnimationView
    private lateinit var tvWelcome: TextView
    private lateinit var tvScripts: TextView
    private lateinit var tvLogs: TextView
    private lateinit var tvDatabases: TextView
    private lateinit var btnNext: Button
    private lateinit var btnDone: Button
    private lateinit var mdvView: MarkdownView
    private lateinit var ivBanner: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        lavWelcome = view.findViewById(R.id.lav_welcome)
        tvWelcome = view.findViewById(R.id.tv_welcome)
        tvScripts = view.findViewById(R.id.tv_scripts)
        tvLogs = view.findViewById(R.id.tv_logs)
        tvDatabases = view.findViewById(R.id.tv_databases)
        btnNext = view.findViewById(R.id.btn_next)
        btnDone = view.findViewById(R.id.btn_done)
        mdvView = view.findViewById(R.id.mdv_view)
        ivBanner = view.findViewById(R.id.iv_banner)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val isTutorial = DataUtils.readData(
            context!!,
            "isTutorial",
            "true"
        ).toBoolean()

        if (isTutorial) {
            searchBtn!!.alpha = 0.1f
            searchEt!!.alpha = 0.1f
            scriptsRc!!.alpha = 0.1f
            logViewAllBtn!!.alpha = 0.1f
            logsRc!!.alpha = 0.1f
            databasesRc!!.alpha = 0.1f
            databasesViewAllBtn!!.alpha = 0.1f
            botsNoneLav!!.alpha = 0.1f
            botsNoneCl!!.alpha = 0.1f
            logsNoneLav!!.alpha = 0.1f
            logsNoneCl!!.alpha = 0.1f
            databasesNoneLav!!.alpha = 0.1f
            databasesNoneCl!!.alpha = 0.1f
            tvScripts.alpha = 0.1f
            tvLogs.alpha = 0.1f
            tvDatabases.alpha = 0.1f

            searchBtn!!.isEnabled = false
            searchEt!!.isEnabled = false
            scriptsRc!!.isEnabled = false
            logViewAllBtn!!.isEnabled = false
            logsRc!!.isEnabled = false
            databasesRc!!.isEnabled = false
            databasesViewAllBtn!!.isEnabled = false
            botsNoneLav!!.isEnabled = false
            botsNoneCl!!.isEnabled = false
            logsNoneLav!!.isEnabled = false
            logsNoneCl!!.isEnabled = false
            databasesNoneLav!!.isEnabled = false
            databasesNoneCl!!.isEnabled = false
            tvScripts.isEnabled = false
            tvLogs.isEnabled = false
            tvDatabases.isEnabled = false

            btnNext.setOnClickListener {
                textview.text = getString(R.string.sandbox)
                val fragmentTransaction = fragmentManage.beginTransaction()
                fragmentTransaction.replace(
                    view,
                    SandboxFragment(fragmentManage, view, bottombar, textview, true)
                ).commit()
                bottombar.setActiveItem(1)
                DashboardActivity.bottomBarIndex = 1
            }

            btnDone.setOnClickListener {
                DataUtils.saveData(
                    context!!,
                    "isTutorial",
                    "false"
                )

                searchBtn!!.alpha = 0.1f
                searchEt!!.alpha = 1.0f
                scriptsRc!!.alpha = 1.0f
                logViewAllBtn!!.alpha = 1.0f
                logsRc!!.alpha = 1.0f
                databasesRc!!.alpha = 1.0f
                databasesViewAllBtn!!.alpha = 1.0f
                botsNoneLav!!.alpha = 1.0f
                botsNoneCl!!.alpha = 1.0f
                logsNoneLav!!.alpha = 1.0f
                logsNoneCl!!.alpha = 1.0f
                databasesNoneLav!!.alpha = 1.0f
                databasesNoneCl!!.alpha = 1.0f
                tvScripts.alpha = 1.0f
                tvLogs.alpha = 1.0f
                tvDatabases.alpha = 1.0f
                searchBtn!!.isEnabled = true
                searchEt!!.isEnabled = true
                scriptsRc!!.isEnabled = true
                logViewAllBtn!!.isEnabled = true
                logsRc!!.isEnabled = true
                databasesRc!!.isEnabled = true
                databasesViewAllBtn!!.isEnabled = true
                botsNoneLav!!.isEnabled = true
                botsNoneCl!!.isEnabled = true
                logsNoneLav!!.isEnabled = true
                logsNoneCl!!.isEnabled = true
                databasesNoneLav!!.isEnabled = true
                databasesNoneCl!!.isEnabled = true
                tvScripts.isEnabled = true
                tvLogs.isEnabled = true
                tvDatabases.isEnabled = true

                lavWelcome.visibility = View.GONE
                tvWelcome.visibility = View.GONE
                ivBanner.visibility = View.GONE
                mdvView.visibility = View.GONE
                btnNext.visibility = View.GONE
                btnDone.visibility = View.GONE
            }

            if (isTutorialEnd) {
                lavWelcome.visibility = View.GONE
                tvWelcome.visibility = View.GONE
                btnNext.visibility = View.GONE
                btnDone.visibility = View.VISIBLE
                ivBanner.visibility = View.VISIBLE
                mdvView.visibility = View.VISIBLE
                mdvView.addStyleSheet(Github())
                mdvView.setEscapeHtml(false)
                mdvView.loadMarkdown(
                    "`KakaoTalkBotHub`는 [오픈소스](https://github.com/sungbin5304/KakaoTalkBotHub)(`GPL-3.0`)로 릴리즈 됩니다.<br/>" +
                            "API 목록 및 함수 설명은 [여기](https://github.com/sungbin5304/KakaoTalkBotHub/blob/master/README.md#main-function)서 확인 가능합니다.<br/><br/>" +
                            "**© 2020 SungBin. all right reserved.**" +
                            ""
                )
            } else {
                lavWelcome.visibility = View.VISIBLE
                tvWelcome.visibility = View.VISIBLE
                btnDone.visibility = View.GONE
                lavWelcome.playAnimation()
                Handler().postDelayed({
                    YoYo.with(Techniques.FadeOut)
                        .withListener(object : Animator.AnimatorListener {
                            override fun onAnimationRepeat(p0: Animator?) {
                            }

                            override fun onAnimationEnd(p0: Animator?) {
                                lavWelcome.setAnimation(R.raw.dashboard)
                                YoYo.with(Techniques.FadeIn)
                                    .withListener(object : Animator.AnimatorListener {
                                        override fun onAnimationRepeat(p0: Animator?) {
                                        }

                                        override fun onAnimationEnd(p0: Animator?) {
                                            lavWelcome.playAnimation()
                                        }

                                        override fun onAnimationCancel(p0: Animator?) {
                                        }

                                        override fun onAnimationStart(p0: Animator?) {

                                        }
                                    })
                                    .duration(500)
                                    .playOn(lavWelcome)
                            }

                            override fun onAnimationCancel(p0: Animator?) {
                            }

                            override fun onAnimationStart(p0: Animator?) {

                            }
                        })
                        .duration(500)
                        .playOn(lavWelcome)

                    YoYo.with(Techniques.FadeOut)
                        .withListener(object : Animator.AnimatorListener {
                            override fun onAnimationRepeat(p0: Animator?) {
                            }

                            override fun onAnimationEnd(p0: Animator?) {
                                tvWelcome.text = getText(R.string.dashboard_description)
                                YoYo.with(Techniques.FadeIn)
                                    .duration(500)
                                    .playOn(tvWelcome)

                                btnNext.visibility = View.VISIBLE
                                YoYo.with(Techniques.FadeIn)
                                    .duration(500)
                                    .playOn(btnNext)
                            }

                            override fun onAnimationCancel(p0: Animator?) {
                            }

                            override fun onAnimationStart(p0: Animator?) {
                            }
                        })
                        .duration(500)
                        .playOn(tvWelcome)
                }, 2500)
            }
        }

        searchBtn!!.setOnClickListener {
            YoYo.with(Techniques.FadeOut)
                .withListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {

                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        searchBtn!!.visibility = View.INVISIBLE
                    }

                    override fun onAnimationCancel(p0: Animator?) {

                    }

                    override fun onAnimationStart(p0: Animator?) {
                        YoYo.with(Techniques.FadeIn)
                            .withListener(object : Animator.AnimatorListener {
                                override fun onAnimationRepeat(p0: Animator?) {

                                }

                                override fun onAnimationEnd(p0: Animator?) {

                                }

                                override fun onAnimationCancel(p0: Animator?) {

                                }

                                override fun onAnimationStart(p0: Animator?) {
                                    searchEt!!.visibility = View.VISIBLE
                                }
                            })
                            .duration(300)
                            .playOn(searchEt!!)
                    }
                })
                .duration(300)
                .playOn(searchBtn!!)
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
                if (i >= 2) break
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
                if (i >= 2) break
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
        } else {
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
        } else {
            logsNoneCl!!.visibility = View.INVISIBLE
            logsRc!!.visibility = View.VISIBLE
        }

        logsNoneLav!!.setOnClickListener {
            logsNoneLav!!.cancelAnimation()
            logsNoneLav!!.playAnimation()
        }

        if (databaseItem.isEmpty()) {
            databasesNoneCl!!.visibility = View.VISIBLE
            databasesRc!!.visibility = View.INVISIBLE
            databasesNoneLav!!.playAnimation()
        } else {
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
            } else {
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
                    if (i >= 2) break
                    val name = databaseList[i].name
                    val item = DatabaseListItem(
                        name, StorageUtils.getFileSize(databaseList[i])
                    )
                    databaseItem.add(item)
                }
            }

            databaseListAdapter.notifyDataSetChanged()

            if (databaseItem.isEmpty()) {
                databasesNoneCl!!.visibility = View.VISIBLE
                databasesRc!!.visibility = View.INVISIBLE
                databasesNoneLav!!.playAnimation()
            } else {
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

        searchEt!!.imeOptions = EditorInfo.IME_ACTION_DONE
        searchEt!!.setOnEditorActionListener { _, id, _ ->
            when (id) {
                EditorInfo.IME_ACTION_DONE -> {
                    scriptListAdapter.sortSearch(searchEt!!.text.toString())
                    scriptsRc!!.scrollToPosition(0)
                    YoYo.with(Techniques.FadeOut)
                        .withListener(object : Animator.AnimatorListener {
                            override fun onAnimationRepeat(p0: Animator?) {

                            }

                            override fun onAnimationEnd(p0: Animator?) {
                                searchEt!!.visibility = View.INVISIBLE
                                searchEt!!.text = SpannableStringBuilder("")
                            }

                            override fun onAnimationCancel(p0: Animator?) {

                            }

                            override fun onAnimationStart(p0: Animator?) {
                                YoYo.with(Techniques.FadeIn)
                                    .withListener(object : Animator.AnimatorListener {
                                        override fun onAnimationRepeat(p0: Animator?) {

                                        }

                                        override fun onAnimationEnd(p0: Animator?) {

                                        }

                                        override fun onAnimationCancel(p0: Animator?) {

                                        }

                                        override fun onAnimationStart(p0: Animator?) {
                                            searchBtn!!.visibility = View.VISIBLE
                                        }
                                    })
                                    .duration(500)
                                    .playOn(searchBtn!!)
                            }
                        })
                        .duration(300)
                        .playOn(searchEt!!)
                    return@setOnEditorActionListener true
                }
                else -> return@setOnEditorActionListener false
            }
        }
    }

    private class ListDecoration : RecyclerView.ItemDecoration() { //아이템 간격
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