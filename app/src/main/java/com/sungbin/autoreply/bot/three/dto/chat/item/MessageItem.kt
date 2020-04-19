package com.sungbin.autoreply.bot.three.dto.chat.item

import com.sungbin.autoreply.bot.three.dto.chat.Content
import java.util.*

class MessageItem {
    var id: String? = null
    var dialogIdString: String? = null
    var user: UserItem? = null
    var text: String? = null
    var createdAt: Date? = null
    var messageStatue: Int? = null
    var messageContent: Content? = null

    constructor(){}
    constructor(
        id: String?,
        dialogIdString: String?,
        user: UserItem?,
        text: String?,
        createdAt: Date?,
        messageStatue: Int?,
        messageContent: Content?
    ) {
        this.id = id
        this.dialogIdString = dialogIdString
        this.user = user
        this.text = text
        this.createdAt = createdAt
        this.messageStatue = messageStatue
        this.messageContent = messageContent
    }
}