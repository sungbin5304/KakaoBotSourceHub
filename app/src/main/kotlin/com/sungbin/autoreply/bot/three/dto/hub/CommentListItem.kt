package com.sungbin.autoreply.bot.three.dto.hub

class CommentListItem {
    var name: String? = null
    var comment: String? = null
    var uuid: String? = null
    var uid: String? = null
    var key: String? = null

    constructor()
    constructor(name: String?, comment: String?, uuid: String?, uid: String?, key: String?) {
        this.name = name
        this.comment = comment
        this.uuid = uuid
        this.uid = uid
        this.key = key
    }
}