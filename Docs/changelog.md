# Changelog - VelaPDF

Semua perubahan yang signifikan pada proyek ini akan didokumentasikan dalam file ini.

## [Unreleased] - Fase 1 Update

### Ditambahkan (Added)
- **Firebase Authentication**: Implementasi Google Sign-In menggunakan `androidx.credentials.CredentialManager` (Android 14+ support).
- **Google Services JSON**: Konfigurasi `google-services.json` ke Firebase untuk mendukung fitur autentikasi, serta penyiapan `google_web_client_id`.
- **Layar Login (LoginScreen)**: Desain UI baru untuk otentikasi akun pengguna sebelum mengakses fitur utama (Dashboard).
- **Session Management**: Penambahan logika pengecekan sesi di `SplashViewModel`. Jika pengguna belum login, akan diarahkan ke `LoginScreen`, dan jika sudah login, diarahkan ke `DashboardScreen`.
- **Integrasi iText7 Core**: Mengganti mesin pemrosesan PDF bawaan Android dengan `itext7-core` versi `7.2.5` demi kestabilan dokumen dan mendukung enkripsi sertifikat kriptografi X.509 di masa depan.
- **UI Akun Pengguna**: Tambahan tombol *Logout* (Keluar) pada layar `SettingsScreen`.

### Diubah (Changed)
- **Desain Ulang Dashboard**: Kartu (card) yang sebelumnya adalah "Word to PDF" dan "Excel to PDF" diganti menjadi "Tandatangani" dan "Cek Keaslian" lengkap dengan status label "TAHAP PENGEMBANGAN".

### Diperbaiki (Fixed)
- **Tema Terang (Light Theme) Kontras Warna**: Memperbaiki variabel warna pada *Material Theme* (`Color.kt`) seperti `primaryContainer` agar tidak meminjam nilai dari Mode Gelap. Bug teks tidak terbaca (kontras bertabrakan) pada lencana/label status di layar *Dashboard* berhasil diperbaiki.
