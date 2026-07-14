package com.example.carlogo.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class RandomPracticeConfigTest {

    @Test
    fun `default question count is ten`() {
        assertEquals(10, RandomPracticeConfig.DEFAULT_QUESTION_COUNT)
    }

    @Test
    fun `accepts both configured range endpoints`() {
        assertEquals(5, RandomPracticeConfig.validateQuestionCount(5))
        assertEquals(50, RandomPracticeConfig.validateQuestionCount(50))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects a question count below the minimum`() {
        RandomPracticeConfig.validateQuestionCount(4)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects a question count above the maximum`() {
        RandomPracticeConfig.validateQuestionCount(51)
    }
}
