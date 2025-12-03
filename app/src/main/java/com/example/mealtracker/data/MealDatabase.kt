package com.example.mealtracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Meal::class, TrackedMeal::class],
    version = 2,
    exportSchema = false)
abstract class MealDatabase : RoomDatabase() {
    abstract fun mealDao(): MealsDao

    companion object {
        @Volatile
        private var Instance: MealDatabase? = null

        fun getDatabase(context: Context): MealDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, MealDatabase::class.java, "meal_database")
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}