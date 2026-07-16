package com.example.carlogo.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MistakeReviewShortcutPresentationTest {

    @Test
    fun `pending mistakes show the count and review detail`() {
        val presentation = MistakeReviewShortcutPresentation.fromPendingMistakeCount(2)

        assertEquals("\u5f85\u590d\u4e60 2 \u9053", presentation.headline)
        assertEquals("\u67e5\u770b\u6613\u9519\u8f66\u578b\u548c\u54c1\u724c", presentation.detail)
        assertTrue(presentation.hasPendingMistakes)
    }

    @Test
    fun `zero pending mistakes show the empty state copy`() {
        val presentation = MistakeReviewShortcutPresentation.fromPendingMistakeCount(0)

        assertEquals("\u6682\u65e0\u9519\u9898", presentation.headline)
        assertEquals("\u7ee7\u7eed\u4fdd\u6301", presentation.detail)
        assertFalse(presentation.hasPendingMistakes)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `negative pending mistake count is rejected`() {
        MistakeReviewShortcutPresentation.fromPendingMistakeCount(-1)
    }
}
