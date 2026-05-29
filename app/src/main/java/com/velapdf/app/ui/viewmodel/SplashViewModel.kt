package com.velapdf.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velapdf.app.data.preferences.PreferencesManager
import com.velapdf.app.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _nextDestination = MutableStateFlow<String?>(null)
    val nextDestination: StateFlow<String?> = _nextDestination.asStateFlow()

    init {
        determineNextScreen()
    }

    private fun determineNextScreen() {
        viewModelScope.launch {
            // Artificial delay to show the professional splash screen animation
            delay(2000)
            
            val isPermissionsGranted = preferencesManager.isPermissionsGranted.first()
            if (isPermissionsGranted) {
                _nextDestination.value = Screen.Dashboard.route
            } else {
                _nextDestination.value = Screen.Permissions.route
            }
        }
    }
}
