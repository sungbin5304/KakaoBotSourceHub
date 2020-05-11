package com.sungbin.autoreply.bot.three.view.bot.activity

import android.animation.Animator
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.utils.bot.SimpleBotUtils
import com.sungbin.sungbintool.StorageUtils
import com.sungbin.sungbintool.ToastUtils
import kotlinx.android.synthetic.main.activity_script_edit.*
import kotlinx.android.synthetic.main.content_simple_edit.*
import java.lang.Exception


/**
 * Created by SungBin on 2020-05-09.
 */

class SimpleEditActivity : AppCompatActivity() {

    private var isMessageExpand = false
    private var isRoomExpand = false
    private var isSenderExpand = false
    private var isGroupExpand = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_edit)

        val scriptName = intent.getStringExtra("name")!!
        toolbar_title.text = scriptName

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        if(SimpleBotUtils.get(scriptName, "reply").isNotBlank()) {
            btn_setting_done!!.backgroundTintList = ContextCompat.getColorStateList(
                applicationContext,
                R.color.colorPrimary
            )
            btn_setting_done!!.isEnabled = true
        }

        et_room.text = SpannableStringBuilder(SimpleBotUtils.get(scriptName, "room"))
        et_reply.text = SpannableStringBuilder(SimpleBotUtils.get(scriptName, "reply"))
        et_sender.text = SpannableStringBuilder(SimpleBotUtils.get(scriptName, "sender"))
        et_message.text = SpannableStringBuilder(SimpleBotUtils.get(scriptName, "message"))

        if(SimpleBotUtils.get(scriptName, "type") != "null" &&
            SimpleBotUtils.get(scriptName, "type") != ""){
            if(SimpleBotUtils.get(scriptName, "type").toBoolean()){
                tbl_group.setToggled(R.id.solo_chat, true)
            }
            else {
                tbl_group.setToggled(R.id.open_chat, true)
            }
        }

        et_room.visibility = if(et_room.text.isNotBlank()) {
            iv_room_show.rotation = 180f
            isRoomExpand = true
            View.VISIBLE
        } else {
            View.GONE
        }

        et_sender.visibility = if(et_sender.text.isNotBlank()) {
            iv_sender_show.rotation = 180f
            isSenderExpand = true
            View.VISIBLE
        } else {
            View.GONE
        }

        et_message.visibility = if(et_message.text.isNotBlank()) {
            iv_message_show.rotation = 180f
            isMessageExpand = true
            View.VISIBLE
        } else {
            View.GONE
        }

        tbl_group.visibility = if(SimpleBotUtils.get(scriptName, "type") != "null" &&
            SimpleBotUtils.get(scriptName, "type") != ""){
            iv_group_show.rotation = 180f
            isGroupExpand = true
            View.VISIBLE
        } else {
            View.GONE
        }

        et_reply.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                if(editable.toString().isNotBlank()){
                    btn_setting_done!!.backgroundTintList = ContextCompat.getColorStateList(
                        applicationContext,
                        R.color.colorPrimary
                    )
                    btn_setting_done!!.isEnabled = true
                }
                else {
                    btn_setting_done!!.backgroundTintList = ContextCompat.getColorStateList(
                        applicationContext,
                        R.color.colorAccent
                    )
                    btn_setting_done!!.isEnabled = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        btn_setting_done.setOnClickListener {
            try {
                val reply = et_reply.text.toString()
                val message = et_message.text.toString()
                val room = et_room.text.toString()
                val sender = et_sender.text.toString()
                val group = if (tbl_group.selectedToggles().isEmpty()) {
                    "null"
                } else {
                    (tbl_group.selectedToggles()[0].id == R.id.open_chat).toString()
                }

                SimpleBotUtils.save(scriptName, sender, room, group, message, reply)
                ToastUtils.show(
                    applicationContext,
                    getString(R.string.save_success),
                    ToastUtils.SHORT,
                    ToastUtils.SUCCESS
                )
            }
            catch (e: Exception){
                StorageUtils.save("AAAAA.txt", e.toString())
            }
        }

        tv_message.setOnClickListener {
            iv_message_show.performClick()
        }

        tv_room.setOnClickListener {
            iv_room_show.performClick()
        }

        tv_sender.setOnClickListener {
            iv_sender_show.performClick()
        }

        tv_group.setOnClickListener {
            iv_group_show.performClick()
        }

        val rotate180 = RotateAnimation(
            -180f,
            0f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate180.duration = 250
        rotate180.interpolator = LinearInterpolator()

        val rotate360 = RotateAnimation(
            180f,
            0f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate360.duration = 250
        rotate360.interpolator = LinearInterpolator()

        iv_message_show.setOnClickListener {
            if(!isMessageExpand){
                iv_message_show.startAnimation(rotate180)
                iv_message_show.rotation = 180f
                et_message.visibility = View.VISIBLE
                isMessageExpand = true

                YoYo.with(Techniques.FadeInDown)
                    .duration(250)
                    .playOn(et_message)
            }
            else {
                iv_message_show.startAnimation(rotate360)
                iv_message_show.rotation = 360f
                isMessageExpand = false

                YoYo.with(Techniques.FadeOutUp)
                    .withListener(object : Animator.AnimatorListener{
                        override fun onAnimationRepeat(p0: Animator?) {
                        }

                        override fun onAnimationEnd(p0: Animator?) {
                            et_message.visibility = View.GONE
                        }

                        override fun onAnimationCancel(p0: Animator?) {
                        }

                        override fun onAnimationStart(p0: Animator?) {
                        }
                    })
                    .duration(250)
                    .playOn(et_message)
            }
        }

        iv_room_show.setOnClickListener {
            if(!isRoomExpand){
                iv_room_show.startAnimation(rotate180)
                iv_room_show.rotation = 180f
                et_room.visibility = View.VISIBLE
                isRoomExpand = true

                YoYo.with(Techniques.FadeInDown)
                    .duration(250)
                    .playOn(et_room)
            }
            else {
                iv_room_show.startAnimation(rotate360)
                iv_room_show.rotation = 360f
                isRoomExpand = false

                YoYo.with(Techniques.FadeOutUp)
                    .withListener(object : Animator.AnimatorListener{
                        override fun onAnimationRepeat(p0: Animator?) {
                        }

                        override fun onAnimationEnd(p0: Animator?) {
                            et_room.visibility = View.GONE
                        }

                        override fun onAnimationCancel(p0: Animator?) {
                        }

                        override fun onAnimationStart(p0: Animator?) {
                        }
                    })
                    .duration(250)
                    .playOn(et_room)
            }
        }

        iv_sender_show.setOnClickListener {
            if(!isSenderExpand){
                iv_sender_show.startAnimation(rotate180)
                iv_sender_show.rotation = 180f
                et_sender.visibility = View.VISIBLE
                isSenderExpand = true

                YoYo.with(Techniques.FadeInDown)
                    .duration(250)
                    .playOn(et_sender)
            }
            else {
                iv_sender_show.startAnimation(rotate360)
                iv_sender_show.rotation = 360f
                isSenderExpand = false

                YoYo.with(Techniques.FadeOutUp)
                    .withListener(object : Animator.AnimatorListener{
                        override fun onAnimationRepeat(p0: Animator?) {
                        }

                        override fun onAnimationEnd(p0: Animator?) {
                            et_sender.visibility = View.GONE
                        }

                        override fun onAnimationCancel(p0: Animator?) {
                        }

                        override fun onAnimationStart(p0: Animator?) {
                        }
                    })
                    .duration(250)
                    .playOn(et_sender)
            }
        }

        iv_group_show.setOnClickListener {
            if(!isGroupExpand){
                iv_group_show.startAnimation(rotate180)
                iv_group_show.rotation = 180f
                tbl_group.visibility = View.VISIBLE
                isGroupExpand = true

                YoYo.with(Techniques.FadeInDown)
                    .duration(250)
                    .playOn(tbl_group)
            }
            else {
                iv_group_show.startAnimation(rotate360)
                iv_group_show.rotation = 360f
                isGroupExpand = false

                YoYo.with(Techniques.FadeOutUp)
                    .withListener(object : Animator.AnimatorListener{
                        override fun onAnimationRepeat(p0: Animator?) {
                        }

                        override fun onAnimationEnd(p0: Animator?) {
                            tbl_group.visibility = View.GONE
                        }

                        override fun onAnimationCancel(p0: Animator?) {
                        }

                        override fun onAnimationStart(p0: Animator?) {
                        }
                    })
                    .duration(250)
                    .playOn(tbl_group)
            }
        }

    }
}