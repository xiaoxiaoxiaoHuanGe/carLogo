package com.example.carlogo.domain

object RandomPracticeConfig {
    const val MIN_QUESTION_COUNT = 5
    const val MAX_QUESTION_COUNT = 50
    const val DEFAULT_QUESTION_COUNT = 10

    fun validateQuestionCount(questionCount: Int): Int {
        require(questionCount in MIN_QUESTION_COUNT..MAX_QUESTION_COUNT) {
            "随机练习题数必须在 $MIN_QUESTION_COUNT 到 $MAX_QUESTION_COUNT 之间。"
        }
        return questionCount
    }
}
