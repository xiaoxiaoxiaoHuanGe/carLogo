package com.example.carlogo.ui

import com.example.carlogo.data.local.MistakeEntity
import com.example.carlogo.domain.model.Brand
import com.example.carlogo.domain.model.CarModel
import com.example.carlogo.domain.model.QuestionType

internal data class MistakePresentation(
    val key: String,
    val brandId: String,
    val brandName: String,
    val listTitle: String,
    val questionTypeLabel: String,
    val question: String,
    val correctAnswer: String,
    val wrongCount: Int,
)

internal object MistakePresentationMapper {
    fun from(mistake: MistakeEntity, brands: List<Brand>): MistakePresentation? {
        val brand = brands.firstOrNull { it.id == mistake.brandId } ?: return null
        val car = brand.cars.firstOrNull { it.id == mistake.carId } ?: return null
        val questionType = QuestionType.entries.firstOrNull { it.name == mistake.questionType } ?: return null
        val brandName = brand.displayName()
        val carName = car.displayName()

        return when (questionType) {
            QuestionType.ModelToBrand -> MistakePresentation(
                key = "${mistake.brandId}|${mistake.carId}|${mistake.questionType}",
                brandId = brand.id,
                brandName = brandName,
                listTitle = carName,
                questionTypeLabel = "看车型猜品牌",
                question = "$carName 属于哪个品牌？",
                correctAnswer = brandName,
                wrongCount = mistake.wrongCount,
            )
            QuestionType.BrandToModel -> MistakePresentation(
                key = "${mistake.brandId}|${mistake.carId}|${mistake.questionType}",
                brandId = brand.id,
                brandName = brandName,
                listTitle = brandName,
                questionTypeLabel = "看品牌选车型",
                question = "$brandName 旗下有哪款车型？",
                correctAnswer = carName,
                wrongCount = mistake.wrongCount,
            )
        }
    }

    private fun Brand.displayName(): String = nameZh.ifBlank { nameEn }

    private fun CarModel.displayName(): String = nameZh.ifBlank { nameEn }
}
