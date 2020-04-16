package com.sungbin.autoreply.bot.three.dto

class DebugListItem {
    var sender: String? = null
    var gravity: Int? = null
    var message: String? = null

    constructor() {}
    constructor(sender: String?, gravity: Int?, message: String?) {
        this.sender = sender
        this.gravity = gravity
        this.message = message
    }
}