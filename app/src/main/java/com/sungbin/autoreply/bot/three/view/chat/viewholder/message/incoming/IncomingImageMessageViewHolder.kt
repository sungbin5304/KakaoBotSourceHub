package com.sungbin.autoreply.bot.three.view.chat.viewholder.message.incoming

import android.view.View
import com.stfalcon.chatkit.messages.MessageHolders.IncomingImageMessageViewHolder
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.chat.model.Message

class IncomingImageMessageViewHolder(
    itemView: View,
    payload: Any?
) : IncomingImageMessageViewHolder<Message>(
    itemView,
    payload
) {
    private val onlineIndicator: View = itemView.findViewById(R.id.onlineIndicator)
    override fun onBind(message: Message) {
        super.onBind(message)
        val isOnline = true
        if (isOnline) {
            onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_online)
        } else {
            onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_offline)
        }
    }

}