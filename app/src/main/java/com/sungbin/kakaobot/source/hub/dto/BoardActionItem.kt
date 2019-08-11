package com.sungbin.kakaobot.source.hub.dto

class BoardActionItem {
    var uuid: String? = null
    var key: String? = null

    constructor() {}
    constructor(uuid: String?, key: String?) {
        this.uuid = uuid
        this.key = key
    }

}