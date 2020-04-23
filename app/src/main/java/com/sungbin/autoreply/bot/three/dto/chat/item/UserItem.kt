package com.sungbin.autoreply.bot.three.dto.chat.item

class UserItem {
    var id: String? = null
    var name: String? = null
    var avatar: String? = null
    var isOnline: Boolean? = null
    var roomList: ArrayList<String>? = null
    var friendsList: ArrayList<String>? = null

    constructor(){}
    constructor(
        id: String?,
        name: String?,
        avatar: String?,
        isOnline: Boolean?,
        roomList: ArrayList<String>?,
        friendsList: ArrayList<String>?
    ) {
        this.id = id
        this.name = name
        this.avatar = avatar
        this.isOnline = isOnline
        this.roomList = roomList
        this.friendsList = friendsList
    }

}