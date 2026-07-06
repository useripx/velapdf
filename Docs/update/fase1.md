# IMPLEMENTATION PLAN: VELA PDF 
**Target Eksekutor:** AI Coding Agent (Antigravity)
**Fase Saat Ini:** Fase 1 - Tahap 1 (Memantapkan Fitur Dasar: Picture to PDF)

## Konteks untuk AI Agent
Anda bertugas untuk menyelesaikan dan memantapkan fungsionalitas inti dari aplikasi Android "Vela PDF". Fitur dasar ini adalah memindai gambar (lewat kamera atau galeri) dan mengubahnya menjadi file PDF secara offline. 
PENTING: Aplikasi ini nantinya akan disematkan model AI (TFLite) dan Kriptografi (RSA) di fase mendatang. Oleh karena itu, arsitektur kode pada Tahap 1 ini harus bersih, modular, dan mengutamakan efisiensi memori (mencegah Out of Memory / OOM).

---

## DETAIL LANGKAH KERJA (FASE 1 - TAHAP 1)

### Langkah 1.1: Audit & Konfigurasi Dependensi (Dependencies Setup)
AI Agent harus memastikan *library* dasar untuk pemrosesan gambar dan PDF sudah tersedia dan menggunakan versi yang stabil.
* **Tugas:**
    1. Buka `build.gradle` (app-level).
    2. Tambahkan dependensi untuk akses kamera (disarankan menggunakan **CameraX** agar kompatibel dengan banyak perangkat).
    3. Tambahkan dependensi pengelola gambar (contoh: **Glide** atau **Coil**).
    4. Tambahkan dependensi pembuat PDF (disarankan **iText7 Core** atau **Apache PDFBox** versi Android).
    5. Konfigurasi `AndroidManifest.xml` untuk menambahkan *permissions*: `CAMERA`, `READ_EXTERNAL_STORAGE`, dan `WRITE_EXTERNAL_STORAGE`.

### Langkah 1.2: Pemantapan Modul Input Gambar (Kamera & Galeri)
Memastikan aplikasi dapat mengambil gambar dengan lancar tanpa membuat UI utama (Main Thread) *freeze*.
* **Tugas:**
    1. Buat/perbaiki `Activity` atau `Fragment` untuk layar utama yang memiliki 2 tombol input: "Ambil dari Kamera" dan "Pilih dari Galeri".
    2. Terapkan penanganan *Permission Request* yang aman (menampilkan *dialog request* jika izin belum diberikan).
    3. Hubungkan *intent* kamera dan galeri agar gambar yang dipilih berhasil ditangkap dalam format `Bitmap` atau `URI`.

### Langkah 1.3: Pra-pemrosesan Gambar (Image Pre-processing & OOM Prevention)
Ini adalah langkah paling krusial. Gambar asli dari kamera HP modern bisa berukuran 10MB+. Jika langsung diconvert ke PDF, HP berspesifikasi rendah akan *Force Close* (OOM).
* **Tugas:**
    1. Buat kelas pembantu (*Helper Class*), misalnya `ImageProcessorHelper.kt`.
    2. Buat fungsi untuk mengecilkan resolusi (*Downscale/Resize*) gambar yang tertangkap kamera secara proporsional. (Batasi maksimal resolusi panjang/lebar di angka sekitar 1500px hingga 2000px).
    3. Buat fungsi untuk mengompres kualitas JPEG (misal ke 80%) agar ukuran *file* lebih ringan di memori sementara (RAM).
    4. PASTIKAN proses *resize* dan kompresi ini berjalan di **Background Thread** (Gunakan *Kotlin Coroutines* `Dispatchers.IO`). Jangan eksekusi di *Main Thread*!

### Langkah 1.4: Mesin Pembuat PDF (Image to PDF Engine)
Mengubah gambar yang sudah di-*resize* tadi menjadi sebuah dokumen PDF.
* **Tugas:**
    1. Buat kelas pembantu, misalnya `PdfGeneratorEngine.kt`.
    2. Buat fungsi `createPdfFromImage(imageBitmap, outputFilePath)`.
    3. Logika fungsi: Inisialisasi dokumen PDF kosong $\rightarrow$ Buat halaman baru $\rightarrow$ Sisipkan gambar ke tengah halaman (sesuaikan skala gambar dengan ukuran kertas A4 secara otomatis) $\rightarrow$ Simpan/Tutup dokumen.
    4. Proses ini juga WAJIB dijalankan di **Background Thread**.

### Langkah 1.5: Manajemen File & Notifikasi Sukses
Memberikan *feedback* kepada pengguna dan menyimpan *file* di tempat yang benar.
* **Tugas:**
    1. Simpan *file* PDF yang dihasilkan ke *directory* yang bisa diakses pengguna (contoh: folder `Documents/VelaPDF` atau *App Cache*).
    2. Berikan *penamaan file otomatis* berdasarkan waktu, misal: `Vela_Scan_20260704_1755.pdf`.
    3. Tampilkan UI *Loading/Progress Bar* saat Langkah 1.3 dan 1.4 sedang berjalan.
    4. Tampilkan pesan (Toast/Snackbar) "PDF Berhasil Dibuat" ketika proses selesai, beserta tombol untuk "Buka PDF" menggunakan aplikasi eksternal pembaca PDF di HP.

---

## PARAMETER PENYELESAIAN (Definition of Done)
AI Agent dianggap telah menyelesaikan Fase 1 Tahap 1 jika kondisi berikut terpenuhi:
- [x] Pengguna bisa memilih gambar dari galeri atau kamera tanpa *crash*.
- [x] Gambar berhasil dikompres dan tidak menyebabkan *Out of Memory* saat diproses.
- [x] *File* PDF berhasil terbuat di penyimpanan *smartphone*.
- [x] Gambar di dalam PDF terlihat proporsional (tidak terpotong atau gepeng).
- [x] Seluruh proses berat memblokir input UI dengan *Loading Bar* (berjalan *Asynchronous*).

---

## DETAIL LANGKAH KERJA (FASE 1 - TAHAP 2: Integrasi Firebase & Pembaruan UI)

### Langkah 2.1: Setup Firebase & Credential Manager
Mengintegrasikan Firebase Authentication dan Google Sign-in untuk identitas pengguna.
* **Tugas:**
    - [x] Tambahkan dependensi `google-services`, `firebase-auth`, dan `androidx.credentials`.
    - [x] Konfigurasi `google-services.json` dan daftarkan SHA-1 aplikasi di Firebase Console.
    - [x] Buat file `strings.xml` untuk menyimpan `google_web_client_id`.

### Langkah 2.2: Pembuatan UI/UX Baru
Mengubah antarmuka aplikasi untuk mengakomodasi fitur keamanan.
* **Tugas:**
    - [x] Ganti kartu "Word to PDF" dan "Excel to PDF" di `DashboardScreen` menjadi "Tandatangani" dan "Cek Keaslian" (Status: Tahap Pengembangan).
    - [x] Perbaiki *color palette* untuk *Light Theme* agar kontras teks pada kontainer terbaca jelas.
    - [x] Buat layar `LoginScreen.kt` dengan integrasi Credential Manager.
    - [x] Tambahkan tampilan detail akun dan tombol *Logout* di layar `SettingsScreen`.

### Langkah 2.3: Pembaruan Navigasi & Alur Aplikasi
* **Tugas:**
    - [x] Implementasi pemeriksaan sesi pada `SplashViewModel`.
    - [x] Mengarahkan pengguna yang belum *login* ke layar *Login*, dan yang sudah *login* ke `Dashboard`.

## STATUS FASE 1:
**SELESAI 100%** (Siap melanjutkan ke Fase 2: Pembuatan AI)