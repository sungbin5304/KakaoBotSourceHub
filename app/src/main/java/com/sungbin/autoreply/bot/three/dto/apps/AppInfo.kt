package com.sungbin.autoreply.bot.three.dto.apps

import android.graphics.drawable.Drawable


/**
 * Created by SungBin on 2020-05-10.
 */

data class AppInfo(
    var name: String,
    var icon: Drawable,
    var packageString: String
)