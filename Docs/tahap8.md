# Dokumentasi Tahap 8: PDF to Image Feature

## Ringkasan Fitur
Fitur **PDF to Image** memungkinkan pengguna untuk mengubah dokumen PDF menjadi format gambar (JPG, PNG, atau WebP) berkualitas tinggi.

### Kemampuan Fitur:
- **Pendeteksi Cerdas Halaman**: Jika PDF hanya memiliki 1 halaman, akan langsung dikonversi menjadi gambar (tanpa ZIP).
- **Opsi Arsip ZIP**: Otomatis menyala jika PDF memiliki lebih dari 1 halaman, sehingga semua gambar digabung dalam 1 file ZIP dengan kompresi tingkat tinggi.
- **Save As Mode**: Memungkinkan pengguna untuk memilih folder spesifik penyimpanan gambar atau ZIP.
- **Lokasi Default**: Jika Save As tidak digunakan, file akan otomatis tersimpan di dalam folder `Documents/VelaPDF/`. Jika konversi banyak halaman dilakukan tanpa ZIP, maka akan membuat sub-folder baru dengan nama dokumen tersebut.
- **Pilihan Ekstensi Gambar**: Tersedia format ekspor gambar JPG, PNG, dan WebP.

## Struktur File
- **`ui/screen/PdfToImageScreen.kt`**: UI utama yang menampilkan tombol fungsi, drop-down ekstensi, toggle ZIP, toggle Save As, serta progres bar animasi saat proses konversi berlangsung.
- **`ui/viewmodel/PdfToImageViewModel.kt`**: Logic pengolah state.
- **`utils/PdfToImageEngine.kt`**: Mesin di belakang layar yang merender setiap halaman PDF menggunakan `android.graphics.pdf.PdfRenderer` ke dalam bentuk gambar (Bitmap) lalu diubah menjadi Output Stream.
- **`utils/NotificationHelper.kt` & `utils/FileUriHelper.kt`**: Memunculkan Notifikasi In-App dan Sistem untuk aksi *intent* klik setelah konversi berhasil diselesaikan tanpa melanggar `FileUriExposedException`.
- **`ui/screen/SuccessScreen.kt`**: Mendeteksi `MIME Type` apakah tipe sukses ini berupa Gambar, ZIP, PDF, atau Direktori/Folder (ketika hasil render multi-gambar bukan dalam bentuk ZIP).

## Cara Melakukan Push Pull Request (PR) ke GitHub
Untuk menerapkan seluruh perubahan ini ke repositori Anda di GitHub melalui Git CLI atau terminal Android Studio, ikuti langkah berikut:

1. **Pastikan berada di Branch yang Tepat**
   Periksa branch saat ini:
   ```bash
   git branch
   ```
   Pastikan Anda berada di branch `feature/pdftoimage`. Jika belum, pindah menggunakan:
   ```bash
   git checkout feature/pdftoimage
   ```

2. **Tambahkan dan Commit Perubahan**
   ```bash
   git add .
   git commit -m "feat: implement PDF to Image feature with ZIP compression and safe URI intents"
   ```

3. **Push ke GitHub (Origin)**
   ```bash
   git push origin feature/pdftoimage
   ```
   Jika Git meminta Anda untuk mengaitkan branch ke origin, gunakan perintah:
   ```bash
   git push --set-upstream origin feature/pdftoimage
   ```

4. **Buat Pull Request (PR) melalui Browser**
   - Buka repositori proyek Anda di GitHub.
   - GitHub secara otomatis akan menampilkan notifikasi kuning bertuliskan **"Compare & pull request"** untuk branch `feature/pdftoimage`.
   - Klik tombol tersebut.
   - Tambahkan judul: `Feature: PDF to Image Converter`
   - Berikan keterangan singkat, kemudian klik **Create pull request**.
   - Setelah selesai direview, klik **Merge pull request** untuk menggabungkannya ke branch `main`.
