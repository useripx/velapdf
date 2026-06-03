package com.njagakneai.velapdf.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.njagakneai.velapdf.data.database.HistoryDao
import com.njagakneai.velapdf.data.preferences.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesManager: PreferencesManager,
    private val historyDao: HistoryDao
) : ViewModel() {

    val appTheme: StateFlow<String> = preferencesManager.appTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Sistem")

    val compressionQuality: StateFlow<String> = preferencesManager.compressionQuality
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Sedang")

    val isAutoOpenEnabled: StateFlow<Boolean> = preferencesManager.isAutoOpenEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val pageSize: StateFlow<String> = preferencesManager.pageSize
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "A4")

    val customPageWidth: StateFlow<Int> = preferencesManager.customPageWidth
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 210)

    val customPageHeight: StateFlow<Int> = preferencesManager.customPageHeight
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 297)

    fun setAppTheme(theme: String) {
        viewModelScope.launch {
            preferencesManager.setAppTheme(theme)
        }
    }

    fun setCompressionQuality(quality: String) {
        viewModelScope.launch {
            preferencesManager.setCompressionQuality(quality)
        }
    }

    fun setAutoOpenEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setAutoOpenEnabled(enabled)
        }
    }

    fun setPageSize(size: String) {
        viewModelScope.launch {
            preferencesManager.setPageSize(size)
        }
    }

    fun setCustomPageWidth(width: Int) {
        viewModelScope.launch {
            preferencesManager.setCustomPageWidth(width)
        }
    }

    fun setCustomPageHeight(height: Int) {
        viewModelScope.launch {
            preferencesManager.setCustomPageHeight(height)
        }
    }

    fun wipeCache(onComplete: () -> Unit) {
        viewModelScope.launch {
            // Delete all DB history
            historyDao.deleteAllHistory()
            // Reset DataStore preferences
            preferencesManager.clearPreferences()
            
            // Delete cache files (pdf_exports, images, camera_images, temp files)
            try {
                val cacheDir = context.cacheDir
                if (cacheDir != null && cacheDir.isDirectory) {
                    deleteRecursive(cacheDir)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            onComplete()
        }
    }
    
    private fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) {
            fileOrDirectory.listFiles()?.forEach { child ->
                deleteRecursive(child)
            }
        }
        fileOrDirectory.delete()
    }
}
