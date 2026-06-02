package com.velapdf.app.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.os.Build
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.velapdf.app.ui.components.StoragePermissionDialog
import com.velapdf.app.utils.PermissionHelper

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(!PermissionHelper.hasStoragePermission(context)) }

    val storagePermissionsToRequest = buildList {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(android.Manifest.permission.READ_MEDIA_IMAGES)
            add(android.Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    val multiplePermissionsState = rememberMultiplePermissionsState(
        permissions = storagePermissionsToRequest
    ) { result ->
        // Check if any is granted, or recheck with PermissionHelper
        showPermissionDialog = !PermissionHelper.hasStoragePermission(context)
    }

    if (showPermissionDialog) {
        StoragePermissionDialog(
            onConfirm = {
                if (multiplePermissionsState.shouldShowRationale) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                } else {
                    multiplePermissionsState.launchMultiplePermissionRequest()
                }
            },
            onDismiss = {
                showPermissionDialog = false
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Dashboard")
    }
}
