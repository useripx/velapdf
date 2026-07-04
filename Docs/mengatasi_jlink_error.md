# Mengatasi Error "jlink executable does not exist" di Antigravity IDE / VS Code

Error `Cause: jlink executable ...\redhat.java-...\jre\...\bin\jlink.exe does not exist` sering terjadi ketika Anda mengembangkan aplikasi Java atau Android di dalam IDE yang menggunakan ekstensi RedHat Java (seperti Antigravity IDE atau VS Code). 

## 🔍 Mengapa ini terjadi?
Ekstensi RedHat Java secara otomatis mengunduh **JRE (Java Runtime Environment)** berukuran kecil yang digunakan secara khusus untuk menjalankan *Language Server* (fitur autocompletion, syntax checking). JRE versi minimalis ini **tidak memiliki** alat-alat kompilasi lengkap yang ada di **JDK (Java Development Kit)**, salah satunya adalah `jlink.exe` atau `javac`.

Ketika Gradle mencoba melakukan *build* atau *sync* melalui IDE, IDE terkadang secara keliru memberikan *path* JRE bawaan ekstensi ini sebagai Java runtime untuk Gradle. Akibatnya, saat Gradle atau plugin (terutama toolchain Java) membutuhkan utilitas pengembangan (seperti `jlink`), proses tersebut gagal.

---

## 🛠️ Cara Mengatasinya

Berikut adalah beberapa cara yang bisa Anda gunakan untuk mengatasi error ini. Anda cukup memilih salah satu yang paling sesuai untuk Anda.

### Metode 1: Mengatur `org.gradle.java.home` di `gradle.properties` (Sangat Direkomendasikan)
Beri tahu Gradle secara eksplisit untuk menggunakan JDK penuh (contohnya yang dipaketkan bersama Android Studio), alih-alih menggunakan JRE minimalis dari IDE.

1. Buka file `gradle.properties` (berada di *root* proyek Anda).
2. Tambahkan atau perbarui baris konfigurasi berikut:
   ```properties
   # Sesuaikan path ini dengan lokasi instalasi Android Studio atau JDK di Windows Anda
   org.gradle.java.home=C:\\Program Files\\Android\\Android Studio\\jbr
   ```
   *(Catatan: Anda harus menggunakan format _double backslash_ `\\` pada sistem Windows).*
3. Simpan file tersebut, lalu jalankan ulang *build* atau sinkronisasi.

### Metode 2: Mengatur JDK pada Settings IDE (`settings.json`)
Anda juga dapat mencegah ekstensi Java agar tidak menggunakan JRE internal miliknya sendiri dan memaksanya menggunakan JDK terinstal yang lengkap.

1. Buka **Settings** di IDE (biasanya dengan menekan `Ctrl` + `,`).
2. Cari pengaturan `java.home` atau buka file *JSON settings* (biasanya ada ikon kertas dengan panah di kanan atas untuk membuka `settings.json`).
3. Tambahkan konfigurasi ini di dalam objek JSON Anda:
   ```json
   "java.jdt.ls.java.home": "C:\\Program Files\\Android\\Android Studio\\jbr",
   "java.configuration.runtimes": [
     {
       "name": "JavaSE-21",
       "path": "C:\\Program Files\\Android\\Android Studio\\jbr",
       "default": true
     }
   ]
   ```
4. *Restart* (muat ulang) IDE Anda.

### Metode 3: Mengatur Environtment Variable `JAVA_HOME` Global
Jika IDE bergantung pada *environment variable* sistem operasi Anda dan sistem Anda diarahkan ke JRE yang salah, perbaiki di level OS:

1. Buka **Start Menu** Windows, cari "Environment Variables".
2. Klik **Edit the system environment variables**.
3. Klik tombol **Environment Variables...**.
4. Di bagian *User variables* atau *System variables*, cari variabel `JAVA_HOME`.
5. Ubah nilainya menjadi folder JDK yang valid (Misal: `C:\Program Files\Android\Android Studio\jbr`).
6. Buka ulang IDE (harus *restart* total agar membaca variabel baru).

---

**💡 Tips Penting:** 
Seringkali proses *Gradle Daemon* masih menyimpan / memakai runtime Java yang lama meskipun Anda sudah mengubahnya. Setelah Anda menerapkan salah satu dari solusi di atas, sangat disarankan untuk menghentikan Daemon Gradle di terminal sebelum memulai *build* ulang:

```bash
./gradlew --stop
```
Lalu coba jalankan proyek Anda lagi!
