package com.example.mealtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val image: String,
    val calories: String,
    val description: String,
    val ingredients: String,
    val instructions: String,
    val date: String
)
