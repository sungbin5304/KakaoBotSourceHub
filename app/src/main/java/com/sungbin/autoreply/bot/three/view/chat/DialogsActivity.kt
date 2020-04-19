package com.sungbin.autoreply.bot.three.view.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.commons.models.MessageContentType
import com.stfalcon.chatkit.dialogs.DialogsList
import com.stfalcon.chatkit.dialogs.DialogsListAdapter
import com.stfalcon.chatkit.dialogs.DialogsListAdapter.OnDialogClickListener
import com.stfalcon.chatkit.dialogs.DialogsListAdapter.OnDialogLongClickListener
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.chat.MessageState
import com.sungbin.autoreply.bot.three.dto.chat.item.DialogItem
import com.sungbin.autoreply.bot.three.dto.chat.item.MessageItem
import com.sungbin.autoreply.bot.three.dto.chat.item.UserItem
import com.sungbin.autoreply.bot.three.dto.chat.model.Dialog
import com.sungbin.autoreply.bot.three.dto.chat.model.Message
import com.sungbin.autoreply.bot.three.dto.chat.model.User
import com.sungbin.autoreply.bot.three.utils.chat.ChatModuleUtils
import com.sungbin.autoreply.bot.three.utils.ui.Glide
import com.sungbin.autoreply.bot.three.view.chat.viewholder.dialog.DialogViewHolder
import com.sungbin.sungbintool.ToastUtils
import com.sungbin.sungbintool.Utils
import kotlinx.android.synthetic.main.activity_custom_holder_dialogs.*
import java.util.*

class DialogsActivity : AppCompatActivity(),
    OnDialogClickListener<Dialog?>, OnDialogLongClickListener<Dialog?> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_holder_dialogs)

        supportActionBar!!.title = getString(R.string.string_chatting)

        val reference = FirebaseDatabase.getInstance().reference.child("Chat").child("Dialogs")
            //.child(ChatModuleUtils.getDeviceId(applicationContext))
        val dialogsList = findViewById<DialogsList>(R.id.dialogsList)
        val dialogItems = ArrayList<Dialog>()
        val imageLoader = ImageLoader { imageView: ImageView?, url: String?, _: Any? ->
            Glide.set(applicationContext, url!!, imageView!!)
        }
        val dialogsAdapter: DialogsListAdapter<Dialog?>? = DialogsListAdapter(
            R.layout.item_custom_dialog_view_holder,
            DialogViewHolder::class.java,
            imageLoader
        )

        dialogsAdapter!!.setItems(dialogItems as List<Dialog?>)
        dialogsAdapter.setOnDialogClickListener(this)
        dialogsAdapter.setOnDialogLongClickListener(this)
        dialogsList.setAdapter(dialogsAdapter)

        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                try {
                    val item = dataSnapshot.getValue(DialogItem::class.java)!!
                    val dialogItem = item.lastMessage!!
                    val dialogUser = dialogItem.user!!
                    val dialogUserItem = User(dialogUser.id!!, dialogUser.name!!,
                        dialogUser.avatar!!, dialogUser.isOnline!!)
                    val message = Message(dialogItem.id!!, dialogItem.dialogIdString!!,
                        dialogUserItem, dialogItem.text!!, dialogItem.createdAt!!,
                        dialogItem.messageStatue!!, dialogItem.messageContent)
                    val userList = ArrayList<User>()
                    for(element in item.users!!){
                        userList.add(User(element.id!!, element.name!!,
                            element.avatar!!, element.isOnline!!))
                    }
                    val dialog = Dialog(item.id!!, item.owner!!, item.dialogName!!, item.dialogPhoto!!,
                        userList, message, item.unreadCount!!)
                    ChatModuleUtils.addDialog(dialog)
                    if(!dialogItems.contains(dialog)) dialogItems.add(dialog)
                    dialogsAdapter.notifyDataSetChanged()
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

        fab.setOnClickListener {
            try {
                val randomUuid = ChatModuleUtils.randomUuid
                val deviceId = ChatModuleUtils.getDeviceId(applicationContext)
                val user = UserItem(deviceId, "내 이름",
                    "https://media.discordapp.net/attachments/701073572021534772/701385662233968680/1587293721969.jpg", true)
                val message = MessageItem(randomUuid, randomUuid, user, "테스트", Date(), MessageState.SENT, null)
                val dialog = DialogItem(randomUuid, deviceId, "AAA",
                    "https://media.discordapp.net/attachments/634652333325680643/701372130365014026/09c3438f71cec89abc483b82f9e3a845.png",
                    arrayListOf(user, user, user, user), message, 11)
                reference.child(randomUuid).setValue(dialog)
            }
            catch (e: Exception){
                Utils.error(applicationContext, e, "add dialog")
            }
        }
    }

    override fun onDialogClick(dialog: Dialog?) {
        startActivity(Intent(this, MessagesActivity::class.java)
            .putExtra("dialogId", dialog!!.id))
    }

    override fun onDialogLongClick(dialog: Dialog?) {
        ToastUtils.show(applicationContext, "${dialog!!.dialogName} 롱클릭",
            ToastUtils.SHORT, ToastUtils.INFO)
    }
}