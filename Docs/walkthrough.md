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

## Tahap 4: Image Picker & Gallery (Selesai)

- **SelectedImage Model** (`data/model/SelectedImage.kt`): Class data metadata *image* yang menyimpan URI asli maupun URI hasil ekstraksi _cache_.
- **URI Caching System** (`utils/FileUriHelper.kt`): Mengatasi limitasi *Scoped Storage* di Android 13+ melalui metode duplikasi aman dengan cara melakukan kloning aliran data (_Input/Output Stream_) pada *System Media Store* ke dalam lokal file di *Cache Directory* agar konversi file tidak menolak perizinan atau melanggar *Policy*.
- **Gallery Grid Component** (`ui/components/GridImagePicker.kt`): Integrasi Multiple Photo Picker (`ActivityResultContracts.PickMultipleVisualMedia`) dipadukan dengan daftar tampilan _Grid_ interaktif dan Coil untuk pratinjau *thumbnail* hingga batasan maksimal `50` gambar yang disetujui.

---

# Tahap 5: Perbaikan Frontend & Dashboard Refactor (Selesai)

- **Redesign Dashboard** (`DashboardScreen.kt`): Mengubah antarmuka secara keseluruhan menggunakan framework Material3. Menjadikan palet warna lebih responsif dan padu. Menyempurnakan rasio ikon TopAppBar dengan `ContentScale`.
- **Eksternal Link**: Mengubah fungsi tombol _Learn Privacy Policy_ pada Dashboard untuk menautkan halaman _Privacy Policy_ ke URL Google Sites secara dinamis.
- **Image Conversion Layout** (`ImageToPdfScreen.kt`): Menyajikan tata letak dasar fungsional pratinjau gambar, implementasi tombol aksi PDF lekat (*sticky*), serta kerangka visual manipulasi *Bitmap* sebelum konversi difinalisasikan oleh *Backend-camera-storage*.
- **Sortable Images Array** (`ui/components/SortableImageGrid.kt`): Penyediaan fungsi visual rotasi, *remove*, dan reorder gambar (dalam antrean proses memori).
- **Icon Refactoring (Bug Fix)**: Memperbaiki kesalahan *Force Close* aplikasi yang disebabkan oleh pengecualian pe-muatan aset bawaan Android (`IllegalArgumentException`) pada _NavigationBar_ dengan memigrasikannya ke ikon resmi Compose (`androidx.compose.material.icons`).
- **Splash Screen Refinement**: Menyesuaikan rasio ikon berlebih pada _SplashScreen.kt_ supaya tepat tertata tanpa pemotongan gambar aneh (_clipping & scale adjust_).

---

## Tahap 7: PDF Generation Engine (Backend) (Selesai)

**Tanggal:** 2 Juni 2026**Branch:** `feature/Backend-pdf-engine`**Task:**

- Binding State Konversi ke UI Loader (Progress bar 0-100%)
- PDF Engine Builder (Konversi Bitmap lokal ke Halaman PDF)
- Menyematkan FileProvider untuk melintasi sistem eksternal URI saat PDF viewer dibuka.

**Endpoint:**

- `ui/screen/ConverterScreen.kt`
- `ui/screen/SuccessScreen.kt`
- `utils/PdfGenerator.kt`
- `data/repository/PdfRepository.kt`
- `data/repository/PdfGenerationState.kt`

**Commit:** `feat: add pdf generation engine`

---

## Tahap 7 Hotfix: Koneksi Flow Konversi (Selesai)

**Tanggal:** 2 Juni 2026**Task:**

- Memperbaiki tombol "Convert to PDF" yang sebelumnya hanya simulasi delay dan tidak melakukan apa-apa setelah selesai.
- Menghubungkan `ImageToPdfScreen` → `PdfRepository` → `SuccessScreen` secara end-to-end.
- Memperbaiki `PdfRepository` dari `flow {}` ke `channelFlow {}` untuk menghindari crash `Flow invariant is violated` saat progress di-emit dari `Dispatchers.IO`.
- Menambahkan `navigationBarsPadding()` pada bottom bar `ImageToPdfScreen` untuk memperbaiki tombol "Convert to PDF" yang terpotong oleh system navigation bar.
- Menambahkan animasi progress bar linear + persentase pada tombol konversi.

**Endpoint:**

- `ui/screen/ImageToPdfScreen.kt` (modifikasi utama)
- `data/repository/PdfRepository.kt` (fix flow context)
- `ui/navigation/NavGraph.kt` (add onConversionSuccess callback)

---

## Tahap 8: Local Notification (Frontend) (Selesai)

**Tanggal:** 2 Juni 2026**Branch:** `feature/frontend-notifications-toast`**Task:**

- Pengintegrasian Tampilan Banner Notifikasi Aplikasi (In-App Toast)
- Android NotificationManager Service untuk Notifikasi PDF Selesai
- Permission `POST_NOTIFICATIONS` untuk Android 13+ (API 33+)

**Endpoint:**

- `ui/components/NotificationToast.kt` — Komponen banner animasi slide-in dengan tipe Success/Error/Info, auto-dismiss 3.5 detik
- `utils/NotificationHelper.kt` — Notification Channel + System Notification dengan PendingIntent untuk membuka PDF
- `AndroidManifest.xml` — Deklarasi permission `POST_NOTIFICATIONS`

**Commit:** `feat: implement local notification`

---

## Tahap 9: Room Database (Backend) (Selesai)

**Tanggal:** 30 Mei 2026
**Branch:** `feature/Backend-cache-repository`
**Task:**
- Pembuatan List Tampilan Riwayat Konversi Berkas PDF
- Setup Room DB, Entity & DAO untuk caching file dan penataan tanggal
- Memperbarui versi Room di `libs.versions.toml` ke `2.7.0` untuk kompatibilitas dengan Kapt di Kotlin 2.2

**Endpoint:**
- `ui/screen/HistoryScreen.kt`
- `data/model/HistoryEntity.kt`
- `data/database/HistoryDao.kt`
- `data/database/AppDatabase.kt`

**Commit:** `feat: setup room database history`

---

## Fitur Tambahan: Ambil Gambar dari Kamera (Selesai)

**Task:**
- Menambahkan tombol "Kamera" di bawah tombol "Upload Image" pada `ImageToPdfScreen`.
- Mengimplementasikan `ActivityResultContracts.TakePicture()` untuk mengambil foto secara langsung.
- Mendaftarkan path `camera_images` di dalam `filepaths.xml` dan membuat File Provider lokal untuk menampung gambar kamera secara aman via `FileUriHelper`.

**Endpoint:**
- `ui/screen/ImageToPdfScreen.kt`
- `utils/FileUriHelper.kt`
- `res/xml/filepaths.xml`
