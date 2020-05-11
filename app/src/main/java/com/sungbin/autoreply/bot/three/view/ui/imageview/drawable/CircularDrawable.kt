package com.sungbin.autoreply.bot.three.view.ui.imageview.drawable

import android.graphics.*
import android.graphics.drawable.Drawable

class CircularDrawable(bitmap: Bitmap) : Drawable() {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }

    var radius: Float = intrinsicWidth.toFloat() / 2
        set(value) {
            field = value
            invalidateSelf()
        }

    override fun draw(canvas: Canvas) {
        val width = bounds.width()
        val height = bounds.height()

        val centerX = width / 2f
        val centerY = height / 2f

        canvas.drawCircle(centerX, centerY, radius, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
        invalidateSelf()
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    fun setMatrix(matrix: Matrix) {
        paint.shader.setLocalMatrix(matrix)
        invalidateSelf()
    }
}