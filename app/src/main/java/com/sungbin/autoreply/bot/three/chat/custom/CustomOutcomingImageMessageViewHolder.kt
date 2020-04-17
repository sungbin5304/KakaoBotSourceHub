package com.sungbin.autoreply.bot.three.chat.custom

import android.util.Pair
import android.view.View
import com.stfalcon.chatkit.messages.MessageHolders.OutcomingImageMessageViewHolder
import com.sungbin.autoreply.bot.three.dto.model.Message

class CustomOutcomingImageMessageViewHolder(
    itemView: View?,
    payload: Any?
) : OutcomingImageMessageViewHolder<Message>(
    itemView,
    payload
) {
    override fun onBind(message: Message) {
        super.onBind(message)
        time.text = message.status + " " + time.text
    }

    //Override this method to have ability to pass custom data in ImageLoader for loading image(not avatar).
    override fun getPayloadForImageLoader(message: Message): Any {
        //For example you can pass size of placeholder before loading
        return Pair(100, 100)
    }
}