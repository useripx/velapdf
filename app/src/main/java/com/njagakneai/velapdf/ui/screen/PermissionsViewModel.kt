package com.njagakneai.velapdf.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.njagakneai.velapdf.data.preferences.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    fun markPermissionsGranted() {
        viewModelScope.launch {
            preferencesManager.setPermissionsGranted(true)
        }
    }
}
