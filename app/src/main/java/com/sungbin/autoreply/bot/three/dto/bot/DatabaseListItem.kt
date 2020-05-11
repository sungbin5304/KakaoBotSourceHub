package com.sungbin.autoreply.bot.three.dto.bot

class DatabaseListItem  {
    var name: String? = null
    var size: String? = null

    constructor() {}
    constructor(name: String?, size: String?) {
        this.name = name
        this.size = size
    }
}