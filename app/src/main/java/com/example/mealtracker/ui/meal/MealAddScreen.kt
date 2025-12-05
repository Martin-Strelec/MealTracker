package com.example.mealtracker.ui.meal

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mealtracker.R
import com.example.mealtracker.ui.AppViewModelProvider
import com.example.mealtracker.ui.navigation.NavigationDestination
import com.example.mealtracker.ui.theme.AppTheme
import kotlinx.coroutines.launch

/**
 * Navigation destination for the Add Meal screen.
 */
object AddMealDestination : NavigationDestination {
    override val route = "add_meal"
    override val titleRes = R.string.add_meal
    override val icon = Icons.Filled.Add
    override val showInDrawer = false
}

/**
 * Composable screen for adding a new meal.
 * Handles user input, image selection (Gallery/Camera), and saving the data.
 *
 * @param navigateBack Callback to return to the previous screen.
 * @param onCameraClick Callback to navigate to the custom CameraScreen.
 * @param cameraImageUri The URI of the image returned from the CameraScreen (if any).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealScreen(
    navigateBack: () -> Unit,
    onCameraClick: () -> Unit,
    cameraImageUri: String?,
    modifier: Modifier = Modifier,
    viewModel: AddMealViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    // Listen for a result from the CameraScreen.
    // If a photo was taken, update the ViewModel state with the new URI.
    LaunchedEffect(cameraImageUri) {
        if (cameraImageUri != null) {
            viewModel.updateUiState(viewModel.mealUiState.mealDetails.copy(image = cameraImageUri))
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Scope required to run the suspend function 'saveMeal'
        val coroutineScope = rememberCoroutineScope()

        Scaffold { innerPadding ->
            MealEntryBody(
                mealUiState = viewModel.mealUiState,
                onItemValueChange = viewModel::updateUiState,
                onSaveClick = {
                    // Note: If the user rotates the screen very fast, the operation may get cancelled
                    // and the item may not be saved in the Database. This is because when config
                    // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                    // be cancelled - since the scope is bound to composition.
                    coroutineScope.launch {
                        viewModel.saveMeal()
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
                    .verticalScroll(rememberScrollState()) // Allow scrolling for smaller screens
                    .fillMaxWidth()
            )
        }
    }
}

/**
 * Body composable containing the input form and the Save button.
 */
@Composable
fun MealEntryBody(
    mealUiState: MealUiState,
    onItemValueChange: (MealDetails) -> Unit,
    onSaveClick: () -> Unit,
    onCameraClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(AppTheme.dimens.paddingMedium),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.paddingLarge)
    ) {
        // Input fields for meal details
        MealInputForm(
            mealDetails = mealUiState.mealDetails,
            onValueChange = onItemValueChange,
            onCameraClick = onCameraClick,
            modifier = Modifier.fillMaxWidth()
        )
        // Save Button - enabled only if input is valid
        Button(
            onClick = onSaveClick,
            enabled = mealUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_action))
        }
    }
}

/**
 * Form composable with text fields and image picker logic.
 */
@Composable
fun MealInputForm(
    mealDetails: MealDetails,
    modifier: Modifier = Modifier,
    onValueChange: (MealDetails) -> Unit = {},
    onCameraClick: () -> Unit,
    enabled: Boolean = true
) {
    val context = LocalContext.current

    // Launcher for the Android Photo Picker (Visual Media).
    // This allows the user to select images from their gallery without needing full storage permissions.
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                // IMPORTANT: Persist permissions to access this URI later (e.g., after app restart).
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(it, flag)

                onValueChange(mealDetails.copy(image = it.toString()))
            }
        }
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.paddingMedium)
    ) {
        // --- Image Selection Area ---
        if (mealDetails.image.isNotBlank()) {
            // State: Image Selected - Show image and edit buttons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppTheme.dimens.inputImageHeight),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = mealDetails.image,
                    contentDescription = stringResource(R.string.meal_image),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
                // Floating overlay buttons to change the image
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(AppTheme.dimens.paddingSmall),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.paddingSmall)
                ) {
                    androidx.compose.material3.FilledTonalIconButton(onClick = {
                        // Launch Gallery Picker
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }) {
                        Icon(Icons.Default.Image, contentDescription = stringResource(R.string.gallery))
                    }
                    androidx.compose.material3.FilledTonalIconButton(onClick = onCameraClick) {
                        // Launch Custom Camera Screen
                        Icon(Icons.Default.CameraAlt, contentDescription = stringResource(R.string.camera))
                    }
                }
            }
        } else {
            // State: No Image - Show placeholder with buttons
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppTheme.dimens.placeholderImageHeight)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.add_photo), style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(AppTheme.dimens.spacerMedium))
                    Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.paddingMedium)) {
                        Button(onClick = {
                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }) {
                            Icon(Icons.Default.Image, contentDescription = null)
                            Spacer(Modifier.width(AppTheme.dimens.paddingSmall))
                            Text(stringResource(R.string.gallery))
                        }
                        Button(onClick = onCameraClick) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Spacer(Modifier.width(AppTheme.dimens.paddingSmall))
                            Text(stringResource(R.string.camera))
                        }
                    }
                }
            }
        }

        // --- Text Fields ---
        TextField(
            value = mealDetails.name,
            onValueChange = { onValueChange(mealDetails.copy(name = it)) },
            label = { Text(stringResource(R.string.meal_name_req)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        TextField(
            value = mealDetails.description,
            onValueChange = { onValueChange(mealDetails.copy(description = it)) },
            label = { Text(stringResource(R.string.meal_desc_req)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            ),
            modifier = Modifier
                .fillMaxWidth(),
            enabled = enabled,
            singleLine = false
        )
        TextField(
            value = mealDetails.calories.toString(),
            // Safe conversion to Int, defaults to 0 if empty/invalid
            onValueChange = { onValueChange(mealDetails.copy(calories = it.toIntOrNull() ?: 0)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(R.string.calories_req)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
    }
}