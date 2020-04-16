package com.sungbin.autoreply.bot.three.dto

class ScriptListItem {
    var name: String? = null
    var onOff: Boolean? = null
    var type: Int? = null
    var lastTime: String? = null
    var image: Int? = null

    constructor(){}
    constructor(name: String?, onOff: Boolean, type: Int?, lastTime: String?, image: Int) {
        this.name = name
        this.onOff = onOff
        this.type = type
        this.lastTime = lastTime
        this.image = image
    }
}