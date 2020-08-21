package com.sungbin.autoreply.bot.three.view.ui.imageview.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet

import androidx.appcompat.widget.AppCompatImageView
import com.sungbin.autoreply.bot.three.R
import kotlin.properties.Delegates

class RoundedImageView : AppCompatImageView {
    companion object {
        private const val DEFAULT_RADIUS = 0
    }

    private var bitmapRect: RectF? = null
    private var clipPath: Path? = null

    var radius: Int by Delegates.observable(DEFAULT_RADIUS) { _, _, _ ->
        invalidate()
    }

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.RoundedImageView,
            defStyleAttr,
            0
        )
        initialize(typedArray)
    }

    private fun initialize(typedArray: TypedArray?) {
        clipPath = Path()

        if (typedArray != null) {
            radius = typedArray.getDimensionPixelSize(
                R.styleable.RoundedImageView_radius,
                DEFAULT_RADIUS
            )
            typedArray.recycle()
        }
    }

    public override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        bitmapRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        if (radius != 0) {
            clipPath!!.reset()
            clipPath!!.addRoundRect(
                bitmapRect!!,
                radius.toFloat(),
                radius.toFloat(),
                Path.Direction.CW
            )
            canvas.clipPath(clipPath!!)
        }

        super.onDraw(canvas)
    }
}