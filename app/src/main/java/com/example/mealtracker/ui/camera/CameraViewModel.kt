package com.example.mealtracker.ui.camera

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {
    var hasCameraPermission by mutableStateOf(false)
        private set

    fun onPermissionResult(isGranted: Boolean) {
        hasCameraPermission = isGranted
    }
}