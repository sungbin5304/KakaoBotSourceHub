package com.sungbin.autoreply.bot.three.dto

class LogListItem  {
    var name: String? = null
    var desc: String? = null
    var script: String? = null
    var type: Int? = null

    constructor() {}
    constructor(name: String?, desc: String?, script: String?, type: Int?) {
        this.name = name
        this.desc = desc
        this.script = script
        this.type = type
    }

}