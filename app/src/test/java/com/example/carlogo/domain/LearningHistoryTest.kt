package com.example.carlogo.domain

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LearningHistoryTest {

    private val today = LocalDate.of(2026, 7, 16)

    @Test
    fun `returns seven consecutive local dates including empty days`() {
        val summaries = LearningHistory.fromCompletedSessions(
            sessions = emptyList(),
            today = today,
            zoneId = ZoneOffset.UTC,
        )

        assertEquals((0..6).map { today.minusDays((6 - it).toLong()) }, summaries.map { it.date })
        assertEquals(List(7) { 0L }, summaries.map { it.completedQuestionCount })
        assertNull(summaries.first().correctRatePercent)
        assertEquals(HeatLevel.NONE, summaries.first().heatLevel)
    }

    @Test
    fun `weights same day sessions and keeps their rows chronological`() {
        val summaries = LearningHistory.fromCompletedSessions(
            sessions = listOf(
                CompletedPracticeSession("BrandPractice", 2, 1, instant("2026-07-16T10:00:00Z")),
                CompletedPracticeSession("Random", 10, 8, instant("2026-07-16T08:00:00Z")),
            ),
            today = today,
            zoneId = ZoneOffset.UTC,
        )

        val summary = summaries.last()
        assertEquals(12, summary.completedQuestionCount)
        assertEquals(9, summary.correctQuestionCount)
        assertEquals(75, summary.correctRatePercent)
        assertEquals(2, summary.sessionCount)
        assertEquals(listOf("Random", "BrandPractice"), summary.sessions.map { it.mode })
    }

    @Test
    fun `omits sessions older than the preceding six local calendar days`() {
        val summaries = LearningHistory.fromCompletedSessions(
            sessions = listOf(
                CompletedPracticeSession("Random", 4, 3, instant("2026-07-09T12:00:00Z")),
                CompletedPracticeSession("Random", 5, 4, instant("2026-07-10T12:00:00Z")),
            ),
            today = today,
            zoneId = ZoneOffset.UTC,
        )

        assertEquals(5, summaries.first().completedQuestionCount)
        assertEquals(0, summaries.sumOf { it.completedQuestionCount } - 5)
    }

    @Test
    fun `assigns every heat level from completed question count`() {
        val summaries = LearningHistory.fromCompletedSessions(
            sessions = listOf(
                CompletedPracticeSession("Random", 1, 1, instant("2026-07-13T12:00:00Z")),
                CompletedPracticeSession("Random", 10, 9, instant("2026-07-14T12:00:00Z")),
                CompletedPracticeSession("Random", 20, 18, instant("2026-07-15T12:00:00Z")),
            ),
            today = today,
            zoneId = ZoneOffset.UTC,
        )

        assertEquals(
            listOf(HeatLevel.LOW, HeatLevel.MEDIUM, HeatLevel.HIGH, HeatLevel.NONE),
            summaries.takeLast(4).map { it.heatLevel },
        )
    }

    @Test
    fun `preserves large daily totals and calculates correct rate without integer overflow`() {
        val summaries = LearningHistory.fromCompletedSessions(
            sessions = listOf(
                CompletedPracticeSession("Random", Int.MAX_VALUE, 0, instant("2026-07-15T08:00:00Z")),
                CompletedPracticeSession("Random", Int.MAX_VALUE, 0, instant("2026-07-15T10:00:00Z")),
                CompletedPracticeSession("Random", 1_000_000_000, 1_000_000_000, instant("2026-07-16T08:00:00Z")),
                CompletedPracticeSession("Random", 1_000_000_000, 1_000_000_000, instant("2026-07-16T10:00:00Z")),
            ),
            today = today,
            zoneId = ZoneOffset.UTC,
        )

        assertEquals(4_294_967_294L, summaries[5].completedQuestionCount)
        assertEquals(2_000_000_000L, summaries.last().completedQuestionCount)
        assertEquals(100, summaries.last().correctRatePercent)
    }

    private fun instant(value: String): Long = Instant.parse(value).toEpochMilli()
}
