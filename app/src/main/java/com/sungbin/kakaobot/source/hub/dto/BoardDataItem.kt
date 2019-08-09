package com.sungbin.kakaobot.source.hub.dto

class BoardDataItem {
    var title: String? = null
    var desc: String? = null
    var good_count: Int? = null
    var bad_count: Int? = null
    var source: String? = null
    var version: String? = null
    var uuid: String? = null

    constructor() {}
    constructor(
        title: String?,
        desc: String?,
        good_count: Int?,
        bad_count: Int?,
        source: String?,
        version: String?,
        uuid: String?
    ) {
        this.title = title
        this.desc = desc
        this.good_count = good_count
        this.bad_count = bad_count
        this.source = source
        this.version = version
        this.uuid = uuid
    }
}