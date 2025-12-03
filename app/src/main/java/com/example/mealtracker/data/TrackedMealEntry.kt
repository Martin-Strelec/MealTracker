package com.example.mealtracker.data

import androidx.room.Embedded

data class TrackedMealEntry (
    @Embedded val meal: Meal,
    val trackId: Int,
    val dateConsumed: Long
)

