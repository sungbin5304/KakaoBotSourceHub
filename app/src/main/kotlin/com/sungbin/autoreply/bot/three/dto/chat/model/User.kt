package com.sungbin.autoreply.bot.three.dto.chat.model

import com.stfalcon.chatkit.commons.models.IUser

class User(
    private val id: String,
    private val name: String,
    private val avatar: String,
    private val isOnline: Boolean,
    private val roomList: ArrayList<String>?,
    private val friendsList: ArrayList<String>?
) : IUser {

    val online: Boolean
        get() = isOnline

    val friends: ArrayList<String>?
        get() = friendsList

    val rooms: ArrayList<String>?
        get() = roomList

    override fun getId(): String {
        return id
    }

    override fun getName(): String {
        return name
    }

    override fun getAvatar(): String {
        return avatar
    }

}