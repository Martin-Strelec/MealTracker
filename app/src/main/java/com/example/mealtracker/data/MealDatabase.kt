package com.example.mealtracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Meal::class],
    version = 1,
    exportSchema = false
)
abstract class MealDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDAO
    companion object {
        @Volatile
        private var Instance: MealDatabase? = null

        fun getDatabase(context: Context): MealDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, MealDatabase::class.java, "meal_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}