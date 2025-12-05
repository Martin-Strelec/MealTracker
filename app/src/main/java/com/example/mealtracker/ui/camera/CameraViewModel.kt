package com.example.mealtracker.ui.camera

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * ViewModel for the CameraScreen.
 * Primarily responsible for holding the state of Camera Permissions to survive configuration changes.
 */
class CameraViewModel : ViewModel() {

    // Mutable state to track if the camera permission is granted.
    // Compose will observe this state to toggle between the Camera Preview and the Fallback UI.
    var hasCameraPermission by mutableStateOf(false)
        private set

    /**
     * Updates the permission state.
     * Called by the ActivityResultLauncher or the initial permission check.
     */
    fun onPermissionResult(isGranted: Boolean) {
        hasCameraPermission = isGranted
    }
}