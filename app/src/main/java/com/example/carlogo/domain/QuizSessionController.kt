package com.example.carlogo.domain

import com.example.carlogo.domain.model.Question

/**
 * Immutable quiz state. Answers remain attached to their question index so a reviewed
 * question displays exactly the answer and feedback the user originally submitted.
 */
data class QuizSessionState(
    val questions: List<Question>,
    val currentIndex: Int = 0,
    val answers: Map<Int, String> = emptyMap(),
    val awaitingNextAfterAnswer: Boolean = false,
    val isResultConfirmed: Boolean = false,
) {
    val currentQuestion: Question get() = questions[currentIndex]
    val selectedOptionId: String? get() = answers[currentIndex]
    val isAnswered: Boolean get() = selectedOptionId != null
    val latestAnsweredIndex: Int get() = answers.keys.maxOrNull() ?: -1
    val answeredCount: Int get() = answers.size
    val correctCount: Int get() = answers.count { (index, optionId) ->
        questions[index].options.firstOrNull { it.id == optionId }?.isCorrect == true
    }
    val canSelect: Boolean get() = !isAnswered && currentIndex == latestAnsweredIndex + 1
    /** These two properties drive button visibility and are based only on question position. */
    val hasPreviousQuestion: Boolean get() = currentIndex > 0
    val hasNextQuestion: Boolean get() = currentIndex < questions.lastIndex
    val canGoPrevious: Boolean get() = hasPreviousQuestion
    /** Never allow a click to skip an unanswered question. */
    val canGoNext: Boolean get() = hasNextQuestion && (isAnswered || answers.containsKey(currentIndex + 1))
    val isFreshAnswer: Boolean get() = awaitingNextAfterAnswer && currentIndex == latestAnsweredIndex
    val isFinished: Boolean get() = answeredCount == questions.size
}

/** Holds answer and review navigation rules independently from Compose UI. */
class QuizSessionController(questions: List<Question>) {
    private var state = QuizSessionState(questions.also { require(it.isNotEmpty()) { "题目不能为空。" } })

    fun current(): QuizSessionState = state

    fun answer(optionId: String): QuizSessionState {
        if (!state.canSelect) return state
        val option = state.currentQuestion.options.firstOrNull { it.id == optionId }
            ?: throw IllegalArgumentException("选项不属于当前题目：$optionId")
        state = state.copy(
            answers = state.answers + (state.currentIndex to option.id),
            awaitingNextAfterAnswer = true,
        )
        return state
    }

    /** Moves backward only through answered questions. */
    fun previous(): QuizSessionState {
        if (state.canGoPrevious) state = state.copy(
            currentIndex = state.currentIndex - 1,
            awaitingNextAfterAnswer = false,
        )
        return state
    }

    /**
     * Advances through answered questions while reviewing. A brand-new question is
     * reachable only immediately after the user has just answered the latest question.
     */
    fun next(): QuizSessionState {
        if (state.canGoNext) state = state.copy(
                currentIndex = state.currentIndex + 1,
                awaitingNextAfterAnswer = false,
            )
        return state
    }

    /** Lets the UI enter the result screen only after the user has seen the final answer feedback. */
    fun confirmResult(): QuizSessionState {
        if (state.isFinished) state = state.copy(isResultConfirmed = true)
        return state
    }
}
