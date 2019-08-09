package com.sungbin.kakaobot.source.hub.dto

class BoardListItem {
    var title: String? = null
    var desc: String? = null
    var good_count: Int? = null
    var bad_count: Int? = null
    var uuid: String? = null

    constructor() {}
    constructor(title: String?, desc: String?, good_count: Int?, bad_count: Int?, uuid: String?) {
        this.title = title
        this.desc = desc
        this.good_count = good_count
        this.bad_count = bad_count
        this.uuid = uuid
    }

}