package com.sungbin.autoreply.bot.three.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.sungbin.autoreply.bot.three.R

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

    fun makeLayoutDialog(act: Activity, view: View, listener: DialogInterface.OnClickListener){
        val dialog = AlertDialog.Builder(act)
        dialog.setView(view)
        dialog.setPositiveButton("확인", listener)
        val alert = dialog.create()
        alert.window!!.setWindowAnimations(R.style.DialogAnimation)
        alert.show()
    }
}