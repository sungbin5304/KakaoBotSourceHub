package com.sungbin.kakaobot.source.hub.dto

class CommentListItem {
    var name: String? = null
    var comment: String? = null
    var uuid: String? = null

    constructor() {}
    constructor(name: String?, comment: String?, uuid: String?) {
        this.name = name
        this.comment = comment
        this.uuid = uuid
    }
}