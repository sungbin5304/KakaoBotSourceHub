package com.sungbin.autoreply.bot.three.view.chat

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.commons.models.MessageContentType
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessageInput.TypingListener
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter
import com.stfalcon.chatkit.utils.DateFormatter
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.chat.Content
import com.sungbin.autoreply.bot.three.dto.chat.ContentType
import com.sungbin.autoreply.bot.three.dto.chat.MessageState
import com.sungbin.autoreply.bot.three.dto.chat.item.MessageItem
import com.sungbin.autoreply.bot.three.dto.chat.item.UserItem
import com.sungbin.autoreply.bot.three.dto.chat.model.Dialog
import com.sungbin.autoreply.bot.three.dto.chat.model.Message
import com.sungbin.autoreply.bot.three.dto.chat.model.User
import com.sungbin.autoreply.bot.three.utils.chat.ChatModuleUtils
import com.sungbin.autoreply.bot.three.view.chat.viewholder.message.incoming.IncomingImageMessageViewHolder
import com.sungbin.autoreply.bot.three.view.chat.viewholder.message.incoming.IncomingTextMessageViewHolder
import com.sungbin.autoreply.bot.three.view.chat.viewholder.message.incoming.outcoming.OutcomingImageMessageViewHolder
import com.sungbin.autoreply.bot.three.view.chat.viewholder.message.incoming.outcoming.OutcomingTextMessageViewHolder
import com.sungbin.sungbintool.ToastUtils
import com.sungbin.sungbintool.Utils
import java.text.SimpleDateFormat
import java.util.*


class MessagesActivity : AppCompatActivity(),
    MessagesListAdapter.SelectionListener, MessagesListAdapter.OnLoadMoreListener,
    MessagesListAdapter.OnMessageLongClickListener<Message?>, MessageInput.InputListener,
    MessageInput.AttachmentsListener, DateFormatter.Formatter {

    private val TOTAL_MESSAGES_COUNT = 100
    private var reference = FirebaseDatabase.getInstance().reference
    private var messagesAdapter: MessagesListAdapter<Message?>? = null
    private var menu: Menu? = null
    private var selectionCount = 0
    private var lastLoadedDate: Date? = null
    private var dialog: Dialog? = null
    private var deviceId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_holder_messages)

        deviceId = ChatModuleUtils.getDeviceId(applicationContext)
        dialog = ChatModuleUtils.getDialog(intent.getStringExtra("dialogId")!!)

        supportActionBar!!.title = dialog!!.dialogName

        reference = reference.child("Chat").child("Messages").child(dialog!!.id)
        val messageItems = ArrayList<Message>()
        val messagesList = findViewById<MessagesList>(R.id.messagesList)
        val input = findViewById<MessageInput>(R.id.input)
        val payload = IncomingTextMessageViewHolder.Payload()
        val imageLoader = ImageLoader { imageView: ImageView?, url: String?, _: Any? ->
                Glide.with(this@MessagesActivity).load(url).into(imageView!!)
            }
        val holdersConfig = MessageHolders()
            .setIncomingTextConfig(
                IncomingTextMessageViewHolder::class.java,
                R.layout.item_custom_incoming_text_message,
                payload
            )
            .setOutcomingTextConfig(
                OutcomingTextMessageViewHolder::class.java,
                R.layout.item_custom_outcoming_text_message
            )
            .setIncomingImageConfig(
                IncomingImageMessageViewHolder::class.java,
                R.layout.item_custom_incoming_content_message
            )
            .setOutcomingImageConfig(
                OutcomingImageMessageViewHolder::class.java,
                R.layout.item_custom_outcoming_content_message
            )

        input.setInputListener(this)
        input.setAttachmentsListener(this)
        input.setTypingListener(object : TypingListener {
            override fun onStartTyping() {
                /*ToastUtils.show(applicationContext, "메시지 입력 시작",
                    ToastUtils.SHORT, ToastUtils.INFO)*/
            }
            override fun onStopTyping() {
                /*ToastUtils.show(applicationContext, "메시지 입력 종료",
                    ToastUtils.SHORT, ToastUtils.INFO)*/
            }
        })

        payload.avatarClickListener = object : IncomingTextMessageViewHolder.OnAvatarClickListener {
            override fun onAvatarClick() {
                ToastUtils.show(applicationContext, "Text message avatar clicked",
                    ToastUtils.SHORT, ToastUtils.SUCCESS)
            }
        }

        messagesAdapter =
            MessagesListAdapter(
                ChatModuleUtils.getDeviceId(applicationContext),
                holdersConfig,
                imageLoader
            )

        messagesAdapter!!.setDateHeadersFormatter(this)
        messagesAdapter!!.setOnMessageLongClickListener(this)
        messagesAdapter!!.setLoadMoreListener(this)
        messagesList!!.setAdapter(messagesAdapter)

        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                try {
                    val item = dataSnapshot.getValue(MessageItem::class.java)!!
                    val messageUser = item.user!!
                    val user = User(messageUser.id!!, messageUser.name!!, messageUser.avatar!!,
                        messageUser.isOnline!!)
                    val message = Message(item.id!!, item.dialogIdString!!, user, item.text!!,
                        item.createdAt!!, item.messageStatue!!, item.messageContent)
                    if(!messageItems.contains(message)) {
                        messageItems.add(message)
                        messagesAdapter!!.addToStart(message, true)
                    }
                    messagesAdapter!!.notifyDataSetChanged()
                    Log.d("AAA", message.text)
                }
                catch (e: Exception) {
                    Utils.error(applicationContext, e, "init messages")
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
    }

    override fun onSubmit(input: CharSequence): Boolean {
        val myUserData = ChatModuleUtils.getUser(deviceId!!)!!
        val user = UserItem(deviceId!!, myUserData.name,
            myUserData.avatar, true)
        val messageId = ChatModuleUtils.randomUuid
        val item = MessageItem(messageId, dialog!!.id,
            user, input.toString(), Date(), MessageState.SENT, null)
        reference.child(messageId).setValue(item)
        return true
    }

    override fun onAddAttachments() {
        val myUserData = ChatModuleUtils.getUser(deviceId!!)!!
        val user = UserItem(deviceId!!, myUserData.name,
            myUserData.avatar, true)
        val messageId = ChatModuleUtils.randomUuid
        val item = MessageItem(messageId, dialog!!.id,
            user, "이미지 첨부됨!", Date(), MessageState.SENT, Content("https://cdn.pixabay.com/photo/2020/04/07/17/01/chicks-5014152_960_720.jpg", ContentType.IMAGE))
        reference.child(messageId).setValue(item)
    }

    override fun onMessageLongClick(message: Message?) {
        ToastUtils.show(applicationContext, "${message!!.text} 클릭됨!",
            ToastUtils.SHORT, ToastUtils.SUCCESS)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.chat_actions_menu, menu)
        onSelectionChanged(0)
        return true
    }

    override fun format(date: Date?): String? {
        return when {
            DateFormatter.isToday(date) -> {
                getString(R.string.date_header_today)
            }
            DateFormatter.isYesterday(date) -> {
                getString(R.string.date_header_yesterday)
            }
            else -> {
                DateFormatter.format(
                    date,
                    "yyyy년 M월 d일 E요일"
                )
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> messagesAdapter!!.deleteSelectedMessages()
            R.id.action_copy -> messagesAdapter!!.copySelectedMessagesText(
                this,
                messageStringFormatter,
                true
            )
        }
        return true
    }

    override fun onBackPressed() {
        if (selectionCount == 0) {
            super.onBackPressed()
        } else {
            messagesAdapter!!.unselectAllItems()
        }
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int) {
        if (totalItemsCount < TOTAL_MESSAGES_COUNT) {
            //loadMessages()
        }
    }

    override fun onSelectionChanged(count: Int) {
        selectionCount = count
        menu!!.findItem(R.id.action_delete).isVisible = count > 0
        menu!!.findItem(R.id.action_copy).isVisible = count > 0
    }

    private val messageStringFormatter: MessagesListAdapter.Formatter<Message?>
        get() = MessagesListAdapter.Formatter { message: Message? ->
            val createdAt =
                SimpleDateFormat("MMM D일, E요일 a h:m", Locale.KOREA).format(message!!.createdAt)
            val text = message.text
            String.format(
                Locale.getDefault(), "[%s] %s: %s",
                createdAt, message.user.name, text
            )
        }
}