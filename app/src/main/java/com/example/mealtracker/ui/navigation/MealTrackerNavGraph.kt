package com.example.mealtracker.ui.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

fun MealTrackerNavHost(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController, startDestination = HomeDestination.route, modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(navigateToAddMeal = { navController.navigate(AddMealDestination.route) },
                navigateToMealUpdate = {
                    navController.navigate("${MealUpdateDestination.route}/$it")
                })
        }
        composable(route = AddMealDestination.route) {
            AddMealScreen(navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp()
                })
        }
        composable(
            route = MealUpdateDestination.routeWithArgs,
            arguments = listOf(navArgument(MealDetailsDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            MealDetailsScreen(
                navigateToEditMeal = {
                    navController.navigate("${MealUpdateDestination.route}/$it")
                },
                navigateBack = { navController.navigateUp() }
            )
        }
        composable(
            route = MealUpdateDestination.routeWithArgs,
            arguments = listOf(navArgument(MealUpdateDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            EditMealScreen(navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }
    }
}