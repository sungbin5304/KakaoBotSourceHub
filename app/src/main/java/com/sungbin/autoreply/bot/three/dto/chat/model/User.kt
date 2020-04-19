package com.sungbin.autoreply.bot.three.dto.chat.model

import com.stfalcon.chatkit.commons.models.IUser

class User(
    private val id: String,
    private val name: String,
    private val avatar: String,
    private val isOnline: Boolean
) : IUser {

    val online: Boolean
        get() = isOnline

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