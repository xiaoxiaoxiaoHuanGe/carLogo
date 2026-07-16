package com.example.carlogo.domain

class DailyGoalProgress private constructor(
    val completedQuestionCount: Int,
    val goalQuestionCount: Int,
) {
    val remainingQuestionCount: Int = goalQuestionCount - completedQuestionCount
    val progress: Float = completedQuestionCount.toFloat() / goalQuestionCount
    val isCompleted: Boolean = completedQuestionCount == goalQuestionCount

    companion object {
        const val DAILY_GOAL_QUESTION_COUNT = 10

        fun fromCompletedQuestionCount(completedQuestionCount: Int): DailyGoalProgress {
            require(completedQuestionCount >= 0) { "Completed question count cannot be negative." }
            return DailyGoalProgress(
                completedQuestionCount = completedQuestionCount.coerceAtMost(DAILY_GOAL_QUESTION_COUNT),
                goalQuestionCount = DAILY_GOAL_QUESTION_COUNT,
            )
        }
    }
}
