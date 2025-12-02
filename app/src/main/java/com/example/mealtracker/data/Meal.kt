package com.example.mealtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Meal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val image: String,
    val description: String,
    val calories: Int,
    val dateAdded: Long,
    val isFavourite: Boolean,
    val isTracked: Boolean
)
