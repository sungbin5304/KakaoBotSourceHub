package com.sungbin.autoreply.bot.three.dto.chat

class Content{
    var url: String? = null
    var type: Int? = null

    constructor(){}
    constructor(url: String?, type: Int?) {
        this.url = url
        this.type = type
    }
}