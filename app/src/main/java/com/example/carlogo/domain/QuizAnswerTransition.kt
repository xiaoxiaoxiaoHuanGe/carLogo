package com.example.carlogo.domain

/** Defines when the UI may automatically move from answer feedback to the next question. */
object QuizAnswerTransition {
    const val CorrectAnswerDelayMillis = 650L

    fun shouldAutoAdvance(isAnswered: Boolean, isCorrect: Boolean, isFinished: Boolean, isFreshAnswer: Boolean): Boolean =
        isAnswered && isCorrect && !isFinished && isFreshAnswer
}
