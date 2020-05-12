package com.sungbin.autoreply.bot.three.adapter.bot

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.bot.ScriptListItem
import com.sungbin.autoreply.bot.three.listener.KakaoTalkListener
import com.sungbin.autoreply.bot.three.utils.bot.BotPathManager
import com.sungbin.autoreply.bot.three.utils.bot.BotPowerUtils
import com.sungbin.autoreply.bot.three.view.bot.activity.ScriptEditActivity
import com.sungbin.autoreply.bot.three.view.bot.activity.SimpleEditActivity
import com.sungbin.sungbintool.DataUtils
import com.sungbin.sungbintool.StorageUtils
import com.sungbin.sungbintool.ToastUtils


@Suppress("DEPRECATION")
class ScriptListAdapter(private val list: ArrayList<ScriptListItem>?,
                        private val act: Activity) :
    RecyclerView.Adapter<ScriptListAdapter.ScriptListViewHolder>() {

    private var ctx: Context? = null

    interface OnScriptRemovedListener {
        fun onRemoved()
    }

    private var listener: OnScriptRemovedListener? = null
    fun setOnScriptRemovedListener(listener: OnScriptRemovedListener?) {
        this.listener = listener
    }

    fun setOnScriptRemovedListener(listener: () -> Unit) {
        this.listener = object : OnScriptRemovedListener {
            override fun onRemoved() {
                listener()
            }
        }
    }

    inner class ScriptListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.tv_script_name)
        var onoff: Switch = view.findViewById(R.id.sw_script_onoff)
        var lastTime: TextView = view.findViewById(R.id.tv_script_run_time)
        var type: ImageView = view.findViewById(R.id.iv_type)
        var menu: ImageView = view.findViewById(R.id.iv_menu)
        var state: View = view.findViewById(R.id.view_reload_state)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ScriptListViewHolder {
        ctx = viewGroup.context
        val view = LayoutInflater.from(ctx).inflate(R.layout.layout_script, viewGroup, false)
        return ScriptListViewHolder(view)
    }

    override fun onBindViewHolder(@NonNull viewholder: ScriptListViewHolder, position: Int) {
        var name = list!![position].name!!
        val type = list[position].type!!
        val onoff = list[position].onOff!!
        val lastTime = list[position].lastTime!!
        val image = list[position].image!!
        val originName = name

        name = if(type == 1){
            name.replace(".bot", "")
        }
        else {
            name.replace(".js", "")
        }

        viewholder.onoff.setOnCheckedChangeListener { _, tf ->
            BotPowerUtils.setOnOff(ctx!!, originName, tf)
        }

        if(onoff) {
            viewholder.state.background = ContextCompat.getDrawable(ctx!!,
                R.drawable.reload_done_view)
        }

        viewholder.type.setImageResource(image)
        viewholder.lastTime.text = lastTime
        viewholder.title.text = name
        viewholder.onoff.isChecked = onoff

        if (type == 1) {
            viewholder.onoff.setOnCheckedChangeListener { _, boolean ->
                if(boolean){
                    viewholder.state.background = ContextCompat.getDrawable(ctx!!,
                        R.drawable.reload_done_view)
                }
                else {
                    viewholder.state.background = ContextCompat.getDrawable(ctx!!,
                        R.drawable.reload_none_view)
                }
            }
        }

        viewholder.menu.setOnClickListener { view ->
            val popupMenu = popupMenu {
                section {
                    title = act.getString(R.string.string_edit)
                    item {
                        labelRes = R.string.string_bot_name
                        icon = R.drawable.ic_title_white_24dp
                        callback = {
                            ToastUtils.show(act, "눌림",
                            ToastUtils.SHORT, ToastUtils.INFO)
                        }
                    }
                    item {
                        labelRes = R.string.string_description
                        icon = R.drawable.ic_description_white_24dp
                        callback = {
                            ToastUtils.show(act, "눌림",
                                ToastUtils.SHORT, ToastUtils.INFO)
                        }
                    }
                    item {
                        labelRes = R.string.string_source
                        icon = R.drawable.ic_code_white_24dp
                        callback = {
                            if (type == 1) {
                                act.startActivity(
                                    Intent(act, SimpleEditActivity::class.java)
                                        .putExtra("name", name)
                                )
                            }
                            else {
                                act.startActivity(
                                    Intent(act, ScriptEditActivity::class.java)
                                        .putExtra("name", name)
                                        .putExtra(
                                            "script",
                                            StorageUtils.read(
                                                "${BotPathManager.JS}/$name.js",
                                                """
                            function response(room, msg, sender, isGroupChat, replier, ImageDB, package) {
                                /*
                                @String room : 메세지를 받은 방 이름 리턴
                                @String sender : 메세지를 보낸 상대의 이름 리턴
                                @Boolean isGroupChat : 메세지를 받은 방이 그룹채팅방(오픈채팅방은 그룹채팅방 취급) 인지 리턴
                                @Object replier : 메세지를 받은 방의 Action를 담은 Object 리턴
                                @Object ImageDB : 이미지 관련 데이터를 담은 Object 리턴
                                @String package : 메세지를 받은 어플의 패키지명 리턴
                                */
                            }    
                            """.trimIndent()
                                            )
                                        )
                                )
                            }
                        }
                    }
                }
                section {
                    title = "기타"
                    if(type == 0){
                        item {
                            labelRes = R.string.string_reload
                            icon = R.drawable.ic_autorenew_white_24dp
                            callback = {
                                lateinit var statue: String
                                val ms1 = System.currentTimeMillis()
                                val pDialog = SweetAlertDialog(
                                    act,
                                    SweetAlertDialog.PROGRESS_TYPE
                                )
                                pDialog.progressHelper.barColor = ContextCompat.getColor(
                                    act,
                                    R.color.colorPrimary
                                )
                                pDialog.titleText = act.getString(R.string.string_reloading)
                                pDialog.setCancelable(false)
                                val thread = Thread {
                                    statue = KakaoTalkListener.initializeJavaScript(name)
                                    act.runOnUiThread {
                                        pDialog.show()
                                    }
                                }
                                thread.start()
                                thread.join()
                                val ms2 = System.currentTimeMillis()
                                val reloadTime = (ms2 - ms1).toString()
                                pDialog.confirmText = "닫기"
                                pDialog.confirmButtonBackgroundColor =
                                    ContextCompat.getColor(act, R.color.colorPrimary)
                                if (statue != "true") {
                                    pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                                    pDialog.titleText = "리로드 실패"
                                    pDialog.contentText = "리로드중 오류가 발생했습니다.<br>" +
                                            "<font color=#EF5350>$statue</font>"
                                    viewholder.state.background = ContextCompat.getDrawable(
                                        ctx!!,
                                        R.drawable.reload_error_view
                                    )

                                    if(!DataUtils.readData(ctx!!, "KeepScope", "false").toBoolean()){
                                        viewholder.onoff.isChecked = false
                                        BotPowerUtils.setOnOff(ctx!!, name, false)
                                    }
                                }
                                else {
                                    pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                    pDialog.titleText = "리로드 성공"
                                    pDialog.contentText = "리로드가 완료되었습니다.<br>" +
                                            "<font color=#4CAF50>리로드 시간 : $reloadTime ms</font>"
                                    viewholder.state.background = ContextCompat.getDrawable(
                                        ctx!!,
                                        R.drawable.reload_done_view
                                    )
                                }
                            }
                        }
                    }
                    item {
                        labelRes = R.string.string_share
                        icon = R.drawable.ic_share_white_24dp
                        callback = {
                            ToastUtils.show(act, "눌림",
                                ToastUtils.SHORT, ToastUtils.INFO)
                        }
                    }
                }
                section {
                    title = act.getString(R.string.string_danger)
                    item {
                        labelRes = R.string.string_delete
                        icon = R.drawable.ic_delete_white_24dp
                        callback = {
                            if (type == 1) {
                                StorageUtils.deleteAll("${BotPathManager.SIMPLE}/$name")
                            }
                            else {
                                StorageUtils.delete("${BotPathManager.JS}/$name.js")
                            }
                            if(listener != null) listener!!.onRemoved()
                        }
                    }
                }
            }

            popupMenu.show(act, view)
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun getItem(position: Int): ScriptListItem {
        return list!![position]
    }

    fun sortSearch(search: String){
        var item: ScriptListItem? = null
        for(element in list!!){
            if(element.name!!.contains(search)){
                item = element
                break
            }
            else continue
        }
        if(item != null){
            list.remove(item)
            list.add(0, item)
        }
        notifyDataSetChanged()
    }

}
