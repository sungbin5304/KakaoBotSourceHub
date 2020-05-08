package com.sungbin.autoreply.bot.three.view.activity

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Layout
import android.text.Selection
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.utils.ui.EdittextHistoryManager
import com.sungbin.sungbintool.StorageUtils
import com.sungbin.sungbintool.ToastUtils
import kotlinx.android.synthetic.main.activity_script_edit.*
import kotlinx.android.synthetic.main.content_script_edit.*
import me.testica.codeeditor.SyntaxHighlightRule
import org.apache.commons.lang3.StringUtils
import org.mozilla.javascript.CompilerEnvirons
import org.mozilla.javascript.Context
import org.mozilla.javascript.Parser
import org.mozilla.javascript.ast.NodeVisitor


class ScriptEditActivity : AppCompatActivity() {

    val classNameList = ArrayList<String>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_script_edit)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        val scriptName = intent.getStringExtra("name")!!
        val editText = editor.getEditText()
        val edittextHistoryManager =
            EdittextHistoryManager(
                editText
            )
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
        for(element in items) list.add(element)
        var highlightRuleString = ""
        for(element in list){
            highlightRuleString += "|$element"
        }
        highlightRuleString = highlightRuleString.replaceFirst("|", "")

        toolbar_title.text = scriptName
        action_left_slash.text = "\\"

        ib_menu.setOnClickListener {
            val popupMenu = popupMenu {
                section {
                    title = getString(R.string.string_editor)
                    item {
                        labelRes = R.string.string_undo
                        icon = R.drawable.ic_undo_white_24dp
                        callback = {
                            edittextHistoryManager.undo()
                        }
                    }
                    item {
                        labelRes = R.string.string_redo
                        icon = R.drawable.ic_redo_white_24dp
                        callback = {
                            edittextHistoryManager.redo()
                        }
                    }
                    item {
                        labelRes = R.string.string_search_kr
                        icon = R.drawable.ic_search_white_24dp
                        callback = {
                            //검색
                        }
                    }
                    item {
                        labelRes = R.string.string_replace
                        icon = R.drawable.ic_find_replace_white_24dp
                        callback = {
                            //바꾸기
                        }
                    }
                }
                section {
                    title = getString(R.string.string_setting_kr)
                    item {
                        labelRes = R.string.string_highlighting
                        icon = R.drawable.ic_highlight_white_24dp
                        callback = {
                            //하이라이트 설정
                        }
                    }
                    item {
                        labelRes = R.string.string_auto_complete
                        icon = R.drawable.ic_flash_auto_white_24dp
                        callback = {
                            //자동완성 설정
                        }
                    }
                }
                section {
                    title = getString(R.string.string_other)
                    item {
                        labelRes = R.string.string_save
                        icon = R.drawable.ic_save_white_24dp
                        callback = {
                            StorageUtils.save("KakaoTalkBotHub/Bots/JavaScript/$scriptName.js",
                                editText.text.toString())
                            ToastUtils.show(applicationContext,
                                getString(R.string.save_success),
                                ToastUtils.SHORT, ToastUtils.SUCCESS)
                        }
                    }
                }
            }

            popupMenu.show(applicationContext, it)
        }

        action_indent.setOnClickListener { editText.insert("\t\t\t\t") }
        action_undo.setOnClickListener { edittextHistoryManager.undo() }
        action_redo.setOnClickListener { edittextHistoryManager.redo() }
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

        editor.setSyntaxHighlightRules(
            SyntaxHighlightRule("\\d|true|false", "#4A8BD6"), //숫자 (파란색)
            SyntaxHighlightRule("\\\"[\\s\\S]*?\\\"", "#26c962"), //텍스트 (초록색)
            SyntaxHighlightRule(highlightRuleString, "#ed975c"), //클레스(?) (주황색)
            SyntaxHighlightRule("(\\/\\*(\\s*|.*?)*\\*\\/)", "#9ea7aa"), //주석 (회색)
            SyntaxHighlightRule("(\\/\\*(\\s*|.*?)*\\*\\/)|(\\/\\/.*)", "#9ea7aa")  //주석 (회색)
        )
        editor.setPadding(16, 16, 16, 16)

        editor.setText(intent.getStringExtra("script")!!)
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
                    val layout: Layout = editor.getEditText().layout
                    val selectionStart = Selection.getSelectionStart(editor.getEditText().text)
                    val now = s.toString().split("\n")[layout.getLineForOffset(selectionStart)]
                        .trim() //지금 쓰고있는 단어 가져오기
                        .split(" ")[s.toString().split("\n")
                            [layout.getLineForOffset(selectionStart)]
                        .trim().split(" ").size - 1]
                    Log.d("DDD", now)
                    val all = s.toString()
                    for(element in classNameList) {
                        Log.d("TTT", element)
                        if(!list.contains(element))
                            list.add(element)
                    }
                    for (i in list.indices) {
                        Log.d("SSS", list[i])
                        if (list[i].startsWith(now) && list[i] != now
                            && now.isNotBlank() && now.replace(" ", "").length > 1) {
                            suggestList.add(list[i])
                        }
                    }
                    if(suggestList.size == 0) {
                        append_auto.visibility = View.GONE
                    }
                    else {
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
                            !StringUtils.isBlank(
                                all.split("\n")[layout.getLineForOffset(selectionStart)]
                            )
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
                    Log.d("EEE", e.toString())
                }
            }
        })
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
        }
        catch (e: Exception){
        }
    }

    private fun String.checkCharCount(find: String?): Int {
        return StringUtils.countMatches(this, find)
    }

    private fun EditText.insert(tag: String?) {
        this.text.insert(this.selectionStart, tag)
    }

}
