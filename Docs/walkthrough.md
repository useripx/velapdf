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

---

# Fitur Tambahan: Merge PDF


# Walkthrough: Fitur Merge PDF

**Tanggal:** 3 Juni 2026
**Branch:** `feature/mergepdf`
**Commit:** `feat: add merge pdf feature`

---

## Ringkasan

Fitur **Merge PDF** memungkinkan pengguna menggabungkan beberapa file PDF dan gambar (JPEG/PNG/WebP) menjadi satu dokumen PDF. Fitur ini ditempatkan di bawah "Image to PDF" pada Dashboard dengan badge "New".

### Kapasitas & Batasan

- Maksimal **100 file** per sesi merge
- Maksimal **500 halaman per file** PDF
- Maksimal **500 halaman total** dalam output gabungan
- Format input: PDF, JPEG, PNG, WebP

### Fitur Utama

- **File Picker** multi-select untuk PDF dan gambar (`OpenMultipleDocuments`)
- **Sortable Document List** — urut ulang file dengan tombol up/down
- **Hapus file** individual atau hapus semua
- **Capacity indicator** — progress bar visual menunjukkan penggunaan kapasitas halaman
- **Auto-convert gambar** ke halaman PDF saat merge
- **Progress bar** animasi saat proses merge berlangsung
- **Notifikasi** in-app toast + system notification saat selesai
- Navigasi ke **SuccessScreen** setelah merge berhasil

---

## File Baru

| File                                                                                                                                                        | Deskripsi                                                                         |
| ----------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------- |
| [MergeableDocument.kt](file:///c:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/data/model/MergeableDocument.kt)          | Data class untuk dokumen (PDF/Image) yang akan digabung                           |
| [PdfMergerEngine.kt](file:///c:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/utils/PdfMergerEngine.kt)                   | Backend engine merge menggunakan native Android `PdfRenderer` + `PdfDocument` |
| [MergePdfViewModel.kt](file:///c:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/ui/viewmodel/MergePdfViewModel.kt)        | ViewModel untuk state management (dokumen list, validasi, merge state)            |
| [SortableDocumentList.kt](file:///c:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/ui/components/SortableDocumentList.kt) | Komponen UI list dokumen dengan kontrol urut/hapus                                |
| [MergePdfScreen.kt](file:///c:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/ui/screen/MergePdfScreen.kt)                 | Layar utama fitur Merge PDF                                                       |

## File Dimodifikasi

| File                                                                                                                                          | Perubahan                                                                                                          |
| --------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------ |
| [NavGraph.kt](file:///c:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/ui/navigation/NavGraph.kt)           | Rute navigasi `Screen.MergePdf`, composable `MergePdfScreen`, callback `onMergeSuccess` ke `SuccessScreen` |
| [DashboardScreen.kt](file:///c:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/ui/screen/DashboardScreen.kt) | Card "Merge PDF" dengan ikon `MergeType`, badge "New", callback `onNavigateToMergePdf`                         |

---

## Keputusan Arsitektur

1. **Native Android API** — Tidak menggunakan library pihak ketiga (`pdfbox-android`). Menggunakan `android.graphics.pdf.PdfRenderer` untuk membaca PDF dan `android.graphics.pdf.PdfDocument` untuk menulis output. Keduanya sudah tersedia sejak API 21.
2. **Reuse `PdfGenerationState`** — Menggunakan sealed class yang sama dengan fitur Image-to-PDF untuk konsistensi state flow (Idle → Loading → Success/Error).
3. **File-level ordering** — Pengguna mengatur urutan berdasarkan file utuh (bukan halaman individual), sesuai keputusan dari review plan.
4. **Format dibatasi PDF + Gambar** — Konversi Word/Excel tetap "Coming Soon" sesuai keputusan pengguna.

---

## Verifikasi

- ✅ `assembleDebug` — BUILD SUCCESSFUL (0 errors, 0 Kotlin warnings)
- ✅ Tidak ada dependensi baru yang ditambahkan (semua native Android API)

---

# Fitur Tambahan: Penyempurnaan UI & Riwayat (Tahap 2)

**Tanggal:** 3 Juni 2026
**Branch:** `fix/fitur_pengaturan`
**Commit:** `feat: implement history, save as, settings`

---

## 1. Riwayat File (Recent Files & Files Menu)
- **Room Database (`AppDatabase`, `HistoryDao`)**: Dibuat sebagai _Single Source of Truth_ untuk riwayat file yang sukses dibuat/digabungkan.
- **`HistoryViewModel` & DI (Hilt)**: Digunakan untuk mengelola state History dan otomatis menampilkan list file terbaru di `HistoryScreen`.
- **Integrasi Menu Files**: Menu "Files" di Bottom Navigation dan tombol "History" di AppBar sekarang terhubung secara fungsional ke `HistoryScreen`.

## 2. Penyimpanan Custom ("Save As")
- **UI Save As**: Menambahkan TextField "Nama File Output" dan Switch "Pilih lokasi manual (Save As)" pada layar **Image to PDF** dan **Merge PDF**.
- **Logika Penyimpanan**: 
  - Jika "Save As" mati: File otomatis tersimpan ke folder `Documents/VelaPDF`.
  - Jika "Save As" menyala: Menggunakan `ActivityResultContracts.CreateDocument` agar pengguna bebas memilih folder (seperti Google Drive, Download, dll).

## 3. Pengaturan Tema & Kompresi (Settings)
- **Tema Dinamis**: `MainActivity` sekarang mengobservasi setelan Tema (Terang/Gelap/Sistem) langsung dari `PreferencesManager` menggunakan `StateFlow`.
- **Kualitas Gambar (Kompresi)**: Fitur konversi Image to PDF dan PDF Rendering kini menerapkan skala kompresi berdasarkan pilihan pengguna di layar Pengaturan (Tinggi = 1.0x/2.0x, Sedang = 0.7x/1.5x, Rendah = 0.4x/1.0x).

## 4. Perbaikan UI Tambahan
- **Hamburger Menu**: Tersedia opsi Beranda, Beri Rating, Bagikan Aplikasi, Kebijakan Privasi (mengarah ke URL), dan Tentang Kami. Fitur klik sudah diaktifkan dan berjalan dengan lancar.
- **Navigasi Terintegrasi**: Keseluruhan Menu Bawah (Beranda, Files, Settings) sudah terkoneksi penuh dengan Screen yang sesuai.

---

# Fitur Tambahan: Pengaturan Lanjutan & Bersihkan Cache (Tahap 3)

**Tanggal:** 3 Juni 2026
**Branch:** `feature/frontend-settings-preferences`
**Commit:** `feat: create settings and wipe cache feature`

---

## 1. Preferensi Ukuran Halaman PDF
- **Pilihan Standar & Custom**: Menambahkan pilihan ukuran halaman (A4, F4, Legal, Letter, A5, B5, dan Custom) di bagian pengaturan konversi.
- **Input Custom Ukuran (Width & Height)**: Saat ukuran halaman diatur ke "Custom", akan muncul field _TextField_ tambahan bagi pengguna untuk menginputkan lebar dan tinggi halaman khusus secara manual dalam satuan milimeter (mm).
- **Penyimpanan Terpusat (DataStore)**: Setiap perubahan state untuk `pageSize`, `customPageWidth`, dan `customPageHeight` langsung disimpan pada `PreferencesManager` dan otomatis terpantau oleh `SettingsViewModel`.

## 2. Fitur Bersihkan Cache & Riwayat
- **Aksi Konfirmasi (AlertDialog)**: Dilengkapi dengan dialog peringatan berwarna merah _(error container)_ untuk mencegah ketidaksengajaan terhapusnya riwayat.
- **Menyeluruh (Room + DataStore)**: Tombol aksi ini bukan sekedar mereset Preferensi di `PreferencesManager.clearPreferences()`, tetapi juga mengeksekusi *query* `historyDao.deleteAllHistory()` untuk menghapus semua *row* daftar di layar *Recent Files / History*.
- **Aman Secara File**: Fitur ini hanya melakukan proses "Wipe Data Local" di layer Room dan DataStore. File PDF asli pengguna di penyimpanan (*Storage*) eksternal tetap terjamin dan tidak ikut terhapus.

## Verifikasi
- ✅ `assembleDebug` — BUILD SUCCESSFUL
- ✅ Fitur terhubung sepenuhnya tanpa kesalahan UI maupun Logika.

---

# Fitur Tambahan: Integrasi UI, Animasi, dan Perbaikan Bug Lanjutan (Tahap 4)

**Tanggal:** 3 Juni 2026
**Branch:** `feature/frontend-settings-preferences`
**Commit:** `feat: add ui animations and file provider`

---

## 1. Integrasi UI & Animasi
- **Transisi Antar Layar**: `NavGraph.kt` telah ditambahkan pengaturan `enterTransition`, `exitTransition`, `popEnterTransition`, dan `popExitTransition` (slide dan fade). Transisi dibuat lebih modern dengan animasi pergerakan layar yang dinamis sesuai dengan material design.
- **Berbagi File (FileProvider)**: Terpasang *FileProvider* yang terkonfigurasi di `filepaths.xml`. Fitur `FileShareHelper.kt` menggunakan intent `ACTION_SEND` untuk berbagi PDF hasil konversi langsung dari `SuccessScreen` ke aplikasi lain (WhatsApp, Email, Telegram, dll). Layar ini juga sudah diterjemahkan seluruhnya ke bahasa Indonesia.
- **Penyelarasan Tampilan**: Teks privasi di beranda (Dashboard) dan tag "TAHAP PENGEMBANGAN" diselaraskan agar ramah mode gelap (Dark Mode).

## 2. Perbaikan Bug Berdasarkan Feedback
- **Wipe Cache & History**: Fungsi untuk membersihkan cache kini memanggil fungsi *recursive delete* yang mengosongkan folder `cacheDir` aplikasi secara fisik (seperti folder `pdf_exports` dan `camera_images`), tidak hanya menghapus Riwayat di database.
- **Interaksi & Ukuran Riwayat**: *Bug* ukuran file konversi di *History* ("0 KB") telah diperbaiki dengan mengambil ukuran sebenarnya dari *temp file* sebelum dihapus. Item di halaman Riwayat sekarang bisa disentuh (diklik) dan akan otomatis membuka *viewer* PDF (melalui `ACTION_VIEW`).
- **Dialog Izin Akses (PermissionsScreen)**: Halaman `PermissionsScreen` yang tadinya berupa *full screen* kini telah didesain ulang menyerupai `AlertDialog` dan sepenuhnya selaras dengan tampilan `StoragePermissionDialog`.

## Verifikasi
- ✅ `assembleDebug` — BUILD SUCCESSFUL (100% tanpa error)
- ✅ Perubahan telah di-*commit* ke Git dan di-*push* untuk di-*Pull Request*.
