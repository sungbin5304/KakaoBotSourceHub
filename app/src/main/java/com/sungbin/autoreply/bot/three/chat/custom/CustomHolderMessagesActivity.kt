package com.sungbin.autoreply.bot.three.chat.custom

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessageInput.AttachmentsListener
import com.stfalcon.chatkit.messages.MessageInput.InputListener
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter
import com.stfalcon.chatkit.messages.MessagesListAdapter.OnMessageLongClickListener
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.chat.custom.AppUtils.showToast
import com.sungbin.autoreply.bot.three.chat.custom.CustomHolderMessagesActivity
import com.sungbin.autoreply.bot.three.chat.custom.CustomIncomingTextMessageViewHolder.OnAvatarClickListener
import com.sungbin.autoreply.bot.three.chat.custom.CustomIncomingTextMessageViewHolder.Payload
import com.sungbin.autoreply.bot.three.dto.model.Message
import com.sungbin.autoreply.bot.three.fixtures.MessagesFixtures

class CustomHolderMessagesActivity : DemoMessagesActivity(),
    OnMessageLongClickListener<Message?>, InputListener, AttachmentsListener {
    private var messagesList: MessagesList? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_holder_messages)
        messagesList = findViewById<View>(R.id.messagesList) as MessagesList
        initAdapter()
        val input = findViewById<View>(R.id.input) as MessageInput
        input.setInputListener(this)
        input.setAttachmentsListener(this)
    }

    override fun onSubmit(input: CharSequence): Boolean {
        messagesAdapter!!.addToStart(
            MessagesFixtures.getTextMessage(input.toString()), true
        )
        return true
    }

    override fun onAddAttachments() {
        messagesAdapter!!.addToStart(MessagesFixtures.imageMessage, true)
    }

    override fun onMessageLongClick(message: Message?) {
        showToast(this, "메시지 롱클릭", false)
    }

    private fun initAdapter() {

        //We can pass any data to ViewHolder with payload
        val payload = Payload()
        //For example click listener
        payload.avatarClickListener = object : OnAvatarClickListener {
            override fun onAvatarClick() {
                Toast.makeText(
                    this@CustomHolderMessagesActivity,
                    "Text message avatar clicked", Toast.LENGTH_SHORT
                ).show()
            }
        }
        val holdersConfig = MessageHolders()
            .setIncomingTextConfig(
                CustomIncomingTextMessageViewHolder::class.java,
                R.layout.item_custom_incoming_text_message,
                payload
            )
            .setOutcomingTextConfig(
                CustomOutcomingTextMessageViewHolder::class.java,
                R.layout.item_custom_outcoming_text_message
            )
            .setIncomingImageConfig(
                CustomIncomingImageMessageViewHolder::class.java,
                R.layout.item_custom_incoming_image_message
            )
            .setOutcomingImageConfig(
                CustomOutcomingImageMessageViewHolder::class.java,
                R.layout.item_custom_outcoming_image_message
            )
        super.messagesAdapter =
            MessagesListAdapter(
                super.senderId,
                holdersConfig,
                super.imageLoader
            )
        super.messagesAdapter!!.setOnMessageLongClickListener(this)
        super.messagesAdapter!!.setLoadMoreListener(this)
        messagesList!!.setAdapter(super.messagesAdapter)
    }

    companion object {
        fun open(context: Context) {
            context.startActivity(Intent(context, CustomHolderMessagesActivity::class.java))
        }
    }
}