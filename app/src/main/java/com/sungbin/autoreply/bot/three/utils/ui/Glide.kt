package com.sungbin.autoreply.bot.three.utils.ui

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.util.*


object Glide {
    fun set(context: Context, content: Drawable, view: ImageView) {
        Glide.with(context)
            .load(content)
            .format(DecodeFormat.PREFER_ARGB_8888)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .skipMemoryCache(true)
            .into(view)
    }
    fun set(context: Context, content: Resources, view: ImageView) {
        Glide.with(context)
            .load(content)
            .format(DecodeFormat.PREFER_ARGB_8888)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .skipMemoryCache(true)
            .into(view)
    }
    fun set(context: Context, content: String, view: ImageView) {
        Glide.with(context)
            .load(content)
            .format(DecodeFormat.PREFER_ARGB_8888)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .skipMemoryCache(true)
            .into(view)
    }
    fun set(context: Context, content: Bitmap, view: ImageView) {
        Glide.with(context)
            .load(content)
            .format(DecodeFormat.PREFER_ARGB_8888)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .skipMemoryCache(true)
            .into(view)
    }
}