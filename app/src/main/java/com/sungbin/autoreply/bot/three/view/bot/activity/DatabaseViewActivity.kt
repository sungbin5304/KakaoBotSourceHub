package com.sungbin.autoreply.bot.three.view.bot.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sungbin.autoreply.bot.three.R
import com.sungbin.sungbintool.DialogUtils
import com.sungbin.sungbintool.StorageUtils
import com.sungbin.sungbintool.StringUtils
import com.sungbin.sungbintool.ToastUtils
import kotlinx.android.synthetic.main.activity_database_view.*
import kotlinx.android.synthetic.main.content_database_view.*
import org.json.JSONObject
import java.util.*


/**
 * Created by SungBin on 2020-05-13.
 */

class DatabaseViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database_view)

        toolbar_title.text = intent!!.getStringExtra("name")!!

        val path = intent!!.getStringExtra("path")!!
        val content = StorageUtils.read(path, "")!!
        if(path.toLowerCase(Locale.getDefault()).contains(".json")) {
            try {
                ToastUtils.show(
                    this,
                    getString(R.string.json_edit_developing),
                    ToastUtils.SHORT,
                    ToastUtils.INFO
                )
                jv_viewer.setJson(JSONObject(content))
                ib_open.setOnClickListener {
                    jv_viewer.expandJson()
                }
                ib_close.setOnClickListener {
                    jv_viewer.collapseJson()
                }
            } catch (e: Exception) {
                DialogUtils.show(
                    this,
                    getString(R.string.wrong_json),
                    e.message.toString(),
                    null
                )
                jv_viewer.visibility = View.GONE
                et_viewer.visibility = View.VISIBLE
                et_viewer.text = StringUtils.toEditable(content)
            }
        }
        else {
            jv_viewer.visibility = View.GONE
            et_viewer.visibility = View.VISIBLE
            et_viewer.text = StringUtils.toEditable(content)
        }
    }
}