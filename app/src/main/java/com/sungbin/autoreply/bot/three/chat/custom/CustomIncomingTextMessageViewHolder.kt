package com.sungbin.autoreply.bot.three.chat.custom

import android.view.View
import com.stfalcon.chatkit.messages.MessageHolders.IncomingTextMessageViewHolder
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.model.Message

class CustomIncomingTextMessageViewHolder(
    itemView: View,
    payload: Any?
) : IncomingTextMessageViewHolder<Message>(
    itemView,
    payload
) {
    private val onlineIndicator: View
    override fun onBind(message: Message) {
        super.onBind(message)
        val isOnline = message.user.isOnline
        if (isOnline) {
            onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_online)
        } else {
            onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_offline)
        }

        //We can set click listener on view from payload
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

    init {
        onlineIndicator = itemView.findViewById(R.id.onlineIndicator)
    }
}