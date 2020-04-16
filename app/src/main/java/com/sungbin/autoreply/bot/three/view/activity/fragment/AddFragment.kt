package com.sungbin.autoreply.bot.three.view.activity.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import belka.us.androidtoggleswitch.widgets.ToggleSwitch
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.view.activity.DashboardActivity
import com.sungbin.sungbintool.StorageUtils
import com.sungbin.sungbintool.ToastUtils
import me.ibrahimsn.lib.SmoothBottomBar

class AddFragment
    constructor(fragmentManage: FragmentManager, v: Int,
                bar: SmoothBottomBar) : Fragment() {

    val bottombar = bar
    val fragmentManagering = fragmentManage
    val view = v
    var botTypeTs: ToggleSwitch? = null
    var inputBotNameEt: EditText? = null
    var addBotBtn: Button? = null
    var isJsBot = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_add, container, false)
        botTypeTs = view.findViewById(R.id.ts_bot_type)
        inputBotNameEt = view.findViewById(R.id.et_input_bot_name)
        addBotBtn = view.findViewById(R.id.btn_bot_add)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        botTypeTs!!.setOnToggleSwitchChangeListener { position, _ ->
            isJsBot = position != 0
        }
        addBotBtn!!.setOnClickListener {
            val botNameText = inputBotNameEt!!.text.toString()

            if(botNameText.isEmpty()){
                ToastUtils.show(context!!,
                    getString(R.string.please_input_bot_name),
                    ToastUtils.SHORT, ToastUtils.WARNING)
                return@setOnClickListener
            }
            else {
                val fragmentTransaction = fragmentManagering.beginTransaction()
                fragmentTransaction.replace(view, DashboardFragment()).commit()
                bottombar.setActiveItem(0)
                if(isJsBot){
                    StorageUtils.save("AutoReply Bot/Bots/JavaScript/$botNameText.js",
                    """
                        function response(room, msg, sender, isGroupChat, replier, ImageDB, packageName) {
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
                }
                else {
                    StorageUtils.save("AutoReply Bot/Bots/AutoReply/$botNameText.bot", "")
                }
                ToastUtils.show(
                    context!!,
                    getString(R.string.add_new_bot),
                    ToastUtils.SHORT, ToastUtils.SUCCESS
                )
            }
        }
    }
}
