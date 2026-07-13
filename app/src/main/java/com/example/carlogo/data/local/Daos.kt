package com.example.carlogo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BrandDao {
    @Query("SELECT COUNT(*) FROM brands") suspend fun count(): Int
    @Query("SELECT * FROM brands ORDER BY nameZh") suspend fun getAll(): List<BrandEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertAll(items: List<BrandEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(item: BrandEntity)
    @Query("DELETE FROM brands WHERE isBuiltIn = 1") suspend fun deleteAllBuiltIn()
    @Query("DELETE FROM brands WHERE id = :id AND isBuiltIn = 0") suspend fun deleteCustom(id: String)
}

@Dao
interface CarDao {
    @Query("SELECT * FROM cars") suspend fun getAll(): List<CarEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertAll(items: List<CarEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(item: CarEntity)
    @Query("DELETE FROM cars WHERE isBuiltIn = 1") suspend fun deleteAllBuiltIn()
    @Query("DELETE FROM cars WHERE id = :id AND isBuiltIn = 0") suspend fun deleteCustom(id: String)
    @Query("DELETE FROM cars WHERE brandId = :brandId AND isBuiltIn = 0") suspend fun deleteCustomByBrandId(brandId: String)
}

@Dao
interface QuizDao {
    @Insert suspend fun insertSession(item: QuizSessionEntity): Long
    @Insert suspend fun insertAnswer(item: AnswerEntity)
    @Query("SELECT * FROM quiz_sessions ORDER BY startedAt DESC") suspend fun getHistory(): List<QuizSessionEntity>
    @Query("SELECT * FROM mistakes ORDER BY lastWrongAt DESC") suspend fun getMistakes(): List<MistakeEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertMistake(item: MistakeEntity)
    @Query("UPDATE quiz_sessions SET correctCount = :correctCount, finishedAt = :finishedAt WHERE id = :id") suspend fun finishSession(id: Long, correctCount: Int, finishedAt: Long)
}
