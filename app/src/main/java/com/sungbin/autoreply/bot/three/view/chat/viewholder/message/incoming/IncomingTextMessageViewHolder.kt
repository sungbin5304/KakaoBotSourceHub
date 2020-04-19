package com.sungbin.autoreply.bot.three.view.chat.viewholder.message.incoming

import android.view.View
import android.widget.TextView
import com.stfalcon.chatkit.messages.MessageHolders.IncomingTextMessageViewHolder
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.chat.model.Message
import com.sungbin.sungbintool.ToastUtils

class IncomingTextMessageViewHolder(
    itemView: View,
    payload: Any?
) : IncomingTextMessageViewHolder<Message>(
    itemView,
    payload
) {
    private val onlineIndicator: View = itemView.findViewById(R.id.onlineIndicator)
    private val messageUser: TextView = itemView.findViewById(R.id.messageName)
    override fun onBind(message: Message) {
        super.onBind(message)
        val isOnline = true
        val userName = message.user.name
        if (isOnline) {
            onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_online)
        } else {
            onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_offline)
        }
        messageUser.text = userName
        val payload = payload as Payload
        userAvatar.setOnClickListener {
            if (payload.avatarClickListener != null) {
                payload.avatarClickListener!!.onAvatarClick()
            }
        }
    }

    class Payload {
        var avatarClickListener: OnAvatarClickListener? = null
    }

    interface OnAvatarClickListener {
        fun onAvatarClick()
    }

}