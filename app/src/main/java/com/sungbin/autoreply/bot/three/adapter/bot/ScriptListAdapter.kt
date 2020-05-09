package com.sungbin.autoreply.bot.three.adapter.bot

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.sungbin.autoreply.bot.three.dto.bot.ScriptListItem
import com.sungbin.autoreply.bot.three.listener.KakaoTalkListener
import com.sungbin.autoreply.bot.three.utils.bot.BotPowerUtils
import com.sungbin.autoreply.bot.three.utils.bot.SimpleBotUtils
import com.sungbin.autoreply.bot.three.view.activity.ScriptEditActivity
import com.sungbin.autoreply.bot.three.view.activity.SimpleEditActivity
import com.sungbin.sungbintool.StorageUtils
import com.sungbin.sungbintool.ToastUtils


@Suppress("DEPRECATION")
class ScriptListAdapter(private val list: ArrayList<ScriptListItem>?, private val act: Activity) :
    RecyclerView.Adapter<ScriptListAdapter.ScriptListViewHolder>() {

    private var ctx: Context? = null

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
                            } else {
                                act.startActivity(
                                    Intent(act, ScriptEditActivity::class.java)
                                        .putExtra("name", name)
                                        .putExtra(
                                            "script",
                                            StorageUtils.read(
                                                "KakaoTalkBotHub/Bots/JavaScript/$name.js",
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
                                if (type == 1) {
                                    act.startActivity(
                                        Intent(act, SimpleEditActivity::class.java)
                                            .putExtra("name", name)
                                    )
                                } else {
                                    val state =
                                        KakaoTalkListener.initializeJavaScript(name).toString()
                                    if (state != "true") {
                                        ToastUtils.show(
                                            ctx!!,
                                            state, ToastUtils.LONG, ToastUtils.ERROR
                                        )
                                        viewholder.state.background = ContextCompat.getDrawable(
                                            ctx!!,
                                            R.drawable.reload_error_view
                                        )
                                    } else {
                                        ToastUtils.show(
                                            ctx!!,
                                            ctx!!.getString(R.string.reload_success),
                                            ToastUtils.LONG, ToastUtils.SUCCESS
                                        )
                                        viewholder.state.background = ContextCompat.getDrawable(
                                            ctx!!,
                                            R.drawable.reload_done_view
                                        )
                                    }
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
                            val beforeDeleteItem = list[position]
                            val beforeDeleteIndex = list.indexOf(beforeDeleteItem)

                            if (type == 1) {
                                val path = "Android/data/com.sungbin.autoreply.bot.three/KakaoTalkBotHub/Bots/simple/$name"

                                val simpleItemLabel = arrayOf("type", "room", "reply", "sender", "message")
                                val beforeDeleteValues = HashMap<String, String>()

                                for(label in simpleItemLabel){
                                    Log.d("AAA", label)
                                    Log.d("BBB", SimpleBotUtils.get(name, label))
                                    beforeDeleteValues[label] = SimpleBotUtils.get(name, label)
                                }

                                StorageUtils.deleteAll(path)

                                list.remove(list[position])
                                notifyDataSetChanged()
                                val bar = Snackbar.make(view, "$name 자동응답이이 삭제되었습니다.", 3000)
                                    .setActionTextColor(
                                        ContextCompat.getColor(
                                            ctx!!,
                                            R.color.colorPrimaryDark
                                        )
                                    )
                                    .setAction("되돌리기") {
                                       StorageUtils.createFolder(path)
                                       SimpleBotUtils.save(
                                           name, beforeDeleteValues["sender"]!!,
                                           beforeDeleteValues["room"]!!, beforeDeleteValues["type"]!!,
                                           beforeDeleteValues["message"]!!, beforeDeleteValues["reply"]!!
                                       )
                                       list.add(beforeDeleteIndex, beforeDeleteItem)
                                       notifyDataSetChanged()
                                    }
                                bar.view.setBackgroundColor(
                                    ContextCompat.getColor(
                                        ctx!!,
                                        R.color.colorAccent
                                    )
                                )
                                bar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                                    .typeface = ResourcesCompat.getFont(ctx!!, R.font.nanumgothic)
                                bar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
                                    .typeface = ResourcesCompat.getFont(ctx!!, R.font.nanumgothic)
                                bar.show()
                            }
                            else {
                                val beforeDeleteSource = StorageUtils.read(
                                    "KakaoTalkBotHub/Bots/JavaScript/$name.js",
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

                                StorageUtils.delete("KakaoTalkBotHub/Bots/JavaScript/$name.js")
                                list.remove(list[position])
                                notifyDataSetChanged()
                                val bar = Snackbar.make(view, "$name 스크립트가 삭제되었습니다.", 3000)
                                    .setActionTextColor(
                                        ContextCompat.getColor(
                                            ctx!!,
                                            R.color.colorPrimaryDark
                                        )
                                    )
                                    .setAction("되돌리기") {
                                        StorageUtils.save(
                                            "KakaoTalkBotHub/Bots/JavaScript/$name.js",
                                            beforeDeleteSource
                                        )
                                        list.add(beforeDeleteIndex, beforeDeleteItem)
                                        notifyDataSetChanged()
                                    }
                                bar.view.setBackgroundColor(
                                    ContextCompat.getColor(
                                        ctx!!,
                                        R.color.colorAccent
                                    )
                                )
                                bar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                                    .typeface = ResourcesCompat.getFont(ctx!!, R.font.nanumgothic)
                                bar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
                                    .typeface = ResourcesCompat.getFont(ctx!!, R.font.nanumgothic)
                                bar.show()
                            }
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
