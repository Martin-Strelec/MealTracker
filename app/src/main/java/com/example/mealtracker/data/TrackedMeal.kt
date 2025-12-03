package com.example.mealtracker.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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

data class TrackedMeal (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val mealId: Int,
    val dateConsumed: Long
)