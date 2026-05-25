### BAGIAN 1: Perintah Langsung (Prompt) Untuk Antigravity Agent


```
Buatkan saya aplikasi Android Native bernama "VelaPDF" yang mereplikasi fitur aplikasi web VelaPDF.
Spesifikasi Teknis:
1. Bahasa & Format: Kotlin dengan Jetpack Compose (UI modern).
2. Arsitektur: Clean Architecture dengan MVVM (Model-View-ViewModel) Pattern.
3. Manajemen State: StateFlow/SharedFlow, LiveData, dan ViewModel.
4. Dependency Injection: Hilt atau Koin (Koin direkomendasikan untuk fleksibilitas).
5. Database Lokal: Room Database (pengganti localStorage riwayat konversi PDF).
6. PDF Generation: Gunakan library 'com.tom-roush:pdfbox-android:2.0.27' atau Android native 'PdfDocument' untuk performa render lokal.
7. PERIZINAN (Kamera & Penyimpanan): Implementasikan Accompanist Permissions API untuk integrasi pop-up persetujuan kamera (android.permission.CAMERA) dan penyimpanan internal (READ_EXTERNAL_STORAGE / READ_MEDIA_IMAGES untuk Android 13+). Harus ada dialog kustom sebelum sistem meminta izin.
8. NOTIFIKASI: Gunakan Android NotificationManager dengan NotificationChannel untuk memunculkan notifikasi pop-up push lokal saat PDF selesai dikonversi.
9. ALUR INTERFACE:
   - SplashView -> DashboardView -> ImageToPdfConverterView -> SuccessView.
   - HistoryView & SettingsView terpisah atau modular.
```

---

### BAGIAN 2: Mapping Struktur Folder & Berkas (MVVM Project Tree)

Berikut adalah skema arsitektur yang ideal untuk diimplementasikan pada proyek Android Anda:

```
app/
├── src/
│   ├── main/
│   │   ├── AndroidManifest.xml          <-- Deklarasi Izin Kamera, Storage, dan Notification
│   │   ├── java/com/velapdf/app/
│   │   │   ├── di/
│   │   │   │   └── AppModule.kt         <-- Dependency Injection (Room, Repository, dll)
│   │   │   ├── data/
│   │   │   │   ├── database/
│   │   │   │   │   ├── AppDatabase.kt   <-- Database Room
│   │   │   │   │   └── HistoryDao.kt    <-- DAO Query simpan riwayat PDF
│   │   │   │   ├── model/
│   │   │   │   │   ├── HistoryItem.kt   <-- Entity Riwayat Dokumen
│   │   │   │   │   └── SelectedImage.kt <-- Data Class Gambar Terpilih & Rotasi
│   │   │   │   └── repository/
│   │   │   │       └── PdfRepository.kt <-- Pengelola logic simpan database & berkas PDF
│   │   │   ├── domain/
│   │   │   │   └── PdfConverterEngine.kt<-- Pembuat berkas PDF lokal dari URIs bitmap
│   │   │   ├── ui/
│   │   │   │   ├── theme/
│   │   │   │   │   ├── Color.kt         <-- Penyesuaian skema warna sistem (Slate & Blue)
│   │   │   │   │   ├── Theme.kt
│   │   │   │   │   └── Type.kt
│   │   │   │   ├── viewmodel/
│   │   │   │   │   ├── MainViewModel.kt <-- ViewModel tunggal penengah Data Flow
│   │   │   │   │   └── HistoryViewModel.kt
│   │   │   │   └── screen/
│   │   │   │       ├── SplashScreen.kt  <-- Tampilan Pembuka VelaPDF Animasi
│   │   │   │       ├── DashboardScreen.kt<-- Dashboard Menu Bento Style
│   │   │   │       ├── ConverterScreen.kt<-- Dropzone seleksi gambar, rotasi, reorder
│   │   │   │       ├── SuccessScreen.kt <-- Tampilan Sukses & Download
│   │   │   │       ├── HistoryScreen.kt <-- Daftar Riwayat PDF lokal
│   │   │   │       └── SettingsScreen.kt<-- Pilihan Format Page & Hapus Cache
│   │   │   └── utils/
│   │   │       ├── NotificationHelper.kt<-- Library Pembuat Pop-up Push Notifikasi
│   │   │       └── PermissionUtils.kt   <-- Pembuat Dialog Izin Kamera & File
│   │   └── res/
│   │       ├── drawable/                <-- Icons, Logo (vektor xml)
│   │       └── values/
│   │           └── strings.xml
│   └── build.gradle.kts (App level)     <-- Konfigurasi Akurasi Versi & Library
```

---

### BAGIAN 3: Implementasi Kunci (Popup Izin & Notifikasi)

Untuk membantu Agen Antigravity agar tidak melakukan coding dari nol, berikan potongan kode Kotlin di bawah ini sebagai arsitektur referensi izin dan notifikasi popup:

#### 1. Konfigurasi AndroidManifest.xml (Izin Wajib)

code Xml

```
<!-- Izin Dasar Kamera dan Penyimpanan -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />
<!-- Khusus Android 13+ (API 33+) -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<!-- Izin Notifikasi Pop-up (Android 13+) -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

#### 2. Jetpack Compose Izin Pop-Up Dialog (Kotlin)

code Kotlin

```
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissionsScreen(
    onPermissionsGranted: () -> Unit
) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val mediaPermissionState = rememberPermissionState(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
    )

    if (cameraPermissionState.status.isGranted && mediaPermissionState.status.isGranted) {
        onPermissionsGranted()
    } else {
        // Tampilkan dialog kustom sebelum memunculkan popup sistem
        AlertDialog(
            onDismissRequest = { /* Handle jika user membatalkan */ },
            title = { Text("Akses VelaPDF Diperlukan") },
            text = { Text("VelaPDF memerlukan izin Kamera untuk langsung memindai dokumen fisik dan Penyimpanan untuk memuat file gambar secara lokal di perangkat Anda.") },
            confirmButton = {
                Button(onClick = {
                    cameraPermissionState.launchPermissionRequest()
                    mediaPermissionState.launchPermissionRequest()
                }) {
                    Text("Berikan Izin")
                }
            }
        )
    }
}
```

#### 3. Pembuat Pop-up Notifikasi Sukses Lokal


```
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationHelper(private val context: Context) {
    private val channelId = "velapdf_channel"
    private val notificationId = 101

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "VelaPDF Conversion"
            val descriptionText = "Notifikasi status konversi PDF"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showConversionSuccessNotification(fileName: String) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_download_done) // Ganti logo app anda
            .setContentTitle("Konversi Sukses! 🎉")
            .setContentText("Berkas $fileName Anda berhasil dibuat.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }
}
```

---

### Ringkasan Desain Aplikasi React VelaPDF Saat Ini:

- ** Splash Loader**: Selesai dibuat dengan transisi indah yang menyeimbangkan status environment ready.
    
- ** Bento Dashboard**: Dilengkapi tata letak paling fresh yang mengarah langsung ke Image-to-PDF dan memisahkan modul-modul masa depan.
    
- ** High Fidelity Converter**: Memiliki pemutar arah rotasi gambar, penataan susunan re-order kertas, penyesuaian file secara mulus, dan pengelola kompresi.
    
- ** Transaksi Sukses**: Membuka popup download & share berkas dengan antarmuka yang ramah pengguna.
    
- ** Modul Riwayat**: Menampilkan seluruh data berkas PDF hasil ekspor Anda yang terintegrasi secara efisien untuk memudahkan pengerjaan ulang kapan pun Anda butuhkan.
    

Gunakan skema di atas untuk menginstruksikan Antigravity Agen dalam membuat VelaPDF versi Android yang solid!