package com.sungbin.autoreply.bot.three.view.ui.edittext

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.Selection
import android.util.AttributeSet
import android.view.Gravity
import android.widget.EditText
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import com.sungbin.autoreply.bot.three.R
import kotlin.math.log10

class CodeEditor(context: Context, attrs: AttributeSet?) :
    ScrollView(context, attrs) {
    private val dp: Int
    private val rect: Rect
    private val lineRect: Rect
    private val lineNumberPaint: Paint
    private val linePaint: Paint
    private val highlightPaint: Paint
    private var currentLine = 0

    val editor: EditText

    override fun onDraw(canvas: Canvas) {
        val lineCount = editor.lineCount
        val digits = log10(lineCount.toDouble()).toInt()
        val textWidth = lineNumberPaint.measureText("" + lineCount).toInt()
        val selectedLine = selectedLine
        getDrawingRect(rect)

        for (i in currentLine - 20..currentLine + 80) {
            if (i in 1..lineCount) {
                val baseline = editor.getLineBounds(i - 1, lineRect)
                val spaceCount = digits - log10(i.toDouble()).toInt()
                canvas.drawText(
                    getSpace(spaceCount) + i,
                    rect.left + dp * 4.toFloat(),
                    baseline.toFloat(),
                    lineNumberPaint
                )
                if (i - 1 == selectedLine) {
                    lineRect.left += rect.left - dp * 4
                    canvas.drawRect(lineRect, highlightPaint)
                }
            }
        }

        canvas.drawLine(
            rect.left + textWidth + (dp * 8).toFloat(),
            rect.top.toFloat(),
            rect.left + textWidth + (dp * 8).toFloat(),
            rect.bottom.toFloat(),
            linePaint
        )

        editor.setPadding(textWidth + dp * 12, 0, 0, 0)
        super.onDraw(canvas)
    }

    val selectedLine: Int
        get() {
            val selectionStart = Selection.getSelectionStart(editor.text)
            val layout = editor.layout
            return if (selectionStart != -1) {
                layout.getLineForOffset(selectionStart)
            } else -1
        }

    fun getSpace(count: Int): String {
        val result = StringBuilder()
        for (i in 0 until count) result.append(" ")
        return result.toString()
    }

    init {
        isFillViewport = true
        dp = context.resources.displayMetrics.density.toInt()
        editor = EditText(context)
        editor.background = null
        editor.gravity = Gravity.TOP or Gravity.START
        editor.setHorizontallyScrolling(true)
        editor.textSize = 14f
        editor.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
        editor.typeface = Typeface.MONOSPACE
        editor.viewTreeObserver.addOnDrawListener { invalidate() }
        addView(editor)
        rect = Rect()
        lineRect = Rect()
        lineNumberPaint = Paint()
        lineNumberPaint.textSize = dp * 10.toFloat()
        lineNumberPaint.color = ContextCompat.getColor(context, R.color.colorGray)
        lineNumberPaint.typeface = Typeface.MONOSPACE
        linePaint = Paint()
        linePaint.color = ContextCompat.getColor(context, R.color.colorGray)
        highlightPaint = Paint()
        highlightPaint.color = ContextCompat.getColor(context, R.color.colorAccent)
        highlightPaint.alpha = 64
        viewTreeObserver.addOnScrollChangedListener {
            currentLine = editor.lineCount * scrollY / getChildAt(0).height
            invalidate()
        }
    }
}