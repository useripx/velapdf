package com.njagakneai.velapdf.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Tipe notifikasi yang menentukan tampilan warna dan ikon banner.
 */
enum class NotificationType {
    Success, Error, Info
}

/**
 * Data untuk menampilkan notifikasi banner.
 */
data class NotificationData(
    val message: String,
    val type: NotificationType = NotificationType.Info
)

/**
 * Komponen Notification Toast yang menampilkan banner animasi di bagian atas layar.
 * Otomatis menghilang setelah [autoDismissMs] milidetik.
 */
@Composable
fun NotificationToast(
    notification: NotificationData?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    autoDismissMs: Long = 3500L
) {
    AnimatedVisibility(
        visible = notification != null,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> -fullHeight },
            animationSpec = tween(durationMillis = 350)
        ) + fadeIn(animationSpec = tween(durationMillis = 350)),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> -fullHeight },
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut(animationSpec = tween(durationMillis = 300)),
        modifier = modifier
    ) {
        notification?.let { data ->
            LaunchedEffect(data) {
                delay(autoDismissMs)
                onDismiss()
            }

            val containerColor = when (data.type) {
                NotificationType.Success -> Color(0xFF1B5E20)
                NotificationType.Error -> Color(0xFFB71C1C)
                NotificationType.Info -> Color(0xFF0D47A1)
            }

            val icon = when (data.type) {
                NotificationType.Success -> Icons.Filled.CheckCircle
                NotificationType.Error -> Icons.Filled.Error
                NotificationType.Info -> Icons.Filled.Info
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .statusBarsPadding(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = containerColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = data.message,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup Notifikasi",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
