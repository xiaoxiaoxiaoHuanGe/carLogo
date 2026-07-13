package com.example.carlogo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "brands")
data class BrandEntity(
    @PrimaryKey val id: String,
    val nameZh: String,
    val nameEn: String,
    val category: String,
    val isBuiltIn: Boolean,
    val parentBrand: String? = null,
)

@Entity(tableName = "cars")
data class CarEntity(@PrimaryKey val id: String, val brandId: String, val nameZh: String, val nameEn: String, val imageRef: String?, val isBuiltIn: Boolean)

@Entity(tableName = "quiz_sessions")
data class QuizSessionEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val mode: String, val brandId: String?, val totalCount: Int, val correctCount: Int, val startedAt: Long, val finishedAt: Long?)

@Entity(tableName = "answers")
data class AnswerEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val sessionId: Long, val questionType: String, val brandId: String, val carId: String, val selectedOptionId: String, val isCorrect: Boolean, val answeredAt: Long)

@Entity(tableName = "mistakes", primaryKeys = ["brandId", "carId", "questionType"])
data class MistakeEntity(val brandId: String, val carId: String, val questionType: String, val wrongCount: Int, val lastWrongAt: Long)
