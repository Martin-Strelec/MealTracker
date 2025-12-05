package com.example.mealtracker.ui.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mealtracker.R
import com.example.mealtracker.ui.AppViewModelProvider
import com.example.mealtracker.ui.theme.AppTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Composable screen responsible for the Camera UI.
 * Handles runtime permissions, displays the camera preview using CameraX, and captures photos.
 *
 * @param onImageCaptured Callback triggered when a photo is successfully taken. Returns the URI of the saved file.
 * @param onError Callback triggered when an image capture exception occurs.
 * @param viewModel ViewModel to manage camera permission state.
 */
@Composable
fun CameraScreen(
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    viewModel: CameraViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    // --- Permission Handling ---

    // Initialize the permission launcher.
    // If the user grants/denies the permission in the system dialog, this callback is executed.
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            viewModel.onPermissionResult(granted)
            if (!granted) {
                // Show a toast if permission is denied, explaining why it's needed (simplified)
                Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Check permission status as soon as the Composable enters the composition.
    LaunchedEffect(Unit) {
        val isGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        // Update ViewModel with initial state
        viewModel.onPermissionResult(isGranted)

        // If not already granted, launch the system permission request dialog
        if (!isGranted) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }


    // --- UI Content based on Permission State ---

    if (viewModel.hasCameraPermission) {
        // Prepare CameraX UseCases
        // ImageCapture: for taking photos
        val imageCapture = remember { ImageCapture.Builder().build() }
        // PreviewView: The Android View that renders the camera feed
        val previewView = remember { PreviewView(context) }

        // Bind CameraX to Lifecycle
        LaunchedEffect(Unit) {
            val cameraProvider = context.getCameraProvider()
            val preview = Preview.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            // Connect the Preview UseCase to the SurfaceProvider of the PreviewView
            preview.setSurfaceProvider(previewView.surfaceProvider)

            try {
                // Unbind any previous use cases before binding new ones
                cameraProvider.unbindAll()

                // Bind the camera lifecycle to the current lifecycle owner (this Composable).
                // This ensures the camera opens/closes automatically.
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageCapture
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            // Embed the Android View (PreviewView) within Compose
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            // Capture Button Overlay
            Button(
                onClick = {
                    takePhoto(
                        filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
                        imageCapture = imageCapture,
                        outputDirectory = context.filesDir,
                        executor = ContextCompat.getMainExecutor(context),
                        onImageCaptured = onImageCaptured,
                        onError = onError
                    )
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(AppTheme.dimens.paddingMedium)
            ) {
                Text(stringResource(R.string.take_photo))
            }
        }
    } else {
        // Fallback UI: Displayed when permission is denied or not yet granted
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.camera_grant_permission),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * Logic to capture a photo and save it to a file.
 *
 * @param filenameFormat The format for the timestamped filename.
 * @param imageCapture The CameraX UseCase object.
 * @param outputDirectory The directory to save the image.
 * @param executor The executor to run the callback (MainExecutor for UI thread).
 * @param onImageCaptured Success callback with the file URI.
 * @param onError Error callback.
 */
private fun takePhoto(
    filenameFormat: String,
    imageCapture: ImageCapture,
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    // 1. Create the output file with a unique name
    val photoFile = File(
        outputDirectory,
        SimpleDateFormat(filenameFormat, Locale.US).format(System.currentTimeMillis()) + ".jpg"
    )

    // 2. Configure output options
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    // 3. Take picture
    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                // On success, create a URI from the file and return it
                val savedUri = Uri.fromFile(photoFile)
                onImageCaptured(savedUri)
            }
        }
    )
}

/**
 * Extension function to retrieve the ProcessCameraProvider asynchronously using Coroutines.
 * Adapts the Future-based API of CameraX to a suspend function.
 */
private suspend fun Context.getCameraProvider() : ProcessCameraProvider = suspendCoroutine { continuation ->
    val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
    cameraProviderFuture.addListener({
        continuation.resume(cameraProviderFuture.get())
    }, ContextCompat.getMainExecutor(this))
}