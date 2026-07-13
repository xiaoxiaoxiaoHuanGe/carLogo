package com.example.carlogo.data

import android.util.Log
import androidx.room.withTransaction
import com.example.carlogo.data.local.AppDatabase
import com.example.carlogo.data.local.BrandEntity
import com.example.carlogo.data.local.CarEntity
import com.example.carlogo.data.local.MistakeEntity
import com.example.carlogo.data.local.QuizSessionEntity
import com.example.carlogo.domain.model.Brand
import com.example.carlogo.domain.model.CarModel
import java.util.UUID

class CarRepository(private val database: AppDatabase) {
    suspend fun loadBrands(): List<Brand> {
        ensureSeeded()
        val carsByBrand = database.carDao().getAll().groupBy { it.brandId }
        return database.brandDao().getAll().map { brand ->
            Brand(
                id = brand.id,
                nameZh = brand.nameZh,
                nameEn = brand.nameEn,
                category = brand.category,
                cars = carsByBrand[brand.id].orEmpty().map { it.toDomain() },
                isBuiltIn = brand.isBuiltIn,
                parentBrand = brand.parentBrand,
            )
        }
    }

    suspend fun addBrand(nameZh: String, nameEn: String, category: String): String {
        require(nameZh.isNotBlank()) { "品牌中文名不能为空。" }
        val id = "custom-${UUID.randomUUID()}"
        database.brandDao().upsert(BrandEntity(id, nameZh.trim(), nameEn.trim(), category.ifBlank { "自定义" }, false))
        return id
    }

    suspend fun addCar(brandId: String, nameZh: String, nameEn: String, imageRef: String?): String {
        require(nameZh.isNotBlank()) { "车型中文名不能为空。" }
        val id = "custom-${UUID.randomUUID()}"
        database.carDao().upsert(CarEntity(id, brandId, nameZh.trim(), nameEn.trim(), imageRef, false))
        return id
    }

    suspend fun updateCustomBrand(id: String, nameZh: String, nameEn: String, category: String) {
        require(nameZh.isNotBlank()) { "品牌中文名不能为空。" }
        database.brandDao().upsert(BrandEntity(id, nameZh.trim(), nameEn.trim(), category.ifBlank { "自定义" }, false))
    }

    suspend fun updateCustomCar(id: String, brandId: String, nameZh: String, nameEn: String, imageRef: String?) {
        require(nameZh.isNotBlank()) { "车型中文名不能为空。" }
        database.carDao().upsert(CarEntity(id, brandId, nameZh.trim(), nameEn.trim(), imageRef, false))
    }

    suspend fun deleteCustomBrand(id: String) {
        database.carDao().deleteCustomByBrandId(id)
        database.brandDao().deleteCustom(id)
    }

    suspend fun deleteCustomCar(id: String) = database.carDao().deleteCustom(id)

    suspend fun startSession(mode: String, brandId: String?, totalCount: Int): Long = database.quizDao().insertSession(
        QuizSessionEntity(mode = mode, brandId = brandId, totalCount = totalCount, correctCount = 0, startedAt = System.currentTimeMillis(), finishedAt = null),
    )

    suspend fun finishSession(sessionId: Long, correctCount: Int) = database.quizDao().finishSession(sessionId, correctCount, System.currentTimeMillis())

    suspend fun history() = database.quizDao().getHistory()

    suspend fun mistakes() = database.quizDao().getMistakes()

    suspend fun recordMistake(brandId: String, carId: String, questionType: String) {
        val previous = database.quizDao().getMistakes().firstOrNull { it.brandId == brandId && it.carId == carId && it.questionType == questionType }
        database.quizDao().upsertMistake(MistakeEntity(brandId, carId, questionType, (previous?.wrongCount ?: 0) + 1, System.currentTimeMillis()))
    }

    /** Replaces only bundled content; custom brands and models are never deleted. */
    private suspend fun ensureSeeded() {
        runCatching {
            database.withTransaction {
                // Delete bundled rows first so retired built-ins cannot remain in the quiz pool.
                database.carDao().deleteAllBuiltIn()
                database.brandDao().deleteAllBuiltIn()
                database.brandDao().upsertAll(SeedData.brands.map {
                    BrandEntity(it.id, it.nameZh, it.nameEn, it.category, true, it.parentBrand)
                })
                database.carDao().upsertAll(SeedData.brands.flatMap { brand ->
                    brand.cars.map { CarEntity(it.id, it.brandId, it.nameZh, it.nameEn, it.imageRef, true) }
                })
            }
        }.onFailure { Log.e(TAG, "Failed to seed local question bank", it) }
    }

    private fun CarEntity.toDomain() = CarModel(id, brandId, nameZh, nameEn, imageRef, isBuiltIn)

    private companion object { const val TAG = "CarRepository" }
}
