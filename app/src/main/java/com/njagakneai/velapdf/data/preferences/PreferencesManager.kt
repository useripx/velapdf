package com.njagakneai.velapdf.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "velapdf_settings")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val dataStore = context.dataStore

    // Permissions State
    val isPermissionsGranted: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.PERMISSIONS_GRANTED] ?: false
    }

    suspend fun setPermissionsGranted(granted: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PERMISSIONS_GRANTED] = granted
        }
    }

    // App Theme (Sistem, Terang, Gelap)
    val appTheme: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.APP_THEME] ?: "Sistem"
    }

    suspend fun setAppTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_THEME] = theme
        }
    }

    // Compression Quality (High, Medium, Low)
    val compressionQuality: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.COMPRESSION_QUALITY] ?: "Sedang"
    }

    suspend fun setCompressionQuality(quality: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.COMPRESSION_QUALITY] = quality
        }
    }

    // Auto Open PDF
    val isAutoOpenEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.AUTO_OPEN_PDF] ?: true
    }

    suspend fun setAutoOpenEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_OPEN_PDF] = enabled
        }
    }

    // Page Size
    val pageSize: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.PAGE_SIZE] ?: "A4"
    }

    suspend fun setPageSize(size: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PAGE_SIZE] = size
        }
    }

    // Custom Page Width (in mm)
    val customPageWidth: Flow<Int> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.CUSTOM_PAGE_WIDTH] ?: 210
    }

    suspend fun setCustomPageWidth(width: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CUSTOM_PAGE_WIDTH] = width
        }
    }

    // Custom Page Height (in mm)
    val customPageHeight: Flow<Int> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.CUSTOM_PAGE_HEIGHT] ?: 297
    }

    suspend fun setCustomPageHeight(height: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CUSTOM_PAGE_HEIGHT] = height
        }
    }

    // Clear all preferences
    suspend fun clearPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private object PreferencesKeys {
        val PERMISSIONS_GRANTED = booleanPreferencesKey("permissions_granted")
        val APP_THEME = stringPreferencesKey("app_theme")
        val COMPRESSION_QUALITY = stringPreferencesKey("compression_quality")
        val AUTO_OPEN_PDF = booleanPreferencesKey("auto_open_pdf")
        val PAGE_SIZE = stringPreferencesKey("page_size")
        val CUSTOM_PAGE_WIDTH = intPreferencesKey("custom_page_width")
        val CUSTOM_PAGE_HEIGHT = intPreferencesKey("custom_page_height")
    }
}
