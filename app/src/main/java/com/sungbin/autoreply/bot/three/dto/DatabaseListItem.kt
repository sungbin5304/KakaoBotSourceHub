package com.sungbin.autoreply.bot.three.dto

class DatabaseListItem  {
    var name: String? = null
    var size: String? = null

    constructor() {}
    constructor(name: String?, size: String?) {
        this.name = name
        this.size = size
    }

}