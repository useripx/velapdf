# VelaPDF - Android Native Implementation Guide (MVVM)

Aplikasi ini dirancang sebagai aplikasi Android Native modern menggunakan **Kotlin**, **MVVM architecture**, dan **ViewBinding**. Proses konversi Image-to-PDF menggunakan library bawaan Android `PdfDocument`.

## 1. Project Architecture (MVVM)

- **Model**: Data class untuk State UI (Loading, Success, Error).
- **View**: `SplashActivity`, `DashboardActivity`, `ImageToPdfActivity`.
- **ViewModel**: `ImageToPdfViewModel` mengelola logika bisnis dan state menggunakan `LiveData` atau `StateFlow`.
- **Repository/Utility**: `PdfConverter` menangani penggambaran bitmap ke `PdfDocument` di background thread (`Dispatchers.IO`).

## 2. Core Conversion Logic (Kotlin)

Fungsi utama menggunakan `android.graphics.pdf.PdfDocument`:

```kotlin
fun convertImageToPdf(bitmap: Bitmap, outputStream: OutputStream) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    
    val canvas = page.canvas
    canvas.drawBitmap(bitmap, 0f, 0f, null)
    
    pdfDocument.finishPage(page)
    pdfDocument.writeTo(outputStream)
    pdfDocument.close()
}
```

## 3. UI Implementation Details

- **Splash Screen**: Menampilkan logo Vela dan nama aplikasi selama 2 detik menggunakan `lifecycleScope.launch` + `delay(2000)`.
- **Dashboard**: Menggunakan `RecyclerView` atau layout statis dengan CardView. Badge "Tahap Pengembangan" menggunakan label tekstual pada elemen yang dinonaktifkan.
- **Image to PDF**: Menggunakan `ActivityResultContracts.GetContent()` untuk pemilihan gambar dan Scoped Storage (MediaStore) untuk menyimpan hasil PDF ke folder Downloads.
