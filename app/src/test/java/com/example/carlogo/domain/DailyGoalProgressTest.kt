package com.example.carlogo.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DailyGoalProgressTest {

    @Test
    fun `starts with the full daily target remaining`() {
        val progress = DailyGoalProgress.fromCompletedQuestionCount(0)

        assertEquals(0, progress.completedQuestionCount)
        assertEquals(10, progress.goalQuestionCount)
        assertEquals(10, progress.remainingQuestionCount)
        assertEquals(0f, progress.progress)
        assertFalse(progress.isCompleted)
    }

    @Test
    fun `shows partial progress for completed questions`() {
        val progress = DailyGoalProgress.fromCompletedQuestionCount(6)

        assertEquals(6, progress.completedQuestionCount)
        assertEquals(4, progress.remainingQuestionCount)
        assertEquals(0.6f, progress.progress)
        assertFalse(progress.isCompleted)
    }

    @Test
    fun `caps the displayed progress after the daily target`() {
        val progress = DailyGoalProgress.fromCompletedQuestionCount(15)

        assertEquals(10, progress.completedQuestionCount)
        assertEquals(0, progress.remainingQuestionCount)
        assertEquals(1f, progress.progress)
        assertTrue(progress.isCompleted)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects a negative completed question count`() {
        DailyGoalProgress.fromCompletedQuestionCount(-1)
    }
}
