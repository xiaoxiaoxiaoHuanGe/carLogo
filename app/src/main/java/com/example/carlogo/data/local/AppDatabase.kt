package com.example.carlogo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [BrandEntity::class, CarEntity::class, QuizSessionEntity::class, AnswerEntity::class, MistakeEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun brandDao(): BrandDao
    abstract fun carDao(): CarDao
    abstract fun quizDao(): QuizDao

    companion object {
        /** Keeps existing users' custom content while adding parent-brand labels. */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE brands ADD COLUMN parentBrand TEXT")
            }
        }
    }
}
