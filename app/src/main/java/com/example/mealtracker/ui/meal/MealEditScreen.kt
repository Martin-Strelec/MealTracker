package com.example.mealtracker.ui.meal

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mealtracker.R
import com.example.mealtracker.ui.AppViewModelProvider
import com.example.mealtracker.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch


/**
 * Navigation destination for Editing a Meal.
 * Requires 'itemId' argument.
 */
object EditMealDestination : NavigationDestination {
    override val route = "edit_meal"
    override val titleRes = R.string.edit_meal
    override val icon = Icons.Filled.Edit
    override val showInDrawer = false
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"

}

/**
 * Composable screen for editing an existing meal.
 * Pre-fills the form with existing data and updates the database on save.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMealScreen(
    navigateBack: () -> Unit,
    onCameraClick: () -> Unit,
    modifier: Modifier = Modifier,
    cameraImageUri: String? = null,
    viewModel: MealEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()

    // Handle image capture result from CameraScreen
    LaunchedEffect(cameraImageUri) {
        if(cameraImageUri != null) {
            viewModel.updateUiState(viewModel.mealUiState.mealDetails.copy(image = cameraImageUri))
        }
    }

    Scaffold { innerPadding ->
        // Reuses the MealEntryBody form used in AddMealScreen
        MealEntryBody(
            mealUiState = viewModel.mealUiState,
            onItemValueChange = viewModel::updateUiState,
            onSaveClick = {
                // Note: If the user rotates the screen very fast, the operation may get cancelled
                // and the item may not be updated in the Database. This is because when config
                // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                // be cancelled - since the scope is bound to composition.
                coroutineScope.launch {
                    viewModel.updateItem()
                    navigateBack()
                }
            },
            onCameraClick = onCameraClick,
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                )
                .verticalScroll(rememberScrollState())
        )
    }
}