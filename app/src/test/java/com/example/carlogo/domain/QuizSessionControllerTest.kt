package com.example.carlogo.domain

import com.example.carlogo.domain.model.Question
import com.example.carlogo.domain.model.QuestionType
import com.example.carlogo.domain.model.QuizOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizSessionControllerTest {
    @Test
    fun `only a correct non-final answer auto advances`() {
        assertTrue(QuizAnswerTransition.shouldAutoAdvance(isAnswered = true, isCorrect = true, isFinished = false, isFreshAnswer = true))
        assertFalse(QuizAnswerTransition.shouldAutoAdvance(isAnswered = true, isCorrect = false, isFinished = false, isFreshAnswer = true))
        assertFalse(QuizAnswerTransition.shouldAutoAdvance(isAnswered = false, isCorrect = true, isFinished = false, isFreshAnswer = true))
        assertFalse(QuizAnswerTransition.shouldAutoAdvance(isAnswered = true, isCorrect = true, isFinished = true, isFreshAnswer = true))
        assertFalse(QuizAnswerTransition.shouldAutoAdvance(isAnswered = true, isCorrect = true, isFinished = false, isFreshAnswer = false))
    }

    @Test
    fun `answer locks question scores it and next advances`() {
        val controller = QuizSessionController(listOf(question("q1", true), question("q2", false)))

        val answered = controller.answer("correct-q1")
        assertTrue(answered.isAnswered)
        assertEquals(1, answered.correctCount)
        assertFalse(answered.canSelect)

        val next = controller.next()
        assertEquals(1, next.currentIndex)
        assertFalse(next.isAnswered)
    }

    @Test
    fun `review navigation can return to the next unanswered question`() {
        val controller = QuizSessionController(listOf(question("q1", true), question("q2", true), question("q3", true)))

        controller.answer("correct-q1")
        controller.next()
        controller.answer("correct-q2")

        val previous = controller.previous()
        assertEquals(0, previous.currentIndex)
        assertEquals("correct-q1", previous.selectedOptionId)
        assertTrue(previous.canGoNext)

        val reviewedLatest = controller.next()
        assertEquals(1, reviewedLatest.currentIndex)
        assertEquals("correct-q2", reviewedLatest.selectedOptionId)

        val nextUnanswered = controller.next()
        assertEquals(2, nextUnanswered.currentIndex)
        assertFalse(nextUnanswered.isAnswered)
        assertFalse(nextUnanswered.hasNextQuestion)
    }

    @Test
    fun `navigation button visibility is determined by question position`() {
        val controller = QuizSessionController(listOf(question("q1", true), question("q2", true), question("q3", true)))

        val first = controller.current()
        assertFalse(first.hasPreviousQuestion)
        assertTrue(first.hasNextQuestion)

        controller.answer("correct-q1")
        val second = controller.next()
        assertTrue(second.hasPreviousQuestion)
        assertTrue(second.hasNextQuestion)

        controller.answer("correct-q2")
        val last = controller.next()
        assertTrue(last.hasPreviousQuestion)
        assertFalse(last.hasNextQuestion)
    }

    @Test
    fun `unanswered current question can return to its answered predecessor`() {
        val controller = QuizSessionController(listOf(question("q1", true), question("q2", true), question("q3", true)))

        controller.answer("correct-q1")
        controller.next()
        controller.answer("correct-q2")
        val unansweredThird = controller.next()

        assertEquals(2, unansweredThird.currentIndex)
        assertFalse(unansweredThird.isAnswered)
        assertTrue(unansweredThird.canGoPrevious)

        val previous = controller.previous()
        assertEquals(1, previous.currentIndex)
        assertEquals("correct-q2", previous.selectedOptionId)
    }

    private fun question(id: String, firstCorrect: Boolean) = Question(
        id, QuestionType.BrandToModel, "题目", "brand", "car",
        listOf(
            QuizOption("correct-$id", "正确", "Correct", isCorrect = firstCorrect),
            QuizOption("wrong-$id", "错误", "Wrong", isCorrect = !firstCorrect),
            QuizOption("other-$id", "其他", "Other", isCorrect = false),
            QuizOption("last-$id", "末项", "Last", isCorrect = false),
        ),
    )
}
