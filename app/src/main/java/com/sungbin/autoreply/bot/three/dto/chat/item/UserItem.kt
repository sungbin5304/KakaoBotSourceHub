package com.sungbin.autoreply.bot.three.dto.chat.item

class UserItem {
    var id: String? = null
    var name: String? = null
    var avatar: String? = null
    var isOnline: Boolean? = null

    constructor(){}
    constructor(id: String?, name: String?, avatar: String?, isOnline: Boolean?) {
        this.id = id
        this.name = name
        this.avatar = avatar
        this.isOnline = isOnline
    }
}