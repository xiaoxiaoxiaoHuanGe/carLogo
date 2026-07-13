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
    fun `random round contains twenty balanced four-option questions`() {
        val questions = QuizGenerator(Random(7)).createRound(QuizMode.Random, brands)

        assertEquals(20, questions.size)
        assertEquals(10, questions.count { it.type == QuestionType.BrandToModel })
        assertEquals(10, questions.count { it.type == QuestionType.ModelToBrand })
        questions.forEach { question ->
            assertEquals(4, question.options.size)
            assertEquals(4, question.options.map { it.id }.distinct().size)
            assertEquals(1, question.options.count { it.isCorrect })
        }
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
}
