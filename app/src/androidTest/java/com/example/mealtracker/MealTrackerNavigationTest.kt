package com.example.mealtracker

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.rule.GrantPermissionRule
import com.example.mealtracker.ui.navigation.MealTrackerNavHost
import com.example.mealtracker.ui.theme.AppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MealTrackerNavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.CAMERA
    )

    private lateinit var navController: NavHostController

    @Before
    fun setupMealTrackerNavHost() {
        composeTestRule.setContent {
            navController = rememberNavController()

            AppTheme {
                MealTrackerApp(navController = navController)
            }
        }
    }

    @Test
    fun mealTracker_verifyStartDestination() {
        composeTestRule
            .onNode(
                hasText("Meals")
                        and !hasClickAction())
            .assertIsDisplayed()
    }

    @Test
    fun mealTracker_verifyDrawerNavigation() {
        composeTestRule
            .onNodeWithContentDescription("Menu")
            .performClick()
        composeTestRule
            .onNode(
                hasText("Tracking")
            and hasClickAction())
            .performClick()
        composeTestRule
            .onNode(
                hasText("Tracking")
                        and !hasClickAction())
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription("Menu")
            .performClick()
        composeTestRule
            .onNode(
                hasText("Favourites")
                        and hasClickAction())
            .performClick()
        composeTestRule
            .onNode(
                hasText("Favourites")
                        and !hasClickAction())
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription("Menu")
            .performClick()
        composeTestRule
            .onNode(
                hasText("Tracking")
                        and hasClickAction())
            .performClick()
        composeTestRule
            .onNode(
                hasText("Tracking")
                        and !hasClickAction())
            .assertIsDisplayed()
    }
}