package com.sungbin.autoreply.bot.three.view.chat

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.adapter.DialogViewPagerAdapter
import com.sungbin.autoreply.bot.three.dto.chat.MessageState
import com.sungbin.autoreply.bot.three.dto.chat.MessageType
import com.sungbin.autoreply.bot.three.dto.chat.item.DialogItem
import com.sungbin.autoreply.bot.three.dto.chat.item.MessageItem
import com.sungbin.autoreply.bot.three.dto.chat.model.Dialog
import com.sungbin.autoreply.bot.three.dto.chat.model.Message
import com.sungbin.autoreply.bot.three.dto.chat.model.User
import com.sungbin.autoreply.bot.three.utils.ui.ImageUtils
import com.sungbin.autoreply.bot.three.utils.chat.ChatModuleUtils
import com.sungbin.autoreply.bot.three.utils.ui.Glide
import com.sungbin.sungbintool.ToastUtils
import com.sungbin.sungbintool.Utils
import de.hdodenhof.circleimageview.CircleImageView
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.MediaType
import kotlinx.android.synthetic.main.activity_custom_holder_dialogs.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


@Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER")
class DialogsActivity : AppCompatActivity() {

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_holder_dialogs)

        val reference = FirebaseDatabase.getInstance().reference.child("Chat").child("Dialogs")
            .child(ChatModuleUtils.getDeviceId(applicationContext))

        var isClickMessageType = 0
        val myUserData = ChatModuleUtils.getUser(ChatModuleUtils.getDeviceId(applicationContext))
        val chatItems = ArrayList<Dialog>()
        val openItems = ArrayList<Dialog>()
        val act = this

        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                try {
                    val item = dataSnapshot.getValue(DialogItem::class.java)!!
                    val dialogItem = item.lastMessage!!
                    val dialogUser = dialogItem.user!!
                    val dialogUserItem = User(dialogUser.id!!, dialogUser.name!!,
                        dialogUser.avatar!!, dialogUser.isOnline!!,
                        dialogUser.roomList, dialogUser.friendsList)
                    val message = Message(dialogItem.id!!, dialogItem.dialogIdString!!,
                        dialogUserItem, dialogItem.text!!, dialogItem.createdAt!!,
                        dialogItem.messageStatue!!, dialogItem.messageContent)
                    val userList = ArrayList<User>()
                    for(element in item.users!!){
                        userList.add(User(element.id!!, element.name!!,
                            element.avatar!!, element.isOnline!!,
                            element.roomList, element.friendsList))
                    }
                    val dialog = Dialog(item.id!!, item.owner!!, item.dialogName!!, item.dialogPhoto!!,
                        userList, message, item.unreadCount!!, item.messageType!!)
                    ChatModuleUtils.addDialog(dialog)

                    when(item.messageType){
                        MessageType.NORMAL -> {
                            if(!chatItems.contains(dialog)) chatItems.add(dialog)
                        }
                        else -> {
                            if(!openItems.contains(dialog)) openItems.add(dialog)
                        }
                    }

                    val viewPagerAdapter = DialogViewPagerAdapter(act, chatItems, openItems, fab)
                    viewPagerAdapter.notifyDataSetChanged()
                    viewPagerAdapter.refresh()
                    view_pager.adapter = viewPagerAdapter
                    tab.setViewPager(view_pager)
                }
                catch (e: Exception) {
                    Utils.error(applicationContext, e, "init dialogs")
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        val viewPagerAdapter = DialogViewPagerAdapter(act, chatItems, openItems, fab)
        viewPagerAdapter.notifyDataSetChanged()
        viewPagerAdapter.refresh()
        view_pager.adapter = viewPagerAdapter
        tab.setViewPager(view_pager)

        fab.setOnClickListener {
            try {
                var imageUrl = ""
                val inflater = LayoutInflater.from(applicationContext)
                    .inflate(R.layout.layout_message_add, null, false)
                val view = inflater.findViewById<LinearLayout>(R.id.layout)
                val imageView = view.findViewById<CircleImageView>(R.id.iv_icon)
                val done = view.findViewById<Button>(R.id.btn_add)
                val input = view.findViewById<EditText>(R.id.et_name)
                val rgLayout = view.findViewById<RadioGroup>(R.id.rg_layout)

                val alert = BottomSheetDialog(this)
                alert.setContentView(view)

                ImageUtils.set(myUserData!!.avatar,
                    imageView, applicationContext)
                imageView.setOnClickListener {
                    TedImagePicker.with(this)
                        .mediaType(MediaType.IMAGE)
                        .start { uri ->
                            imageUrl = uri.toString()
                            Glide.set(applicationContext, imageUrl, imageView)
                        }
                }

                rgLayout.setOnCheckedChangeListener { _, i ->
                    isClickMessageType = when(i){
                        R.id.rb_group -> MessageType.NORMAL
                        else -> MessageType.OPEN
                    }
                }

                done.setOnClickListener {
                    val randomUuid = ChatModuleUtils.randomUuid
                    val deviceId = ChatModuleUtils.getDeviceId(applicationContext)
                    val message = MessageItem(randomUuid, randomUuid,
                        ChatModuleUtils.createUserItem(myUserData), "채팅방이 생성되었습니다.",
                        Date(), MessageState.SENT, null)
                    if(imageUrl == ""){
                        val dialog = DialogItem(randomUuid, deviceId,
                            input.text.toString(), myUserData.avatar,
                            arrayListOf(ChatModuleUtils.createUserItem(myUserData)),
                            message, 0, isClickMessageType)
                        reference.child(randomUuid).setValue(dialog)
                        alert.cancel()
                    }
                    else {
                        val link = if(imageUrl.startsWith("file://")) {
                            imageUrl.replaceFirst("file://", "")
                        } else imageUrl
                        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                        pDialog.progressHelper.barColor = ContextCompat.getColor(applicationContext,
                            R.color.colorAccent)
                        pDialog.titleText = getString(R.string.uploading_image)
                        pDialog.setCancelable(false)
                        pDialog.show()

                        val file = Uri.fromFile(File(link))
                        val storageRef = FirebaseStorage.getInstance().reference
                        var prefix = "jpg"
                        if(link.toLowerCase(Locale.getDefault()).contains(".gif")) prefix = "gif"
                        val riversRef = storageRef.child("Dialog/$randomUuid/cover.$prefix")
                        val uploadTask = riversRef.putFile(file)
                        uploadTask.addOnFailureListener {
                            ToastUtils.show(this, "방 사진 업로드에 실패했습니다.\n\n${it}",
                                ToastUtils.SHORT, ToastUtils.ERROR)
                            pDialog.cancel()
                            alert.cancel()
                        }.addOnSuccessListener {
                            riversRef.downloadUrl.addOnSuccessListener {
                                val dialog = DialogItem(randomUuid, deviceId,
                                    input.text.toString(), it.toString(),
                                    arrayListOf(ChatModuleUtils.createUserItem(myUserData)),
                                    message, 0, isClickMessageType)
                                reference.child(randomUuid).setValue(dialog)
                                pDialog.cancel()
                                alert.cancel()
                            }
                            riversRef.downloadUrl.addOnFailureListener {
                                ToastUtils.show(this, "방 사진 다운로드 링크 추출에 실패했습니다.\n\n${it}",
                                    ToastUtils.SHORT, ToastUtils.ERROR)
                                pDialog.cancel()
                                alert.cancel()
                            }
                        }
                    }
                }

                alert.show()
            }
            catch (e: Exception){
                Log.d("EEEEEE", e.toString())
                //Utils.error(applicationContext, e, "add dialog")
            }
        }
    }
}