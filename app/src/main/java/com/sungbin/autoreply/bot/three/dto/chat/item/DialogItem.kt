package com.sungbin.autoreply.bot.three.dto.chat.item

import com.sungbin.autoreply.bot.three.dto.chat.MessageType
import com.sungbin.autoreply.bot.three.dto.chat.model.Message
import com.sungbin.autoreply.bot.three.dto.chat.model.User
import java.util.ArrayList

class DialogItem {
    var id: String? = null
    var owner: String? = null
    var dialogName: String? = null
    var dialogPhoto: String? = null
    var users: ArrayList<UserItem>? = null
    var lastMessage: MessageItem? = null
    var unreadCount: Int? = null
    var messageType: Int? = null

    constructor(){}
    constructor(
        id: String?,
        owner: String?,
        dialogName: String?,
        dialogPhoto: String?,
        users: ArrayList<UserItem>?,
        lastMessage: MessageItem?,
        unreadCount: Int?,
        messageType: Int?
    ) {
        this.id = id
        this.owner = owner
        this.dialogName = dialogName
        this.dialogPhoto = dialogPhoto
        this.users = users
        this.lastMessage = lastMessage
        this.unreadCount = unreadCount
        this.messageType = messageType
    }
}