package com.sungbin.autoreply.bot.three.view.ui.bottombar

import android.graphics.RectF
import android.graphics.drawable.Drawable

data class BottomBarItem(
    var title: String,
    val icon: Drawable,
    var rect: RectF = RectF(),
    var alpha: Int
)
