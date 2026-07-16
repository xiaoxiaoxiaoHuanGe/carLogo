package com.example.carlogo.domain

import com.example.carlogo.domain.model.Brand
import com.example.carlogo.domain.model.CarModel
import com.example.carlogo.domain.model.QuestionType
import com.example.carlogo.domain.model.QuizMode
import com.example.carlogo.domain.model.displayText
import com.example.carlogo.domain.model.letteredText
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizGeneratorTest {

    private val brands = (1..5).map { brandNumber ->
        Brand(
            id = "brand-$brandNumber",
            nameZh = "品牌$brandNumber",
            nameEn = "Brand $brandNumber",
            cars = (1..5).map { carNumber ->
                CarModel(
                    id = "car-$brandNumber-$carNumber",
                    brandId = "brand-$brandNumber",
                    nameZh = "车型$brandNumber-$carNumber",
                    nameEn = "Model $brandNumber-$carNumber",
                )
            },
        )
    }

    @Test
    fun `random round defaults to ten four-option questions`() {
        val questions = QuizGenerator(Random(7)).createRound(QuizMode.Random, brands)

        assertEquals(10, questions.size)
        questions.forEach { question ->
            assertEquals(4, question.options.size)
            assertEquals(4, question.options.map { it.id }.distinct().size)
            assertEquals(1, question.options.count { it.isCorrect })
        }
    }

    @Test
    fun `random round supports an odd requested count without duplicate targets`() {
        val questions = QuizGenerator(Random(7)).createRound(
            mode = QuizMode.Random,
            brands = brands,
            randomQuestionCount = 11,
        )

        assertEquals(11, questions.size)
        assertEquals(
            questions.size,
            questions.map { "${it.type}|${it.targetBrandId}|${it.targetCarId}" }.distinct().size,
        )
    }

    @Test
    fun `random round supports the configured maximum`() {
        val questions = QuizGenerator(Random(9)).createRound(
            mode = QuizMode.Random,
            brands = brands,
            randomQuestionCount = 50,
        )

        assertEquals(50, questions.size)
        assertTrue(questions.all { question ->
            question.options.size == 4 && question.options.count { option -> option.isCorrect } == 1
        })
    }

    @Test
    fun `random round does not force a balanced question type split`() {
        val questions = QuizGenerator(ZeroRandom()).createRound(
            mode = QuizMode.Random,
            brands = brands,
            randomQuestionCount = 50,
        )

        assertEquals(50, questions.count { it.type == QuestionType.BrandToModel })
    }

    @Test
    fun `brand practice asks only about the selected brand`() {
        val selectedBrandId = "brand-3"
        val questions = QuizGenerator(Random(19)).createRound(
            mode = QuizMode.BrandPractice(selectedBrandId),
            brands = brands,
        )

        assertTrue(questions.all { it.targetBrandId == selectedBrandId })
    }

    @Test
    fun `random round ignores an empty custom brand`() {
        val brandsWithEmptyCustomBrand = brands + Brand(
            id = "custom-empty",
            nameZh = "空品牌",
            nameEn = "Empty brand",
            cars = emptyList(),
            isBuiltIn = false,
        )

        val questions = QuizGenerator(Random(23)).createRound(
            mode = QuizMode.Random,
            brands = brandsWithEmptyCustomBrand,
        )

        assertEquals(10, questions.size)
        assertTrue(questions.none { it.targetBrandId == "custom-empty" })
    }

    @Test
    fun `brand practice works when another brand has no cars`() {
        val brandsWithEmptyCustomBrand = brands + Brand(
            id = "custom-empty",
            nameZh = "空品牌",
            nameEn = "Empty brand",
            cars = emptyList(),
            isBuiltIn = false,
        )

        val questions = QuizGenerator(Random(29)).createRound(
            mode = QuizMode.BrandPractice("brand-3"),
            brands = brandsWithEmptyCustomBrand,
        )

        assertTrue(questions.isNotEmpty())
        assertTrue(questions.all { it.targetBrandId == "brand-3" })
    }

    @Test
    fun `question prompt wraps english brand name and options have unique labels`() {
        val questions = QuizGenerator(Random(2)).createRound(QuizMode.Random, brands)
        val brandQuestion = questions.first { it.type == QuestionType.BrandToModel }

        assertTrue(brandQuestion.prompt.contains("品牌"))
        assertTrue(brandQuestion.prompt.contains("("))
        assertEquals(4, brandQuestion.options.map { it.displayText() }.distinct().size)
        assertTrue(brandQuestion.options.first().letteredText(0).startsWith("A. "))
    }

    @Test
    fun `model question only shows a repeated Chinese and English model name once`() {
        val crvBrands = brands.mapIndexed { index, brand ->
            if (index == 0) {
                brand.copy(cars = brand.cars.mapIndexed { carIndex, car ->
                    if (carIndex == 0) car.copy(nameZh = "CR-V", nameEn = "CR-V") else car
                })
            } else {
                brand
            }
        }

        val question = QuizGenerator(Random(5)).createRound(
            mode = QuizMode.BrandPractice("brand-1"),
            brands = crvBrands,
        ).first { it.type == QuestionType.ModelToBrand && it.targetCarId == "car-1-1" }

        assertEquals("CR-V 属于哪个品牌？", question.prompt)
    }

    @Test
    fun `round does not repeat a prompt and correct answer pair`() {
        val smallBrands = brands.map { brand -> brand.copy(cars = brand.cars.take(2)) }

        val questions = QuizGenerator(Random(31)).createRound(QuizMode.BrandPractice("brand-1"), smallBrands)
        val uniquenessKeys = questions.map { question ->
            "${question.prompt}|${question.options.single { it.isCorrect }.id}"
        }

        assertEquals(uniquenessKeys.size, uniquenessKeys.distinct().size)
        assertEquals(4, questions.size)
    }

    private class ZeroRandom : Random() {
        override fun nextBits(bitCount: Int) = 0
    }
}
