package com.example.mealtracker.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// Tracked_meals table
@Entity(
    tableName = "tracked_meals",
    foreignKeys = [
        ForeignKey(
            entity = Meal::class,
            parentColumns = ["id"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

// Tracked meal entity
data class TrackedMeal (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val mealId: Int,
    val dateConsumed: Long
)