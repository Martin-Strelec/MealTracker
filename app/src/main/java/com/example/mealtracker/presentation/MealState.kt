package com.example.mealtracker.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.mealtracker.data.Meal

data class MealState(
    val meals: List<Meal> = emptyList(),
    val name: MutableState<String> = mutableStateOf(""),
    val image: MutableState<String> = mutableStateOf(""),
    val calories: MutableState<String> = mutableStateOf(""),
    val description: MutableState<String> = mutableStateOf(""),
    val ingredients: MutableState<String> = mutableStateOf(""),
    val instructions: MutableState<String> = mutableStateOf("")
)
