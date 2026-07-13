package com.example.carlogo.ui

/** Typography rule for the quiz prompt after Compose has measured its actual line count. */
object QuizPromptLayout {
    const val COMPACT_FONT_SCALE = 0.8f
    const val COMPACT_TRIGGER_LINE_COUNT = 3

    fun fontScaleForMeasuredLineCount(lineCount: Int): Float =
        if (lineCount >= COMPACT_TRIGGER_LINE_COUNT) COMPACT_FONT_SCALE else 1f
}
