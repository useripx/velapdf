package com.njagakneai.velapdf.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.njagakneai.velapdf.R

object NotificationHelper {

    private const val CHANNEL_ID = "pdf_conversion_channel"
    private const val CHANNEL_NAME = "PDF Conversion"
    private const val CHANNEL_DESCRIPTION = "Notifications when PDF conversion is completed"
    private const val NOTIFICATION_ID_BASE = 2000

    /**
     * Creates the notification channel for PDF conversion notifications.
     * Should be called once on app startup (idempotent — safe to call multiple times).
     */
    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = CHANNEL_DESCRIPTION
            setShowBadge(true)
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Shows a system notification when a PDF file has been successfully created.
     * Tapping the notification opens the PDF with an external viewer.
     */
    fun showPdfCompleteNotification(context: Context, fileName: String, pdfUri: Uri) {
        // Ensure the channel exists
        createNotificationChannel(context)

        // Check POST_NOTIFICATIONS permission for Android 13+ (API 33+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission not granted — silently skip so the app doesn't crash
                return
            }
        }

        // Intent to open the PDF with an external viewer
        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(pdfUri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            viewIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("PDF Berhasil Dibuat")
            .setContentText("$fileName siap dibuka")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("File \"$fileName\" telah berhasil dikonversi menjadi PDF. Ketuk untuk membuka.")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(
            NOTIFICATION_ID_BASE + System.currentTimeMillis().toInt(),
            notification
        )
    }
}
