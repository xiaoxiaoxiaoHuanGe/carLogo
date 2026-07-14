package com.example.carlogo.domain

import com.example.carlogo.domain.model.Brand
import com.example.carlogo.domain.model.CarModel
import com.example.carlogo.domain.model.Question
import com.example.carlogo.domain.model.QuestionType
import com.example.carlogo.domain.model.QuizMode
import com.example.carlogo.domain.model.QuizOption
import com.example.carlogo.domain.model.displayText
import kotlin.random.Random

/**
 * Creates an in-memory round. Keeping this class free of Android and database code
 * makes the randomisation rules deterministic and inexpensive to unit-test.
 */
class QuizGenerator(private val random: Random = Random.Default) {

    fun createRound(
        mode: QuizMode,
        brands: List<Brand>,
        randomQuestionCount: Int = RandomPracticeConfig.DEFAULT_QUESTION_COUNT,
    ): List<Question> {
        require(brands.size >= OPTION_COUNT) { "题库至少需要 4 个品牌才能生成四选一题目。" }
        require(brands.all { it.cars.isNotEmpty() }) { "每个参与出题的品牌至少需要 1 款车型。" }

        val targetBrands = when (mode) {
            QuizMode.Random -> brands
            is QuizMode.BrandPractice -> listOf(
                brands.firstOrNull { it.id == mode.brandId }
                    ?: throw IllegalArgumentException("未找到品牌专项练习所需的品牌：${mode.brandId}"),
            )
        }

        // Build unique brand-car targets first. Sampling from this set prevents a repeated
        // question stem and correct answer from being kept only because its distractors differ.
        val uniqueTargets = targetBrands
            .flatMap { brand -> brand.cars.map { car -> brand to car } }
            .distinctBy { (brand, car) -> "${brand.displayText()}|${car.displayText()}" }
        val questionSpecs = when (mode) {
            QuizMode.Random -> {
                val requestedCount = RandomPracticeConfig.validateQuestionCount(randomQuestionCount)
                val candidates = uniqueTargets.flatMap { target ->
                    QuestionType.entries.map { type -> type to target }
                }
                val unusedCandidates = candidates.toMutableList()
                List(requestedCount) {
                    val questionType = QuestionType.entries.random(random)
                    val typeCandidates = unusedCandidates.filter { (type, _) -> type == questionType }
                        .ifEmpty { candidates.filter { (type, _) -> type == questionType } }
                    val selectedCandidate = typeCandidates.random(random)
                    unusedCandidates.remove(selectedCandidate)
                    selectedCandidate
                }
            }
            is QuizMode.BrandPractice -> {
                val questionsPerType = minOf(QUESTIONS_PER_TYPE, uniqueTargets.size)
                (
                    uniqueTargets.shuffled(random).take(questionsPerType).map { QuestionType.BrandToModel to it } +
                        uniqueTargets.shuffled(random).take(questionsPerType).map { QuestionType.ModelToBrand to it }
                    ).shuffled(random)
            }
        }

        return questionSpecs.mapIndexed { index, (type, target) ->
            val (targetBrand, targetCar) = target
            when (type) {
                QuestionType.BrandToModel -> createBrandToModelQuestion(
                    index = index,
                    targetBrand = targetBrand,
                    targetCar = targetCar,
                    allBrands = brands,
                )
                QuestionType.ModelToBrand -> createModelToBrandQuestion(
                    index = index,
                    targetBrand = targetBrand,
                    targetCar = targetCar,
                    allBrands = brands,
                )
            }
        }
    }

    private fun createBrandToModelQuestion(
        index: Int,
        targetBrand: Brand,
        targetCar: CarModel,
        allBrands: List<Brand>,
    ): Question {
        val distractors = allBrands
            .asSequence()
            .filter { it.id != targetBrand.id }
            .flatMap { it.cars.asSequence() }
            .filter { it.nameZh != targetCar.nameZh || it.nameEn != targetCar.nameEn }
            .distinctBy { "${it.nameZh}|${it.nameEn}" }
            .shuffled(random)
            .take(DISTRACTOR_COUNT)
            .toList()
        require(distractors.size == DISTRACTOR_COUNT) { "其他品牌车型不足，无法生成干扰项。" }

        val options = (listOf(targetCar.toOption(isCorrect = true)) +
            distractors.map { it.toOption(isCorrect = false) }).shuffled(random)
        return Question(
            id = "question-$index",
            type = QuestionType.BrandToModel,
            prompt = "下列哪个车型属于${targetBrand.displayText()}？",
            targetBrandId = targetBrand.id,
            targetCarId = targetCar.id,
            options = options,
            imageRef = targetCar.imageRef,
        )
    }

    private fun createModelToBrandQuestion(
        index: Int,
        targetBrand: Brand,
        targetCar: CarModel,
        allBrands: List<Brand>,
    ): Question {
        val distractors = allBrands.filter { it.id != targetBrand.id }
            .shuffled(random)
            .take(DISTRACTOR_COUNT)
        require(distractors.size == DISTRACTOR_COUNT) { "其他品牌不足，无法生成干扰项。" }

        val options = (listOf(targetBrand.toOption(isCorrect = true)) +
            distractors.map { it.toOption(isCorrect = false) }).shuffled(random)
        return Question(
            id = "question-$index",
            type = QuestionType.ModelToBrand,
            prompt = "${targetCar.displayText()} 属于哪个品牌？",
            targetBrandId = targetBrand.id,
            targetCarId = targetCar.id,
            options = options,
            imageRef = targetCar.imageRef,
        )
    }

    private fun CarModel.toOption(isCorrect: Boolean) = QuizOption(
        id = id,
        labelZh = nameZh,
        labelEn = nameEn,
        imageRef = imageRef,
        isCorrect = isCorrect,
    )

    private fun Brand.toOption(isCorrect: Boolean) = QuizOption(
        id = id,
        labelZh = nameZh,
        labelEn = nameEn,
        isCorrect = isCorrect,
    )

    private companion object {
        const val QUESTIONS_PER_TYPE = 10
        const val OPTION_COUNT = 4
        const val DISTRACTOR_COUNT = OPTION_COUNT - 1
    }
}
