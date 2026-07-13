package com.example.carlogo.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class QuizPromptLayoutTest {
    @Test
    fun `prompt typography shrinks only when measured text reaches three lines`() {
        assertEquals(1f, QuizPromptLayout.fontScaleForMeasuredLineCount(1))
        assertEquals(1f, QuizPromptLayout.fontScaleForMeasuredLineCount(2))
        assertEquals(0.8f, QuizPromptLayout.fontScaleForMeasuredLineCount(3))
        assertEquals(0.8f, QuizPromptLayout.fontScaleForMeasuredLineCount(4))
    }
}
