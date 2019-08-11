@file:Suppress("NAME_SHADOWING")

package com.sungbin.kakaobot.source.hub.highlighter

import android.graphics.Color
import android.text.Editable
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import java.util.ArrayList

class JsHighlighter {

    private val data = ArrayList<Highlighter>()
    private val blue = Color.argb(255, 21, 101, 192) //일반
    private val red = Color.argb(255, 191, 54, 12) //숫자
    private val brown = Color.argb(255, 255, 160, 0) //주석
    private val green = Color.argb(255, 139, 195, 74) //문자열

    private inner class Highlighter internal constructor(
        internal var value: String, internal var color: Int)

    private fun initHighlightData() {
        val blueData = arrayOf(
            "String",
            "File",
            "Java",
            "io",
            "Array",
            "int",
            "function",
            "return",
            "var",
            "let",
            "const",
            "if",
            "else",
            "switch",
            "for",
            "while",
            "do",
            "break",
            "continue",
            "case",
            "in",
            "with",
            "true",
            "false",
            "new",
            "null",
            "undefined",
            "typeof",
            "delete",
            "try",
            "catch",
            "finally",
            "prototype",
            "this",
            "super",
            "default",
            "prototype"
        )
        for (blueDatum in blueData) {
            data.add(Highlighter(blueDatum, -1))
        }
    }

    fun apply(s: Editable): Editable {
        val s = s
        try {
            val str = s.toString()
            if (str.isEmpty()) return s
            val spans = s.getSpans(0, s.length, ForegroundColorSpan::class.java)
            for (span1 in spans) {
                s.removeSpan(span1)
            }
            var start = 0
            while (start >= 0) {
                try {
                    val index = str.indexOf("/*", start)
                    var end = str.indexOf("*/", index + 2)
                    if (index >= 0 && end >= 0) {
                        s.setSpan(ForegroundColorSpan(green), index, end + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    } else {
                        end = -5
                    }
                    start = end + 2
                } catch (e: Exception) {
                }

            }

            start = 0
            while (start >= 0) {
                try {
                    val index = str.indexOf("//", start)
                    var end = str.indexOf("\n", index + 1)
                    if (index >= 0 && end >= 0) {
                        s.setSpan(ForegroundColorSpan(green), index, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    } else {
                        end = -1
                    }
                    start = end
                } catch (e: Exception) {
                }

            }

            start = 0
            while (start >= 0) {
                try {
                    var index = str.indexOf("\"", start)
                    while (index > 0 && str[index - 1] == '\\') {
                        index = str.indexOf("\"", index + 1)
                    }
                    var end = str.indexOf("\"", index + 1)
                    while (end > 0 && str[end - 1] == '\\') {
                        end = str.indexOf("\"", end + 1)
                    }
                    if (index >= 0 && end >= 0) {
                        var span = s.getSpans(index, end + 1, ForegroundColorSpan::class.java)
                        if (span.isNotEmpty()) {
                            if (str.substring(index + 1, end).contains("/*") && str.substring(
                                    index + 1,
                                    end
                                ).contains("*/")
                            ) {
                                for (foregroundColorSpan in span) {
                                    s.removeSpan(foregroundColorSpan)
                                }
                                s.setSpan(
                                    ForegroundColorSpan(brown),
                                    index,
                                    end + 1,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            } else if (str.substring(index + 1, end).contains("//")) {
                                span = s.getSpans(index, str.indexOf("\n", end), ForegroundColorSpan::class.java)
                                for (foregroundColorSpan in span) {
                                    s.removeSpan(foregroundColorSpan)
                                }
                                s.setSpan(
                                    ForegroundColorSpan(brown),
                                    index,
                                    end + 1,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            }
                        } else {
                            s.setSpan(ForegroundColorSpan(brown), index, end + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    } else {
                        end = -5
                    }
                    start = end + 1
                } catch (e: Exception) {
                }

            }

            start = 0
            while (start >= 0) {
                try {
                    var index = str.indexOf("'", start)
                    while (index > 0 && str[index - 1] == '\\') {
                        index = str.indexOf("'", index + 1)
                    }
                    var end = str.indexOf("'", index + 1)
                    while (end > 0 && str[end - 1] == '\\') {
                        end = str.indexOf("'", end + 1)
                    }
                    if (index >= 0 && end >= 0) {
                        var span = s.getSpans(index, end + 1, ForegroundColorSpan::class.java)
                        if (span.isNotEmpty()) {
                            if (str.substring(index + 1, end).contains("/*") && str.substring(
                                    index + 1,
                                    end
                                ).contains("*/")
                            ) {
                                for (foregroundColorSpan in span) {
                                    s.removeSpan(foregroundColorSpan)
                                }
                                s.setSpan(
                                    ForegroundColorSpan(brown),
                                    index,
                                    end + 1,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            } else if (str.substring(index + 1, end).contains("//")) {
                                span = s.getSpans(index, str.indexOf("\n", end), ForegroundColorSpan::class.java)
                                for (foregroundColorSpan in span) {
                                    s.removeSpan(foregroundColorSpan)
                                }
                                s.setSpan(
                                    ForegroundColorSpan(brown),
                                    index,
                                    end + 1,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            }
                        } else {
                            s.setSpan(ForegroundColorSpan(brown), index, end + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    } else {
                        end = -5
                    }
                    start = end + 1
                } catch (e: Exception) {
                }

            }

            for (n in data.indices) {
                try {
                    start = 0
                    while (start >= 0) {
                        try {
                            val index = str.indexOf(data[n].value, start)
                            var end = index + data[n].value.length
                            if (index >= 0) {
                                var color = data[n].color
                                if (color == -1) color = blue
                                if (s.getSpans(index, end, ForegroundColorSpan::class.java)
                                        .isEmpty() && isSeperated(str, index, end - 1)) {
                                    s.setSpan(ForegroundColorSpan(color),
                                        index, end,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                }
                            } else {
                                end = -1
                            }
                            start = end
                        } catch (e: Exception) {
                        }
                    }
                } catch (e: Exception) {
                }

            }
            val redData = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".")
            for (redDatum in redData) {
                start = 0
                while (start >= 0) {
                    try {
                        val index = str.indexOf(redDatum, start)
                        var end = index + 1
                        if (index >= 0) {
                            if (s.getSpans(index, end, ForegroundColorSpan::class.java).isEmpty() && checkNumber(
                                    str,
                                    index
                                )
                            )
                                s.setSpan(ForegroundColorSpan(red), index, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        } else {
                            end = -1
                        }
                        start = end
                    } catch (e: Exception) {
                    }

                }
            }
            return s
        }
        catch (e: Exception) {
            return s
        }

    }

    private fun checkNumber(str: String, index: Int): Boolean {
        val start = getStartPos(str, index)
        val end = getEndPos(str, index)
        if (str[end - 1] == '.') return false
        return if (start == 0) {
            if (str[start] == '.') false else isNumber(str.substring(start, end))
        } else {
            if (str[start + 1] == '.') false else isNumber(str.substring(start + 1, end))
        }
    }

    private fun isSplitPoint(ch: Char): Boolean {
        return if (ch == '\n') true else " []{}()+-*/%&|!?:;,<>=^~".contains(ch + "")
    }

    private fun getStartPos(str: String, index: Int): Int {
        var index = index
        while (index >= 0) {
            if (isSplitPoint(str[index])) return index
            index--
        }
        return 0
    }

    private fun getEndPos(str: String, index: Int): Int {
        var index = index
        while (str.length > index) {
            if (isSplitPoint(str[index])) return index
            index++
        }
        return str.length
    }

    private fun isSeperated(str: String, start: Int, end: Int): Boolean {
        try {
            var front = false
            val points = " []{}()+-*/%&|!?:;,<>=^~.".toCharArray()
            if (start == 0) {
                front = true
            } else if (str[start - 1] == '\n') {
                front = true
            } else {
                for (point in points) {
                    if (str[start - 1] == point) {
                        front = true
                        break
                    }
                }
            }
            if (front) {
                try {
                    if (str[end + 1] == '\n') {
                        return true
                    } else {
                        for (point in points) {
                            if (str[end + 1] == point) return true
                        }
                    }
                } catch (e: Exception) {
                    return true
                }

            }
            return false
        } catch (e: Exception) {
            return false
        }

    }

    private fun isNumber(value: String): Boolean {
        return try {
            java.lang.Double.valueOf(value)
            true
        } catch (e: Exception) {
            false
        }

    }
}

