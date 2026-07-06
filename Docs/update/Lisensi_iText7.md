# Kebijakan & Justifikasi Penggunaan Lisensi iText7

Aplikasi **Vela PDF** menggunakan *library* **iText7 Core** (`com.itextpdf:itext7-core`) untuk memproses, membuat, dan memanipulasi file PDF (terutama untuk penyisipan sertifikat digital X.509 dan enkripsi SHA-256 pada Fase Kriptografi).

## Mengapa iText7?
Sesuai dengan *Checklist VelaPDF*, pengembang dilarang keras merakit struktur *byte* PDF dari nol secara manual. Hal ini karena:
1. Modifikasi PDF secara manual rentan menyebabkan *file* korup atau rusak (`File is corrupted or damaged`).
2. Proses penanaman *Digital Signature* (Tanda Tangan Digital) yang valid memerlukan standar industri yang sesuai dengan spesifikasi ISO 32000-1 (PDF). iText7 adalah *library* paling stabil, aman, dan mutakhir untuk kebutuhan spesifik ini di platform Java/Android.

## Detail Lisensi (AGPLv3)
*Library* iText7 didistribusikan secara *open-source* di bawah lisensi **AGPL (Affero General Public License) v3**.

### Apa Implikasinya?
1. **Gratis untuk Penggunaan Akademis/Non-Komersial**: Anda diizinkan menggunakan iText7 secara gratis (tanpa membeli lisensi komersial) asalkan aplikasi ini ditujukan untuk keperluan riset, skripsi, atau proyek *open-source*.
2. **Kewajiban Distribusi Kode Sumber**: Jika VelaPDF nantinya didistribusikan ke publik (misalnya di-*upload* ke Google Play Store) sebagai aplikasi *open-source*, maka seluruh kode sumber (*source code*) VelaPDF harus dibuka dan dilisensikan di bawah AGPLv3 juga.
3. **Penggunaan Komersial**: Jika di masa depan VelaPDF ingin dimonetisasi (dijual atau diberi iklan) sebagai aplikasi tertutup (*closed-source*), maka pengembang **wajib** membeli lisensi komersial berbayar dari iText Group.

## Kesimpulan
Untuk tahapan **Skripsi dan Penelitian Akademis**, penggunaan iText7 **sangat aman, legal, dan sangat direkomendasikan** karena tidak melanggar ketentuan komersial. Jika proyek ini berlanjut menjadi produk komersial, disarankan untuk membeli lisensi komersial iText atau bermigrasi ke *library* alternatif bersumber terbuka permisif (seperti Apache PDFBox, meskipun dukungan PDFBox di Android sedikit lebih terbatas untuk manipulasi enkripsi PDF tingkat lanjut).
