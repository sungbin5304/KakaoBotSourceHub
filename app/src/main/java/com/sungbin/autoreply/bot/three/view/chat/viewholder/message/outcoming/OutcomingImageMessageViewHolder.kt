package com.sungbin.autoreply.bot.three.view.chat.viewholder.message.incoming.outcoming

import android.view.View
import android.widget.TextView
import com.stfalcon.chatkit.messages.MessageHolders.OutcomingImageMessageViewHolder
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.chat.model.Message
import com.sungbin.autoreply.bot.three.utils.chat.FormatUtils

class OutcomingImageMessageViewHolder(
    itemView: View?,
    payload: Any?
) : OutcomingImageMessageViewHolder<Message>(
    itemView,
    payload
) {
    private  val messageDate: TextView = itemView!!.findViewById(R.id.messageDate)
    override fun onBind(message: Message) {
        super.onBind(message)
        messageDate.text = FormatUtils.createDate(message.createdAt)
    }
}