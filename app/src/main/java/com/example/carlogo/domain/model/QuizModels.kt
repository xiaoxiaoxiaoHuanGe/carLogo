package com.example.carlogo.domain.model

/** Brand together with the models used to build quiz questions. */
data class Brand(
    val id: String,
    val nameZh: String,
    val nameEn: String,
    val category: String = "传统燃油",
    val cars: List<CarModel>,
    val isBuiltIn: Boolean = true,
    /** Optional parent-group label, for example "比亚迪旗下". */
    val parentBrand: String? = null,
)

/** A car model may have either a bundled drawable reference or a local image path. */
data class CarModel(
    val id: String,
    val brandId: String,
    val nameZh: String,
    val nameEn: String,
    val imageRef: String? = null,
    val isBuiltIn: Boolean = true,
)

sealed interface QuizMode {
    data object Random : QuizMode
    data class BrandPractice(val brandId: String) : QuizMode
}

enum class QuestionType { BrandToModel, ModelToBrand }

data class QuizOption(
    val id: String,
    val labelZh: String,
    val labelEn: String,
    val imageRef: String? = null,
    val isCorrect: Boolean,
)

fun Brand.displayText(): String {
    val bilingualName = if (nameEn.isBlank() || nameZh.equals(nameEn, ignoreCase = true)) nameZh else "$nameZh ($nameEn)"
    return parentBrand?.takeIf { it.isNotBlank() }?.let { "$bilingualName · $it" } ?: bilingualName
}

/** Keeps bilingual vehicle names readable when both fields contain the same value. */
fun CarModel.displayText(): String = if (nameEn.isBlank() || nameZh.equals(nameEn, ignoreCase = true)) nameZh else "$nameZh ($nameEn)"

fun QuizOption.displayText(): String = if (labelEn.isBlank() || labelZh.equals(labelEn, ignoreCase = true)) labelZh else "$labelZh ($labelEn)"

fun QuizOption.letteredText(index: Int): String = "${('A'.code + index).toChar()}. ${displayText()}"

data class Question(
    val id: String,
    val type: QuestionType,
    val prompt: String,
    val targetBrandId: String,
    val targetCarId: String,
    val options: List<QuizOption>,
    val imageRef: String? = null,
)
