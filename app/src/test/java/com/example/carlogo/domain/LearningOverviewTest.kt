package com.example.carlogo.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class LearningOverviewTest {

    @Test
    fun `shows zero state when there are no completed sessions`() {
        val overview = LearningOverview.fromCompletedSessions(
            sessions = emptyList(),
            weekStartMillis = 1_000L,
            pendingMistakeCount = 0,
        )

        assertEquals(0, overview.totalCompletedQuestionCount)
        assertEquals(0, overview.correctRatePercent)
        assertEquals(0, overview.weeklyCompletedSessionCount)
        assertEquals(0, overview.pendingMistakeCount)
    }

    @Test
    fun `aggregates cumulative questions accuracy weekly sessions and mistakes`() {
        val overview = LearningOverview.fromCompletedSessions(
            sessions = listOf(
                CompletedLearningSession(totalQuestionCount = 10, correctQuestionCount = 8, finishedAtMillis = 1_000L),
                CompletedLearningSession(totalQuestionCount = 5, correctQuestionCount = 4, finishedAtMillis = 2_000L),
            ),
            weekStartMillis = 1_500L,
            pendingMistakeCount = 3,
        )

        assertEquals(15, overview.totalCompletedQuestionCount)
        assertEquals(12, overview.totalCorrectQuestionCount)
        assertEquals(80, overview.correctRatePercent)
        assertEquals(1, overview.weeklyCompletedSessionCount)
        assertEquals(3, overview.pendingMistakeCount)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects a session with more correct answers than completed questions`() {
        CompletedLearningSession(totalQuestionCount = 5, correctQuestionCount = 6, finishedAtMillis = 1_000L)
    }
}
