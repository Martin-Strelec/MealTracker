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

object AddMealDestination : NavigationDestination {
    override val route = "add_meal"
    override val titleRes = R.string.add_meal
    override val icon = Icons.Filled.Add
    override val showInDrawer = false
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealScreen(
    navigateBack: () -> Unit,
    onCameraClick: () -> Unit,
    cameraImageUri: String?,
    modifier: Modifier = Modifier,
    viewModel: AddMealViewModel = viewModel(factory = AppViewModelProvider.Factory)
    ) {

    LaunchedEffect(cameraImageUri) {
        if (cameraImageUri != null) {
            viewModel.updateUiState(viewModel.mealUiState.mealDetails.copy(image = cameraImageUri))
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
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
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            )
        }
    }
}

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
        MealInputForm(
            mealDetails = mealUiState.mealDetails,
            onValueChange = onItemValueChange,
            onCameraClick = onCameraClick,
            modifier = Modifier.fillMaxWidth()
        )
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

@Composable
fun MealInputForm(
    mealDetails: MealDetails,
    modifier: Modifier = Modifier,
    onValueChange: (MealDetails) -> Unit = {},
    onCameraClick: () -> Unit,
    enabled: Boolean = true
) {
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
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
            // Show selected image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppTheme.dimens.inputImageHeight),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = mealDetails.image,
                    contentDescription = "Meal Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Option to change image (Floating overlay)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(AppTheme.dimens.paddingSmall),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.paddingSmall)
                ) {
                    androidx.compose.material3.FilledTonalIconButton(onClick = {
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }) {
                        Icon(Icons.Default.Image, contentDescription = "Gallery")
                    }
                    androidx.compose.material3.FilledTonalIconButton(onClick = onCameraClick) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
                    }
                }
            }
        } else {
            // Show placeholder with TWO options
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
                    Text("Add a photo", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(AppTheme.dimens.spacerMedium))
                    Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.paddingMedium)) {
                        Button(onClick = {
                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }) {
                            Icon(Icons.Default.Image, contentDescription = null)
                            Spacer(Modifier.width(AppTheme.dimens.paddingSmall))
                            Text("Gallery")
                        }
                        Button(onClick = onCameraClick) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Spacer(Modifier.width(AppTheme.dimens.paddingSmall))
                            Text("Camera")
                        }
                    }
                }
            }
        }
        TextField(
            value = mealDetails.name,
            onValueChange = { onValueChange(mealDetails.copy(name = it)) },
            label = { Text(stringResource(R.string.meal_name_req)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
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
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            //leadingIcon = { Text(Currency.getInstance(Locale.getDefault()).symbol) },
            modifier = Modifier
                .fillMaxWidth(),
            enabled = enabled,
            singleLine = false
        )
        TextField(
            value = mealDetails.calories.toString(),
            onValueChange = { onValueChange(mealDetails.copy(calories = it.toIntOrNull() ?: 0)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(R.string.calories_req)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        if (enabled) {
            Text(
                text = stringResource(R.string.required_fields),
                modifier = Modifier.padding(AppTheme.dimens.paddingMedium)
            )
        }
    }
}