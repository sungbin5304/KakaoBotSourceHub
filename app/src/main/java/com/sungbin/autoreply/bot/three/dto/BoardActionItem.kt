package com.sungbin.autoreply.bot.three.dto

class BoardActionItem {
    var uuid: String? = null
    var key: String? = null

    constructor() {}
    constructor(uuid: String?, key: String?) {
        this.uuid = uuid
        this.key = key
    }

}