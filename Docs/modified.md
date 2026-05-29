# Log Modifikasi File VelaPDF

Dokumen ini mendata seluruh jejak modifikasi atau pembuatan file berdasarkan tahap-tahap pengembangan yang dilakukan. Hal ini bertujuan untuk mempermudah pelacakan jika terjadi regresi (bug/kesalahan) atau sinkronisasi dengan tim pengembang lainnya.

---

### Tahap 1: Project Setup, Manifest, & UI Theme

Tahap ini berfokus pada pendirian pengaturan awal kompilasi Gradle, penyesuaian dependensi Hilt-*Dependency Injection*, serta konfigurasi arsitektur warna dan tema berbasis standar Material 3.

| File yang Diedit / Dibuat | Penjelasan / Detail Modifikasi |
| --- | --- |
| `gradle/libs.versions.toml` | Meng-upgrade versi ekstensi `Hilt` ke `2.57.1` agar tidak bentrok dengan *Kotlin* (`kotlinx-metadata-jvm:2.2.0`). |
| `app/build.gradle.kts` | Menambahkan pengaturan kompilasi `kotlinOptions { jvmTarget = "11" }` untuk stabilitas mesin Java dan sinkronisasi Hilt (`kapt`). |
| `app/src/main/AndroidManifest.xml` | Menambahkan rujukan ke antarmuka _VelaPDFTheme_ dan menghapus atribut *package* tingkat akar yang *deprecated*. |
| `[...]/VelaPDFApplication.kt` | Mengonfirmasi inisialisasi awal arsitektur dengan anotasi wajib pengenal `@HiltAndroidApp`. |
| `[...]/ui/view/MainActivity.kt` | Memindahkan kelas utama aplikasi ke tata kelola folder *Clean Architecture* dan menambahkan anotasi *setup* `@AndroidEntryPoint`. |
| `[...]/ui/theme/Color.kt` | Mendeskripsikan secara eksplisit seluruh _Color Map_ referensial (Surface, Text, Background) sesuai `design.md`. |
| `[...]/ui/theme/Theme.kt` | Menerapkan struktur penerapan warna statis material terang *Light Mode* pada tata kelola antarmuka proyek. |
| `[...]/ui/theme/Type.kt` | Mendefinisikan dasar-dasar tipografi yang mencakup kelas penamaan _Headline, Body,_ dan _Label_ (segera diikat dengan *font* *Inter*). |
| `Beragam File Placeholder Kosong` | Mengisi deklarasi `package ...` pada 17 buah skrip kosong kerangka bawaan awal untuk mencegah pembacaan malfungsi *Compiler*. |

---

### Tahap 2: Frontend Navigation & Splash Screen

Tahap ini mencakup pembungkusan UI menjadi sebuah _Graph_ (rute hierarkis sistematis) serta pembuatan Splash Screen beranimasi untuk layar permulaan. Kita juga mengintegrasikan sistem permintaan izin (Permissions).

| File yang Diedit / Dibuat | Penjelasan / Detail Modifikasi |
| --- | --- |
| `app/src/main/res/drawable/logo.png` | Menyalin *copy* gambar mentah `/Docs/img/icon.png` sebagai wadah _Drawable Asset Resource_ yang bisa merender logo utama secara internal. |
| `[...]/data/preferences/PreferencesManager.kt` | Menerapkan `DataStore<Preferences>` yang digunakan untuk merekam _state record_ boolean persetujuan (*Permissions_Granted*). |
| `[...]/ui/navigation/NavGraph.kt` | Mengonstruksikan `NavHost` untuk me-render peralihan layar rute mulai dari `Splash` -> `Permissions` -> ke `Dashboard`. |
| `[...]/ui/view/MainActivity.kt` | Menstimulasi konektivitas langsung fungsi hierarki utama UI ke skema navigasi dasar (mengganti rute *Hello World* awal dengan blok modul `AppNavigation()`). |
| `[...]/ui/viewmodel/SplashViewModel.kt` | Memberikan pengatur *Delay Timer* muat memori (2 detik) & melakukan _logic check_ arah destinasi rute (tergantung riwayat *record* perizinan DataStore). |
| `[...]/ui/screen/SplashScreen.kt` | Menyusun layar muka pertama, mengatur parameter fungsi animasi statis *Compose* pudar dan pembesaran logo serta memuat elemen bar muat statis bernama `LoadingShimmer()`. |
| `[...]/ui/screen/PermissionsViewModel.kt` | Mengikat jembatan arsitektur pemicuan sinkron antara permintaan perizinan Halaman UI ke _database_ internal (DataStore). |
| `[...]/ui/screen/PermissionsScreen.kt` | Memuat koding halaman responsif _Accompanist Permissions_. Digunakan untuk menampilkan Pop-Up izin `CAMERA`, `WRITE_EXTERNAL_STORAGE`, `READ_EXTERNAL_STORAGE` sebagai otorisasi pemindaian PDF awal. |
| `[...]/ui/screen/DashboardScreen.kt` | Pengerjaan rute dummy (*Placeholder Screen*) sederhana sebagai sarana singgah sementara setelah proses orientasi Tahap 2 selesai. |
