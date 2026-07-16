package com.example.carlogo.domain

data class CompletedLearningSession(
    val totalQuestionCount: Int,
    val correctQuestionCount: Int,
    val finishedAtMillis: Long,
) {
    init {
        require(totalQuestionCount >= 0) { "Total question count cannot be negative." }
        require(correctQuestionCount in 0..totalQuestionCount) { "Correct question count must be within the completed question count." }
    }
}

class LearningOverview private constructor(
    val totalCompletedQuestionCount: Int,
    val totalCorrectQuestionCount: Int,
    val weeklyCompletedSessionCount: Int,
    val pendingMistakeCount: Int,
) {
    val correctRatePercent: Int = if (totalCompletedQuestionCount == 0) {
        0
    } else {
        totalCorrectQuestionCount * 100 / totalCompletedQuestionCount
    }

    companion object {
        fun fromCompletedSessions(
            sessions: List<CompletedLearningSession>,
            weekStartMillis: Long,
            pendingMistakeCount: Int,
        ): LearningOverview {
            require(pendingMistakeCount >= 0) { "Pending mistake count cannot be negative." }
            return LearningOverview(
                totalCompletedQuestionCount = sessions.sumOf { it.totalQuestionCount },
                totalCorrectQuestionCount = sessions.sumOf { it.correctQuestionCount },
                weeklyCompletedSessionCount = sessions.count { it.finishedAtMillis >= weekStartMillis },
                pendingMistakeCount = pendingMistakeCount,
            )
        }
    }
}
