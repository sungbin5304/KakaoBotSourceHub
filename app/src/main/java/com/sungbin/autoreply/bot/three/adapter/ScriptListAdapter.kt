package com.sungbin.autoreply.bot.three.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.graphics.Typeface.createFromAsset
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.google.android.material.snackbar.Snackbar
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.ScriptListItem
import com.sungbin.autoreply.bot.three.listener.KakaoTalkListener
import com.sungbin.autoreply.bot.three.utils.BotPowerUtils
import com.sungbin.autoreply.bot.three.view.activity.ScriptEditActivity
import com.sungbin.sungbintool.StorageUtils
import com.sungbin.sungbintool.ToastUtils


@Suppress("DEPRECATION")
class ScriptListAdapter(private val list: ArrayList<ScriptListItem>?, private val act: Activity) :
    RecyclerView.Adapter<ScriptListAdapter.ScriptListViewHolder>() {

    private var ctx: Context? = null
    private var beforeDeleteIndex = -1
    private var beforeDeleteSource = ""
    private var beforeDeleteItem: ScriptListItem? = null

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
        } else {
            name.replace(".js", "")
        }

        viewholder.onoff.setOnCheckedChangeListener { _, tf ->
            BotPowerUtils.setOnOff(ctx!!, originName, tf)
        }

        viewholder.type.setImageResource(image)
        viewholder.lastTime.text = lastTime
        viewholder.title.text = name
        viewholder.onoff.isChecked = onoff
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
                            act.startActivity(
                                Intent(act, ScriptEditActivity::class.java)
                                    .putExtra("name", name)
                                    .putExtra("script",
                                        StorageUtils.read("AutoReply Bot/Bots/JavaScript/$name.js",
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
                            """.trimIndent()))
                            )
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
                                val state = KakaoTalkListener.initializeJavaScript(name).toString()
                                if(state != "true") {
                                    ToastUtils.show(ctx!!,
                                        state, ToastUtils.LONG, ToastUtils.ERROR)
                                    viewholder.state.background = ContextCompat.getDrawable(ctx!!,
                                        R.drawable.reload_error_view)
                                }
                                else {
                                    ToastUtils.show(ctx!!,
                                        ctx!!.getString(R.string.reload_success),
                                        ToastUtils.LONG, ToastUtils.SUCCESS)
                                    viewholder.state.background = ContextCompat.getDrawable(ctx!!,
                                        R.drawable.reload_done_view)
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
                    item {
                        labelRes = R.string.string_delete
                        icon = R.drawable.ic_delete_white_24dp
                        callback = {
                            beforeDeleteItem = list[position]
                            beforeDeleteIndex = list.indexOf(beforeDeleteItem!!)
                            beforeDeleteSource = StorageUtils.read("AutoReply Bot/Bots/JavaScript/$name.js",
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
                            """.trimIndent())

                            StorageUtils.delete("AutoReply Bot/Bots/JavaScript/$name.js")
                            list.remove(list[position])
                            notifyDataSetChanged()
                            val bar = Snackbar.make(view, "$name 스크립트가 삭제되었습니다.", 3000)
                                .setActionTextColor(ContextCompat.getColor(ctx!!, R.color.colorPrimaryDark))
                                .setAction("되돌리기") {
                                    StorageUtils.save("AutoReply Bot/Bots/JavaScript/$name.js", beforeDeleteSource)
                                    list.add(beforeDeleteIndex, beforeDeleteItem!!)
                                    notifyDataSetChanged()
                                }
                            bar.view.setBackgroundColor(ContextCompat.getColor(ctx!!, R.color.colorAccent))
                            bar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                                .typeface = ResourcesCompat.getFont(ctx!!, R.font.nanumgothic)
                            bar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
                                .typeface = ResourcesCompat.getFont(ctx!!, R.font.nanumgothic)
                            bar.show()
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
