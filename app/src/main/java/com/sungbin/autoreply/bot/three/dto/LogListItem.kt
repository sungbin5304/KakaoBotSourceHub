package com.sungbin.autoreply.bot.three.dto

class LogListItem  {
    var name: String? = null
    var time: String? = null
    var content: String? = null
    var type: String? = null

    constructor() {}
    constructor(name: String?, time: String?, content: String?, type: String?) {
        this.name = name
        this.time = time
        this.content = content
        this.type = type
    }
}