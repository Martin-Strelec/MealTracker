package com.example.mealtracker.presentation

import com.example.mealtracker.data.Meal

sealed interface MealEvents {
    object GetMeals: MealEvents

    data class DeleteMeal(val meal: Meal): MealEvents

    data class SaveMeal(
        val name: String,
        val image: String,
        val calories: String,
        val description: String,
        val ingredients: String,
        val instructions: String,
        val date: Long
    ): MealEvents
}