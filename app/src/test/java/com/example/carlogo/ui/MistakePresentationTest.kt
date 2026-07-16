package com.example.carlogo.ui

import com.example.carlogo.data.local.MistakeEntity
import com.example.carlogo.domain.model.Brand
import com.example.carlogo.domain.model.CarModel
import com.example.carlogo.domain.model.QuestionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MistakePresentationTest {

    private val cadillac = Brand(
        id = "cadillac",
        nameZh = "凯迪拉克",
        nameEn = "",
        cars = listOf(CarModel(id = "ct4", brandId = "cadillac", nameZh = "CT4", nameEn = "")),
    )

    @Test
    fun `model to brand mistake shows the car question and brand answer`() {
        val presentation = MistakePresentationMapper.from(
            mistake = MistakeEntity("cadillac", "ct4", QuestionType.ModelToBrand.name, wrongCount = 2, lastWrongAt = 1L),
            brands = listOf(cadillac),
        )

        requireNotNull(presentation)
        assertEquals("cadillac", presentation.brandId)
        assertEquals("凯迪拉克", presentation.brandName)
        assertEquals("CT4", presentation.listTitle)
        assertEquals("看车型猜品牌", presentation.questionTypeLabel)
        assertEquals("CT4 属于哪个品牌？", presentation.question)
        assertEquals("凯迪拉克", presentation.correctAnswer)
        assertEquals(2, presentation.wrongCount)
    }

    @Test
    fun `brand to model mistake shows the brand question and car answer`() {
        val presentation = MistakePresentationMapper.from(
            mistake = MistakeEntity("cadillac", "ct4", QuestionType.BrandToModel.name, wrongCount = 1, lastWrongAt = 1L),
            brands = listOf(cadillac),
        )

        requireNotNull(presentation)
        assertEquals("cadillac", presentation.brandId)
        assertEquals("凯迪拉克", presentation.brandName)
        assertEquals("凯迪拉克", presentation.listTitle)
        assertEquals("看品牌选车型", presentation.questionTypeLabel)
        assertEquals("凯迪拉克 旗下有哪款车型？", presentation.question)
        assertEquals("CT4", presentation.correctAnswer)
    }

    @Test
    fun `returns no presentation when a saved mistake cannot resolve its source`() {
        val presentation = MistakePresentationMapper.from(
            mistake = MistakeEntity("missing", "missing-car", QuestionType.ModelToBrand.name, wrongCount = 1, lastWrongAt = 1L),
            brands = listOf(cadillac),
        )

        assertNull(presentation)
    }
}
