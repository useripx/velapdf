# Roadmap & Checklist Pengembangan Vela PDF
**(Keamanan Lapis Ganda: AI CNN + Kriptografi RSA/SHA-256)

### Fase 1: Persiapan Dasar & Sistem Akun (Aplikasi)
- [x] **1. Memantapkan Fitur Dasar:** Menyelesaikan fungsionalitas utama aplikasi Vela PDF (Pemindai *Picture to PDF*) yang sudah dikembangkan sebelumnya.
- [x] **2. Integrasi Firebase Auth:** Menambahkan Firebase Authentication untuk fitur *Login/Register* agar sistem memiliki data nama dan *email* pengguna.
- [x] **3. Desain UI/UX:** Membangun antarmuka dasar untuk fitur utama, khususnya tombol "Tandatangani" dan "Cek Keaslian".

### Fase 2: Pembuatan AI Deteksi Pemalsuan Gambar (Lapis Keamanan 1)
- [ ] **4. Pengumpulan Data Asli:** Mengumpulkan 750 gambar dokumen ASLI (KTP, Nota, Ijazah, dll) hasil jepretan kamera murni.
- [ ] **5. Pembuatan Data Palsu:** Memanipulasi/mengedit 750 gambar asli tersebut di Photoshop untuk menghasilkan 750 gambar PALSU (Total dataset: 1.500 gambar).
- [ ] **6. Persiapan Environment:** Membuat akun Gmail khusus skripsi untuk *storage* Google Drive (15 GB) dan komputasi awan Google Colab.
- [ ] **7. Penulisan Kode AI (Python):** Membuat *script* untuk pemrosesan ELA (*Error Level Analysis*) dan algoritma *Convolutional Neural Network* (CNN) menggunakan TensorFlow/Keras (Otomatis me-resize gambar menjadi standar AI).
- [ ] **8. Training Model:** Melatih AI di Google Colab menggunakan *dataset* yang telah dibuat hingga mencapai akurasi optimal (target: >90%), menggunakan fitur *ModelCheckpoint* agar aman.
- [ ] **9. Export Model:** Mengonversi dan menyimpan model AI yang sudah terlatih ke dalam format `.tflite` (TensorFlow Lite) agar ukurannya ringan (3-15 MB).

### Fase 3: Pembuatan Kriptografi & Tanda Tangan (Lapis Keamanan 2)
- [ ] **10. Algoritma SHA-256:** Membuat *script/library* untuk memproses *Hash* SHA-256 (membuat sidik jari digital dokumen).
- [ ] **11. Algoritma RSA:** Membuat *script/library* untuk men-*generate* Kunci Publik (*Public Key*) dan Kunci Privat (*Private Key*) secara lokal di perangkat pengguna.
- [ ] **12. Identity Binding (X.509):** Membuat kode untuk menggabungkan profil pengguna (dari sesi Firebase) dan *Public Key* ke dalam sebuah Sertifikat Digital.
- [ ] **13. Metadata Embedding:** Memprogram logika penyisipan hasil kriptografi (Tanda Tangan & Sertifikat) langsung ke dalam *metadata* atau *Signature Dictionary* file PDF.
### Fase 4: Integrasi Keseluruhan (Penyatuan Sistem & UI Lanjutan)
- [ ] **14. Implementasi On-Device AI & Offline Mode:** Menanamkan *file* model `.tflite` dan logika kriptografi ke dalam aplikasi Android agar 99% proses verifikasi berjalan murni *offline* tanpa internet (kecuali proses *Login*).
- [ ] **15. Pembuatan Alur Pipa Keamanan:** Menyambungkan *flow* sistem secara berurutan: `Kamera -> AI Mengecek (Tolak jika editan) -> Konversi ke PDF -> Penguncian dengan RSA & SHA-256`.
- [ ] **16. Pengembangan UI Panel Sukses (Anti Re-Sign):** Membuat panel "Cek Keaslian" yang WAJIB memunculkan informasi dinamis saat dokumen valid: Status Asli, Nama Penandatangan, Email, dan Waktu Penandatanganan.
- [ ] **17. Pengembangan UI Panel Penolakan & Visualisasi Bukti [FITUR BARU]:** Membangun panel interaktif yang menjelaskan detail penolakan (contoh: Gagal Integritas Hash vs Anomali Piksel), serta menambahkan tombol **[Lihat Bukti Peta ELA]** yang memunculkan letak gambar/angka editan yang menyala terang.
### Fase 5: Pengujian Akademis (Syarat Skripsi & Jurnal Sinta 3)
- [ ] **18. Uji Akurasi AI:** Melakukan pengetesan model AI menggunakan gambar dokumen baru yang belum pernah dilihat AI pada saat *training*.
- [ ] **19. Uji Waktu Komputasi:** Mengukur durasi (dalam milidetik) proses RSA dan SHA-256 untuk berbagai ukuran *file* PDF (contoh: 1 MB, 3 MB, 5 MB).
- [ ] **20. Uji Avalanche Effect:** Membuktikan kekuatan SHA-256 dengan mengubah 1 karakter pada dokumen dan mencatat persentase perubahan *hash* (harus berubah drastis).
- [ ] **21. Black Box & UI/UX Testing:** Menguji seluruh tombol, skenario navigasi, fitur *offline*, dan ketahanan aplikasi agar tidak terjadi *force close* saat digunakan oleh pengguna awam.

---

# Kemungkinan Kendala
*(Daftar hambatan teknis yang berpotensi terjadi beserta solusi mitigasinya selama pengerjaan proyek Vela PDF)*

**1. Fase Pembuatan AI (Beban Dataset & Risiko Overfitting)**
*   **Kendala:** Mengedit 750 gambar menjadi data palsu di Photoshop secara manual membutuhkan waktu dan tenaga yang sangat besar. Selain itu, ada risiko AI mengalami *Overfitting* (AI hanya menghafal *dataset*), sehingga ketika pengguna memfoto dokumen asli dengan pencahayaan gelap, AI malah menganggapnya sebagai dokumen palsu/editan.
*   **Solusi:** Manfaatkan fitur *Actions* (makro otomatis) di Photoshop untuk mempercepat proses *copy-paste* teks/stempel ke ratusan gambar sekaligus. Untuk mencegah *Overfitting*, pastikan 750 foto dokumen asli diambil dengan berbagai variasi pencahayaan (terang, remang, atau terdapat bayangan) agar AI memahami bahwa kondisi foto buram/gelap itu wajar, bukan hasil manipulasi.

**2. Fase Kriptografi (Modifikasi Struktur PDF yang Rentan)**
*   **Kendala:** Terdapat risiko *file* PDF menjadi *corrupt* atau rusak. Upaya menyisipkan Sertifikat Digital (X.509), Kunci Publik, dan *Hash* langsung ke dalam struktur *byte* PDF secara manual sangat rentan menghasilkan pesan *error* "File is corrupted or damaged" saat pengguna membuka dokumen.
*   **Solusi:** Dilarang keras merakit atau memodifikasi struktur PDF dari nol secara manual. Tim pengembang wajib menggunakan *library* standar industri yang aman dan teruji untuk memanipulasi PDF di Android, seperti **iText7** (iText Core) atau **Apache PDFBox**.

**3. Fase Integrasi Aplikasi (Limitasi Performa Smartphone)**
*   **Kendala:** Aplikasi berpotensi mengalami *Crash* atau *Out of Memory* (OOM). Alur kerja yang mengeksekusi banyak proses berat sekaligus—membuka kamera resolusi tinggi, mengubah gambar ke ELA, menjalankan komputasi AI `.tflite`, melakukan konversi PDF, dan mengunci dengan enkripsi RSA—akan sangat membebani RAM pada perangkat Android berspesifikasi rendah.
*   **Solusi:** Wajib menerapkan fungsi *resize* pada gambar yang ditangkap kamera sebelum dimasukkan ke dalam pemrosesan AI. Pastikan juga semua beban komputasi berjalan menggunakan *Background Thread* atau *Coroutines* (bukan di layar utama/ *Main Thread*), serta sediakan antarmuka *Loading* yang interaktif agar layar tidak *freeze*.

**4. Fase Visualisasi (Hambatan Pemrosesan Peta ELA di Android)**
*   **Kendala:** Mengonversi algoritma *Error Level Analysis* (ELA) dari bahasa Python untuk bisa berjalan secara *native* dan menampilkannya sebagai "Peta Bercak Menyala" secara *offline* menggunakan Java/Kotlin di Android merupakan tugas yang cukup menantang.
*   **Solusi:** Pengembang aplikasi harus mengimplementasikan *library* **OpenCV for Android**. OpenCV memiliki kemampuan dan fungsi bawaan untuk mengekstraksi tingkat kualitas kompresi JPEG, sehingga dapat digunakan untuk memproses dan merender Peta ELA langsung di dalam perangkat pengguna.

