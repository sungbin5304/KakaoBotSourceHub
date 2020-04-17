package com.sungbin.autoreply.bot.three.chat.custom

import android.view.View
import com.stfalcon.chatkit.messages.MessageHolders.IncomingImageMessageViewHolder
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.model.Message

class CustomIncomingImageMessageViewHolder(
    itemView: View,
    payload: Any?
) : IncomingImageMessageViewHolder<Message>(
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
    }

    init {
        onlineIndicator = itemView.findViewById(R.id.onlineIndicator)
    }
}