package com.njagakneai.velapdf.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.njagakneai.velapdf.data.preferences.PreferencesManager
import com.njagakneai.velapdf.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth

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
            val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
            
            if (!isPermissionsGranted) {
                _nextDestination.value = Screen.Permissions.route
            } else if (!isLoggedIn) {
                _nextDestination.value = Screen.Login.route
            } else {
                _nextDestination.value = Screen.Dashboard.route
            }
        }
    }
}
