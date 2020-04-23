package com.sungbin.autoreply.bot.three.dto.chat.model

import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.MessageContentType
import com.sungbin.autoreply.bot.three.dto.chat.Content
import com.sungbin.autoreply.bot.three.dto.chat.ContentType
import com.sungbin.autoreply.bot.three.dto.chat.MessageType
import java.util.*

class Message(
    private val id: String,
    private val dialogIdString: String,
    private val user: User,
    private val text: String,
    private val createdAt: Date,
    private val messageStatue: Int,
    private val messageContent: Content?
) : IMessage, MessageContentType.Image,
    MessageContentType {

    val status: Int
        get() = messageStatue

    val dialogId: String
        get() = dialogIdString

    val content: Content?
        get() = messageContent

    override fun getId(): String {
        return id
    }

    override fun getText(): String {
        return text
    }

    override fun getCreatedAt(): Date {
        return createdAt
    }

    override fun getUser(): User {
        return user
    }

    override fun getImageUrl(): String? {
        return if(messageContent != null){
            if(messageContent.type == ContentType.IMAGE){
                messageContent.url
            } else null
        } else null
    }
}