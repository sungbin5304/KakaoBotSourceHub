package com.sungbin.autoreply.bot.three.view.bot.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Layout
import android.text.Selection
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Switch
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.balsikandar.crashreporter.CrashReporter
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.google.android.material.textfield.TextInputEditText
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.adapter.bot.EditorFindAdapter
import com.sungbin.autoreply.bot.three.dto.bot.EditorFindItem
import com.sungbin.autoreply.bot.three.utils.bot.BotPathManager
import com.sungbin.sungbintool.*
import kotlinx.android.synthetic.main.activity_script_edit.*
import kotlinx.android.synthetic.main.activity_script_edit.nv_navigation
import kotlinx.android.synthetic.main.content_script_edit.*
import kotlinx.android.synthetic.main.fragment_sandbox.*
import org.mozilla.javascript.CompilerEnvirons
import org.mozilla.javascript.Context
import org.mozilla.javascript.Parser
import org.mozilla.javascript.ast.NodeVisitor
import java.util.*
import kotlin.collections.ArrayList


class ScriptEditActivity : AppCompatActivity() {

    private val classNameList = ArrayList<String>()
    private val timer = Timer()

    @SuppressLint("InflateParams")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_script_edit)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        DialogUtils.showOnce(
            this,
            getString(R.string.experimental_function),
            getString(R.string.editor_experimental_function_description),
            "experimental_editor2",
            null, false
        )

        val editText = sce_editor.editor
        val scriptName = intent.getStringExtra("name")!!
        val textSize = DataUtils.readData(applicationContext, "TextSize", "17").toInt()
        val autoSave = DataUtils.readData(applicationContext, "AutoSave", "true").toBoolean()
        val notHighting = DataUtils.readData(applicationContext, "NotHighting", "false").toBoolean()
        val notErrorHighting =
            DataUtils.readData(applicationContext, "NotErrorHighting", "false").toBoolean()

        val headerView = LayoutInflater
            .from(applicationContext)
            .inflate(R.layout.header_layout_editor_find, null, false)
        val etFind = headerView.findViewById<TextInputEditText>(R.id.et_find)
        val swIgnoreUpper = headerView.findViewById<Switch>(R.id.sw_ignore)
        val rvList = headerView.findViewById<RecyclerView>(R.id.rv_list)

        rvList.layoutManager = LinearLayoutManager(
            applicationContext,
            LinearLayoutManager.VERTICAL,
            false
        )

        etFind.imeOptions = EditorInfo.IME_ACTION_SEARCH
        etFind.setOnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_SEARCH) {
                val items = ArrayList<EditorFindItem>()
                val result = sce_editor.findText(
                    etFind.text.toString(),
                    swIgnoreUpper.isChecked
                )
                if (result.isNotEmpty()) {
                    for (i in result.indices) {
                        val array = result[i]
                        val item = EditorFindItem(
                            editText.text.split("\n")[array[0]],
                            etFind.text.toString(),
                            array[0],
                            array[1]
                        )
                        items.add(item)
                    }
                } else {
                    items.add(
                        EditorFindItem(
                            getString(R.string.find_none),
                            "null",
                            -1,
                            -1
                        )
                    )
                }
                val adapter = EditorFindAdapter(items)
                adapter.setOnItemClickListener { findText, _, line, index ->
                    if (index > 0) {
                        val i = getStartIndex(editText, line, index)
                        editText.setSelection(i, i + findText.length)
                    }
                    dl_layout.closeDrawer(GravityCompat.START)
                }
                rvList.adapter = adapter
                adapter.notifyDataSetChanged()
            } else return@setOnEditorActionListener false
            return@setOnEditorActionListener true
        }

        nv_navigation.addHeaderView(headerView)

        val typeface = ResourcesCompat.getFont(applicationContext, R.font.d2coding)
        editText.typeface = typeface

        editText.textSize = textSize.toFloat()
        if (autoSave) {
            timer.schedule(
                AutoSaveTimer(
                    this,
                    editText,
                    scriptName
                ),
                300000,
                300000
            )
        }

        toolbar_title.text = scriptName
        if (notHighting) sce_editor.applyHighlight = false

        val suggestList: ArrayList<String> = ArrayList()
        val list = ArrayList<String>()
        val items = arrayOf(
            "String",
            "File",
            "java",
            "io",
            "Array",
            "int",
            "function",
            "return",
            "var",
            "let",
            "const",
            "if",
            "else",
            "switch",
            "for",
            "while",
            "do",
            "break",
            "continue",
            "case",
            "in",
            "with",
            "true",
            "false",
            "new",
            "null",
            "undefined",
            "typeof",
            "delete",
            "try",
            "catch",
            "finally",
            "prototype",
            "this",
            "super",
            "default",
            "prototype"
        )
        for (element in items) list.add(element)
        var highlightRuleString = ""
        for (element in list) {
            highlightRuleString += "|$element"
        }
        highlightRuleString = highlightRuleString.replaceFirst("|", "")

        action_left_slash.text = "\\"


        ib_save.setOnClickListener {
            StorageUtils.save(
                "${BotPathManager.JS}/$scriptName.js",
                editText.text.toString()
            )
            ToastUtils.show(
                applicationContext,
                getString(R.string.saved),
                ToastUtils.SHORT,
                ToastUtils.SUCCESS
            )
            Utils.copy(applicationContext, editText.text.toString())
        }
        ib_menu.setOnClickListener {
            val popupMenu = popupMenu {
                section {
                    title = getString(R.string.editor)
                    item {
                        labelRes = R.string.undo
                        icon = R.drawable.ic_undo_white_24dp
                        callback = {
                            sce_editor.undo()
                        }
                    }
                    item {
                        labelRes = R.string.redo
                        icon = R.drawable.ic_redo_white_24dp
                        callback = {
                            sce_editor.redo()
                        }
                    }
                    item {
                        labelRes = R.string.search_kr
                        icon = R.drawable.ic_search_white_24dp
                        callback = {
                            dl_drawer.openDrawer(GravityCompat.START)
                        }
                    }
                    item {
                        labelRes = R.string.replace
                        icon = R.drawable.ic_find_replace_white_24dp
                        callback = {
                            //바꾸기
                        }
                    }
                }
                section {
                    title = getString(R.string.setting_kr)
                    item {
                        labelRes = R.string.highlighting
                        icon = R.drawable.ic_highlight_white_24dp
                        callback = {
                            //하이라이트 설정
                        }
                    }
                    item {
                        labelRes = R.string.auto_complete
                        icon = R.drawable.ic_flash_auto_white_24dp
                        callback = {
                            //자동완성 설정
                        }
                    }
                }
                section {
                    title = getString(R.string.other)
                    item {
                        labelRes = R.string.save
                        icon = R.drawable.ic_save_white_24dp
                        callback = {
                            StorageUtils.save(
                                "${BotPathManager.JS}/$scriptName.js",
                                editText.text.toString()
                            )
                            ToastUtils.show(
                                applicationContext,
                                getString(R.string.saved),
                                ToastUtils.SHORT,
                                ToastUtils.SUCCESS
                            )
                            Utils.copy(applicationContext, editText.text.toString())
                        }
                    }
                }
            }

            popupMenu.show(applicationContext, it)
        }

        action_indent.setOnClickListener { editText.insert("\t\t\t\t") }
        action_undo.setOnClickListener { sce_editor.undo() }
        action_redo.setOnClickListener { sce_editor.redo() }
        action_right_big.setOnClickListener { editText.insert("{") }
        action_left_big.setOnClickListener { editText.insert("}") }
        action_right_small.setOnClickListener { editText.insert("(") }
        action_left_small.setOnClickListener { editText.insert(")") }
        action_right_slash.setOnClickListener { editText.insert("/") }
        action_left_slash.setOnClickListener { editText.insert("\\") }
        action_big_quote.setOnClickListener { editText.insert("\"") }
        action_small_quote.setOnClickListener { editText.insert("'") }
        action_dot.setOnClickListener { editText.insert(".") }
        action_end.setOnClickListener { editText.insert(";") }
        action_plus.setOnClickListener { editText.insert("+") }
        action_minus.setOnClickListener { editText.insert("-") }
        action_underbar.setOnClickListener { editText.insert("_") }

        /*editor.setSyntaxHighlightRules(
            SyntaxHighlightRule("\\d|true|false", "#4A8BD6"), //숫자 (파란색)
            SyntaxHighlightRule("\\\"[\\s\\S]*?\\\"", "#26c962"), //텍스트 (초록색)
            SyntaxHighlightRule(highlightRuleString, "#ed975c"), //클레스(?) (주황색)
            SyntaxHighlightRule("(\\/\\*(\\s*|.*?)*\\*\\/)", "#9ea7aa"), //주석 (회색)
            SyntaxHighlightRule("(\\/\\*(\\s*|.*?)*\\*\\/)|(\\/\\/.*)", "#9ea7aa")  //주석 (회색)
        )*/

        editText.setPadding(16, 16, 16, 16)
        editText.setText(intent.getStringExtra("script")!!)
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                try {
                    suggestList.clear()
                    loadClassName(editText.text.toString())
                    val layout: Layout = editText.layout
                    val selectionStart = Selection.getSelectionStart(editText.text)
                    val now = s.toString().split("\n")[layout.getLineForOffset(selectionStart)]
                        .trim() //지금 쓰고있는 단어 가져오기
                        .split(" ")[s.toString().split("\n")
                            [layout.getLineForOffset(selectionStart)]
                        .trim().split(" ").size - 1]
                    Log.d("DDD", now)
                    val all = s.toString()
                    for (element in classNameList) {
                        Log.d("TTT", element)
                        if (!list.contains(element))
                            list.add(element)
                    }
                    for (i in list.indices) {
                        Log.d("SSS", list[i])
                        if (list[i].startsWith(now) && list[i] != now
                            && now.isNotBlank() && now.replace(" ", "").length > 1
                        ) {
                            suggestList.add(list[i])
                        }
                    }
                    if (suggestList.size == 0) {
                        append_auto.visibility = View.GONE
                    } else {
                        if (suggestList.isNotEmpty()) {
                            if (suggestList.size == 1) {
                                append_auto.visibility = View.VISIBLE
                                append_auto.text = suggestList[0]
                                append_auto.setOnClickListener {
                                    append_auto.visibility = View.GONE
                                    try {
                                        editText.insert(suggestList[0].replace(now, ""))
                                    } catch (ignored: Exception) {
                                    }
                                }
                            }
                        } else if (suggestList.size != list.size &&
                            !all.split("\n")[layout.getLineForOffset(selectionStart)].isBlank()
                        ) {
                            append_auto.visibility = View.VISIBLE
                            append_auto.text = getString(R.string.auto_complete)
                            append_auto.setOnClickListener {
                                val p = PopupMenu(applicationContext, it)
                                for (i in 0 until suggestList.size) {
                                    p.menu.add(0, i, 0, suggestList[i])
                                }
                                p.setOnMenuItemClickListener { menuItem ->
                                    append_auto.visibility = View.GONE
                                    editText.insert(
                                        suggestList[menuItem.itemId].replace(now, "")
                                    )
                                    return@setOnMenuItemClickListener false
                                }
                                p.show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    CrashReporter.logException(e)
                }
            }
        })
    }

    private fun getStartIndex(editText: EditText, line: Int, index: Int): Int {
        var i = 0
        val texts = editText.text.split("\n")
        for (n in 0..line) i += texts[n].length
        return i + index
    }

    override fun onBackPressed() {
        super.onBackPressed()
        timer.cancel()
    }

    private fun loadClassName(source: String) {
        try {
            classNameList.clear()
            val env = CompilerEnvirons()
            env.languageVersion = Context.VERSION_ES6
            env.optimizationLevel = -1
            val parser = Parser(env)
            val nv = NodeVisitor { node ->
                val type = node.javaClass.name
                if (type == "org.mozilla.javascript.ast.Name") {
                    val name = node.toSource()
                    classNameList.add(name)
                }
                true
            }
            parser.parse(source, null, 1).visitAll(nv)
        } catch (e: Exception) {
        }
    }

    private fun EditText.insert(tag: String) {
        this.text.insert(this.selectionStart, tag)
    }

    private class AutoSaveTimer
    constructor(
        private val activity: Activity,
        private val editText: EditText,
        private val scriptName: String
    ) : TimerTask() {

        override fun run() {
            StorageUtils.save(
                "${BotPathManager.JS}/$scriptName.js",
                editText.text.toString()
            )

            activity.runOnUiThread {
                ToastUtils.show(
                    activity,
                    activity.getString(R.string.auto_saving),
                    ToastUtils.SHORT,
                    ToastUtils.SUCCESS
                )
                Utils.copy(activity, editText.text.toString())
            }
        }
    }

}
