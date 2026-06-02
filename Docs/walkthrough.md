# VelaPDF - Catatan Walkthrough Proyek

Dokumen ini melacak riwayat pengembangan, integrasi arsitektur, dan perubahan yang diterapkan pada repositori VelaPDF sesuai dengan panduan _MVVM_ dan pengembangan Android modern.

---

## Tahap 1: Inisialisasi Proyek & Navigasi (Selesai)

- **Tema dan Warna Utama**: `Color.kt`, `Theme.kt`, dan `Type.kt` telah diperbarui dengan referensi dari `Docs/design.md`.
- **Integrasi Build.gradle**: Menggunakan Hilt versi `2.57.1`, AGP `8.7.2`, beserta `jvmTarget="11"`.
- **MainActivity**: Menggunakan `@AndroidEntryPoint` untuk *Dependency Injection*.
- **Manifest**: Membersihkan entri yang *deprecated* dan memastikan `Theme.VelaPDF` terpasang.

---

## Tahap 2: Splash Screen dan Route Navigasi (Selesai)

- **Animasi Splash Screen** (`SplashScreen.kt`): Menerapkan layar selamat datang yang merujuk pada tata letak `.html` referensi, dilengkapi animasi *fade/scale* dan bar loading berupa _shimmer_.
- **Permission Popups** (`PermissionsScreen.kt` & `PermissionsViewModel.kt`): Mengeksekusi permintaan izin perangkat untuk `CAMERA`, `READ_EXTERNAL_STORAGE`, dan `WRITE_EXTERNAL_STORAGE` menggunakan *Accompanist Permissions*. Antarmuka dirancang menyesuaikan palet warna VelaPDF.
- **Preferences DataStore** (`PreferencesManager.kt`): Menginisialisasi *key-value datastore* lokal untuk merekam apakah preferensi perangkat sudah disetujui, menggunakan *flow coroutines*.
- **App Navigation** (`NavGraph.kt`): Setup komponen host rute (Splash -> Permissions -> Dashboard) terintegrasi pada `MainActivity`.
- **Icon / Drawable Asset**: `icon.png` berhasil dikonfigurasi ke dalam `res/drawable/logo.png`.
- **Status Kompilasi**: SUCCESSFUL (`assembleDebug` berhasil dikompilasi tanpa *error* ketergantungan).

---

## Langkah Selanjutnya (Next Tasks)

- Mengembangkan *Dashboard* dan komponen antarmuka yang lengkap sesuai fungsionalitas UI pada `Docs/design.md`.
- Implementasi fungsional backend/pemindaian kamera dan konversi PDF utama.

---

## Tahap 3: Dialog Izin Memori (Selesai)

- **Permission Logic Handler** (`PermissionHelper.kt`): Implementasi manajemen deteksi perizinan (*Permissions Check*) khusus untuk Storage dengan mempertimbangkan pembaruan spesifikasi Android 13+ (`READ_MEDIA_XYZ`) versus versi sebelumnya.
- **Storage Dialog Component** (`PermissionDialogs.kt`): Integrasi dialog rasionalisasi memori (StoragePermissionDialog) menggunakan elemen UI Jetpack Compose. Menambahkan ikon *launcher* (`R.drawable.logo`) beserta teks dan konfirmasi pengguna.
- **Dashboard Refactor** (`DashboardScreen.kt`): Otomasi *trigger* `StoragePermissionDialog` yang akan mencegat *flow* utama dan meminta perizinan ketika hak akses penggunaan memori terdeteksi belum diberikan pada perangkat.

---

## Langkah Selanjutnya (Next Tasks)

- Implementasi modul *Core File Management* untuk pengolahan direktori dokumen.

---

## Tahap 4: Image Picker & Gallery (Selesai)

- **SelectedImage Model** (`data/model/SelectedImage.kt`): Class data metadata *image* yang menyimpan URI asli maupun URI hasil ekstraksi _cache_.
- **URI Caching System** (`utils/FileUriHelper.kt`): Mengatasi limitasi *Scoped Storage* di Android 13+ melalui metode duplikasi aman dengan cara melakukan kloning aliran data (_Input/Output Stream_) pada *System Media Store* ke dalam lokal file di *Cache Directory* agar konversi file tidak menolak perizinan atau melanggar *Policy*.
- **Gallery Grid Component** (`ui/components/GridImagePicker.kt`): Integrasi Multiple Photo Picker (`ActivityResultContracts.PickMultipleVisualMedia`) dipadukan dengan daftar tampilan _Grid_ interaktif dan Coil untuk pratinjau *thumbnail* hingga batasan maksimal `50` gambar yang disetujui.
