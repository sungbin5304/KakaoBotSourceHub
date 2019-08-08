package com.sungbin.kakaobot.source.hub.utils

import android.content.Context
import android.content.res.Resources
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.sungbin.kakaobot.source.hub.R

object DialogUtils {
    fun makeMarginLayout(res: Resources, ctx: Context, layout: LinearLayout): FrameLayout{
        val container = FrameLayout(ctx)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        params.leftMargin = res.getDimensionPixelSize(R.dimen.margin_default)
        params.rightMargin = res.getDimensionPixelSize(R.dimen.margin_default)
        params.topMargin = res.getDimensionPixelSize(R.dimen.margin_default)

        layout.layoutParams = params
        container.addView(layout)

        return container
    }
}