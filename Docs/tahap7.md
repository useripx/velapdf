# Rencana Implementasi: Tahap 7 - PDF Generation Engine (Backend)

Sesuai dengan instruksi Tahap 7, kita akan membangun *engine* pembuatan PDF lokal beserta antarmuka pemuatan (*loader*) dan layar sukses konversi.

## Branch Terkait

- **Branch:** `feature/Backend-pdf-engine`
- **Commit Pattern:** `feat: add pdf generation engine`

## Tujuan (Goals)

1. **Binding State Konversi ke UI Loader**: Menampilkan *Progress bar* 0-100% secara *real-time* kepada pengguna saat konversi berlangsung.
2. **PDF Engine Builder**: Melakukan proses pemisahan/penulisan bitmap cache lokal (`SelectedImage`) ke dalam format Canvas Halaman PDF melalui `android.graphics.pdf.PdfDocument`.
3. **Mengaplikasikan Icon**: Memastikan *icon.png* (`R.drawable.logo`) teraplikasikan pada animasi kesuksesan konversi atau halaman *Converter* sesuai desain.

---

## 1. Modul Backend (Engine & Repository)

#### [NEW] `utils/PdfGenerator.kt`

- Berisi kelas statis / utilitas untuk menangani pembuatan PDF secara *background thread* (`Dispatchers.IO`).
- Fungsi `generatePdfFromImages(context, images, onProgress)` akan mengambil daftar `SelectedImage`, menyiapkan `PdfDocument`, melakukan iterasi untuk memuat `Bitmap`, menskalakan gambar agar pas di kanvas halaman A4/fleksibel, menulis ke aliran berkas sistem (*FileOutputStream* di MediaStore/Cache), dan menerbitkan pembaruan persentase (progress).

#### [NEW] `data/repository/PdfRepository.kt`

- Kelas *Repository Pattern* untuk membungkus `PdfGenerator`.
- Menjadikan aliran konversi sebagai `Flow<PdfGenerationState>` dengan status `Idle`, `Loading(progress)`, `Success(Uri)`, dan `Error(Exception)`.

---

## 2. Implementasi Antarmuka UI (Endpoint)

Berdasarkan *endpoint* arahan, kita akan menyematkan 2 layar *View* ini di dalam blok fungsional:

#### [NEW] `ui/view/ConverterScreen.kt`

- Halaman UI yang menampilkan status `Loading(...)` dengan *Progress Bar* / *Circular Progress Indicator*.
- Memuat teks persentase dinamis (Mulai dari 0% hingga 100%) berdasarkan status dari `PdfRepository`.
- Menghalangi fungsi "Back" saat proses konversi (*prevent accidental cancellation*) lewat `BackHandler`.

#### [NEW] `ui/view/SuccessScreen.kt`

- Ditampilkan saat `PdfGenerationState` mencapai `Success`.
- Berisi ikon aplikasi (`icon.png` via `R.drawable.logo`), pesan keberhasilan "Conversion Successful".
- Tombol:
  - **Open PDF**: Membuka file *output* menggunakan aplikasi *Viewer* eksternal.
  - **Back to Dashboard**: Membersihkan sistem dan memandu pengguna ke layar beranda.

---

## 3. Pembaruan Navigasi & Dokumentasi

#### [MODIFY] `ui/navigation/NavGraph.kt`

- Menginjeksi *route* baru untuk navigasi ke `ConverterScreen` dan pengalihan ke `SuccessScreen`.

#### [MODIFY] `Docs/walkthrough.md`

- Di akhir eksekusi nanti, bagian `Tahap 7` akan dicatatkan pada file `walkthrough.md` sesuai dengan kerangka format rujukan:
  ```markdown
  Tahap 7
  PDF Generation Engine (Backend)
  Tanggal: [Hari Ini]
  Branch: feature/Backend-pdf-engine
  Task: ...
  Endpoint: ...
  Commit: feat: add pdf generation engine
  ```

---

> [!IMPORTANT]
> **Persetujuan Pengguna Diperlukan**
> Harap konfirmasi rancangan pengembangan ini sebelum saya mulai melakukan modifikasi *source code* dan memicu logika konversi Android PDF! Ketik **"Lanjut"** jika struktur *Endpoint* dan *Repository* sudah sesuai dengan instruksi yang diharapkan.
>
