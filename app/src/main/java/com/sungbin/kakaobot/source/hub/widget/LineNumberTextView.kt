package com.sungbin.kakaobot.source.hub.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.text.Layout
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.sungbin.kakaobot.source.hub.R
import kotlin.math.max
import kotlin.math.min


open class LineNumberTextView : AppCompatTextView {

    private var mTextPaint: Paint? = null
    private var mLeftPadding: Int = 0
    private var mRightPadding: Int = 0
    private var mLayoutOnLeft: Boolean = false
    private var mHugLine: Boolean = false
    private var ctx: Context? = null
    private var mController: Controller? = null
    private var mCachedLineNumberPadding: Int = 0

    private val defaultLineNumberController: Controller?
        get() {
            try {
                return object : Controller {
                    override fun getLineNumberText(layoutOnLeft: Boolean, line: Int): String {
                        return line.toString()
                    }

                    override fun showLineNumber(line: Int): Boolean {
                        return true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        }

    private val lineNumberPadding: Int
        get() {
            try {
                val layout = layout
                val lineCount = layout?.lineCount ?: 1
                return mTextPaint!!.measureText(mController!!.getLineNumberText(mLayoutOnLeft, lineCount)).toInt()
            } catch (e: Exception) {
                e.printStackTrace()
                return 0
            }

        }

    interface Controller {
        fun getLineNumberText(layoutOnLeft: Boolean, line: Int): String
        fun showLineNumber(line: Int): Boolean
    }

    constructor(context: Context) : super(context) {
        ctx = context
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        ctx = context
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        ctx = context
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        mTextPaint = Paint()
        mTextPaint!!.typeface = typeface
        mTextPaint!!.textSize = 20f
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.isSubpixelText = true
        mTextPaint!!.textAlign = Paint.Align.LEFT
        mController = defaultLineNumberController
        mLeftPadding = paddingLeft
        mRightPadding = paddingRight
        mCachedLineNumberPadding = 0
        if (attrs != null) {
            mLayoutOnLeft = true
            mHugLine = false
        } else {
            mLayoutOnLeft = true
            mHugLine = false
        }
        fixLineNumberPadding()
    }

    override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        try {
            if (mTextPaint == null) {
                post { fixLineNumberPadding() }
            } else {
                fixLineNumberPadding()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    public override fun isPaddingOffsetRequired(): Boolean {
        return true
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        try {
            val fixPadding = mLeftPadding != left || mRightPadding != right
            mLeftPadding = left
            mRightPadding = right
            if (fixPadding) {
                fixLineNumberPadding()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @SuppressLint("ObsoleteSdkInt")
    private fun textViewClip(canvas: Canvas) {
        try {
            val scrollX = scrollX
            val scrollY = scrollY
            val left = left
            val right = right
            val top = top
            val bottom = bottom
            val maxScrollY = layout.height - bottom - top - compoundPaddingBottom - compoundPaddingTop

            var clipLeft = scrollX.toFloat()
            var clipTop = (if (scrollY == 0) 0 else extendedPaddingTop + scrollY).toFloat()
            var clipRight = (right - left + scrollX).toFloat()
            var clipBottom =
                (bottom - top + scrollY - if (scrollY == maxScrollY) 0 else extendedPaddingBottom).toFloat()

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                val shadowRadius = shadowRadius
                if (shadowRadius != 0f) {
                    val shadowDx = shadowDx
                    val shadowDy = shadowDy
                    clipLeft += min(0f, shadowDx - shadowRadius)
                    clipRight += max(0f, shadowDx + shadowRadius)

                    clipTop += min(0f, shadowDy - shadowRadius)
                    clipBottom += max(0f, shadowDy + shadowRadius)
                }
            }

            canvas.clipRect(clipLeft, clipTop, clipRight, clipBottom)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        try {
            canvas.save()
            textViewClip(canvas)

            val layout = layout

            val scrollY = scrollY
            val firstLine = layout.getLineForVertical(scrollY)
            val lastLine = layout.getLineForVertical(scrollY + (height - extendedPaddingTop - extendedPaddingBottom))
            var positionY = baseline + (layout.getLineBaseline(firstLine) - layout.getLineBaseline(0))

            drawLineNumber(canvas, layout, positionY, firstLine)
            for (i in firstLine + 1..lastLine) {
                positionY += layout.getLineBaseline(i) - layout.getLineBaseline(i - 1)
                drawLineNumber(canvas, layout, positionY, i)
            }

            canvas.restore()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun drawLineNumber(canvas: Canvas, layout: Layout, positionY: Int, line: Int) {
        try {
            if (mController!!.showLineNumber(line + 1)) {
                val positionX = getLineNumberX(layout, line)
                canvas.drawText(
                    mController!!.getLineNumberText(mLayoutOnLeft, line + 1),
                    positionX.toFloat(),
                    positionY.toFloat(),
                    mTextPaint!!
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun fixLineNumberPadding() {
        try {
            mCachedLineNumberPadding = lineNumberPadding
            if (mLayoutOnLeft) {
                super.setPadding(mLeftPadding + mCachedLineNumberPadding, paddingTop, mRightPadding, paddingBottom)
            } else {
                super.setPadding(mLeftPadding, paddingTop, mRightPadding + mCachedLineNumberPadding, paddingBottom)
            }
            invalidate()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getLineNumberX(layout: Layout, line: Int): Int {
        try {
            if (mLayoutOnLeft) {
                val leftColumn = leftPaddingOffset
                return if (mHugLine) {
                    val lineLeft = layout.getLineLeft(line).toInt()
                    max(leftColumn, lineLeft - mLeftPadding)
                } else {
                    leftColumn
                }
            } else {
                 val rightColumn = width + rightPaddingOffset - mCachedLineNumberPadding
                return if (mHugLine) {
                    val lineRight = layout.getLineRight(line).toInt()
                    min(rightColumn, lineRight + compoundPaddingLeft + mRightPadding)
                } else {
                    rightColumn
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }

    }
}
