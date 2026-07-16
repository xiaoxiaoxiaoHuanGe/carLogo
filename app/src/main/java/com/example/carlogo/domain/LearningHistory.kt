package com.example.carlogo.domain

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class CompletedPracticeSession(
    val mode: String,
    val totalQuestionCount: Int,
    val correctQuestionCount: Int,
    val finishedAtMillis: Long,
) {
    init {
        require(totalQuestionCount >= 0) { "Total question count cannot be negative." }
        require(correctQuestionCount in 0..totalQuestionCount) {
            "Correct question count must be within the completed question count."
        }
    }
}

enum class HeatLevel {
    NONE,
    LOW,
    MEDIUM,
    HIGH,
}

data class LearningDaySummary(
    val date: LocalDate,
    val completedQuestionCount: Long,
    val correctQuestionCount: Long,
    val correctRatePercent: Int?,
    val sessionCount: Int,
    val heatLevel: HeatLevel,
    val sessions: List<CompletedPracticeSession>,
)

object LearningHistory {

    fun fromCompletedSessions(
        sessions: List<CompletedPracticeSession>,
        today: LocalDate,
        zoneId: ZoneId,
    ): List<LearningDaySummary> {
        val startDate = today.minusDays(DAYS_IN_HISTORY - 1L)
        val sessionsByDate = sessions
            .filter { session ->
                session.localDate(zoneId) in startDate..today
            }
            .groupBy { it.localDate(zoneId) }

        return (0 until DAYS_IN_HISTORY).map { offset ->
            val date = startDate.plusDays(offset.toLong())
            val dailySessions = sessionsByDate[date].orEmpty().sortedBy { it.finishedAtMillis }
            val completedQuestionCount = dailySessions.sumOf { it.totalQuestionCount.toLong() }
            val correctQuestionCount = dailySessions.sumOf { it.correctQuestionCount.toLong() }

            LearningDaySummary(
                date = date,
                completedQuestionCount = completedQuestionCount,
                correctQuestionCount = correctQuestionCount,
                correctRatePercent = completedQuestionCount.takeIf { it > 0 }
                    ?.let { correctRatePercent(correctQuestionCount, it) },
                sessionCount = dailySessions.size,
                heatLevel = heatLevelFor(completedQuestionCount),
                sessions = dailySessions,
            )
        }
    }

    private fun CompletedPracticeSession.localDate(zoneId: ZoneId): LocalDate =
        Instant.ofEpochMilli(finishedAtMillis).atZone(zoneId).toLocalDate()

    private fun correctRatePercent(correctQuestionCount: Long, completedQuestionCount: Long): Int {
        var percent = 0
        var remainder = 0L
        val amountBeforeWholePercent = completedQuestionCount - correctQuestionCount

        repeat(100) {
            if (remainder >= amountBeforeWholePercent) {
                remainder -= amountBeforeWholePercent
                percent += 1
            } else {
                remainder += correctQuestionCount
            }
        }

        return percent
    }

    private fun heatLevelFor(completedQuestionCount: Long): HeatLevel = when {
        completedQuestionCount == 0L -> HeatLevel.NONE
        completedQuestionCount < MEDIUM_THRESHOLD -> HeatLevel.LOW
        completedQuestionCount < HIGH_THRESHOLD -> HeatLevel.MEDIUM
        else -> HeatLevel.HIGH
    }

    private const val DAYS_IN_HISTORY = 7
    private const val MEDIUM_THRESHOLD = 10L
    private const val HIGH_THRESHOLD = 20L
}
