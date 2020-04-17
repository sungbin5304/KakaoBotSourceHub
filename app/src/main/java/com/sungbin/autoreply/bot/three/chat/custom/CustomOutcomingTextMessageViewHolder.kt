package com.sungbin.autoreply.bot.three.chat.custom

import android.view.View
import com.stfalcon.chatkit.messages.MessageHolders.OutcomingTextMessageViewHolder
import com.sungbin.autoreply.bot.three.dto.model.Message

class CustomOutcomingTextMessageViewHolder(
    itemView: View?,
    payload: Any?
) : OutcomingTextMessageViewHolder<Message>(
    itemView,
    payload
) {
    override fun onBind(message: Message) {
        super.onBind(message)
        time.text = message.status + " " + time.text
    }
}