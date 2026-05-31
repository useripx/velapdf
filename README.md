<p align="center">
  <img src="https://img.icons8.com/fluency/96/pdf-2.png" alt="VelaPDF Logo" width="96" height="96"/>
</p>

<h1 align="center">📄 VelaPDF</h1>

<p align="center">
  <strong>A Modern PDF Toolkit for Android</strong>
</p>

<p align="center">
  <a href="https://developer.android.com"><img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Platform Android"/></a>
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-2.0+-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"/></a>
  <a href="https://developer.android.com/jetpack/compose"><img src="https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose"/></a>
  <a href="https://github.com/useripx/velapdf"><img src="https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge" alt="License MIT"/></a>
</p>

<p align="center">
  <a href="https://github.com/useripx/velapdf/stargazers"><img src="https://img.shields.io/github/stars/useripx/velapdf?style=social" alt="GitHub Stars"/></a>
  <a href="https://github.com/useripx/velapdf/network/members"><img src="https://img.shields.io/github/forks/useripx/velapdf?style=social" alt="GitHub Forks"/></a>
  <a href="https://github.com/useripx/velapdf/issues"><img src="https://img.shields.io/github/issues/useripx/velapdf?color=red" alt="GitHub Issues"/></a>
  <a href="https://github.com/useripx/velapdf/pulls"><img src="https://img.shields.io/github/issues-pr/useripx/velapdf?color=blue" alt="Pull Requests"/></a>
  <img src="https://img.shields.io/github/last-commit/useripx/velapdf?color=green" alt="Last Commit"/>
  <img src="https://img.shields.io/github/repo-size/useripx/velapdf?color=orange" alt="Repo Size"/>
</p>

---

## ✨ Overview

**VelaPDF** is a modern Android application built for managing, viewing, and converting PDF documents. Designed with a clean MVVM architecture and powered by Jetpack Compose, VelaPDF delivers a smooth and intuitive user experience.

---

## 🚀 Features

- 📖 **PDF Viewer** — View PDF documents with smooth rendering
- 🖼️ **Image to PDF** — Convert images to PDF format
- 📝 **PDF Editor** — Basic PDF editing capabilities
- 🔄 **File Converter** — Convert between various document formats
- 🗂️ **File Manager** — Organize and manage your PDF files
- 🌙 **Dark Mode** — Beautiful dark theme support

---

## 🏗️ Architecture

VelaPDF follows the **MVVM (Model-View-ViewModel)** architecture pattern with clean separation of concerns:

```
📦 com.velapdf.app
├── 📂 data/          # Data layer (repositories, data sources, models)
├── 📂 di/            # Dependency Injection modules
├── 📂 domain/        # Domain layer (use cases, entities)
├── 📂 ui/            # Presentation layer
│   ├── 📂 screen/    # UI Screens (Composables)
│   ├── 📂 theme/     # Material Design theme
│   └── 📂 viewmodel/ # ViewModels
└── 📂 utils/         # Utility classes and extensions
```

---

## 🛠️ Tech Stack

| Technology | Description |
|---|---|
| **Kotlin** | Primary programming language |
| **Jetpack Compose** | Modern UI toolkit |
| **Material Design 3** | Design system |
| **MVVM** | Architecture pattern |
| **Gradle KTS** | Build system |

---

## 📋 Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK** 17 or higher
- **Android SDK** API 24+ (minimum)
- **Kotlin** 2.0+

---

## ⚡ Getting Started (Tutorial untuk Pemula)

Ikuti langkah-langkah di bawah ini untuk mengunduh dan menjalankan project ini di komputermu, terutama jika kamu masih pemula:

### Langkah 1: Persiapan (Prerequisites)
Pastikan hal-hal berikut sudah ter-install di komputermu:
1. **Git**: Unduh dan install dari [git-scm.com](https://git-scm.com/).
2. **Android Studio**: Unduh dan install Android Studio versi terbaru dari [developer.android.com/studio](https://developer.android.com/studio).

### Langkah 2: Clone (Unduh) Repository
Buka terminal (Command Prompt/PowerShell/Git Bash di Windows, atau Terminal di Mac/Linux), lalu jalankan perintah berikut secara berurutan:

```bash
# 1. Buat Folder Project di C:\Users\Username\AndroidStudioProjects dengan nama Folder VelaPDF

# 2. Unduh kode dari GitHub
git clone https://github.com/useripx/velapdf.git

# 3. Masuk ke folder project
cd velapdf
```

### Langkah 3: Buka Project di Android Studio
1. Buka aplikasi **Android Studio**.
2. Pada layar utama (Welcome to Android Studio), klik menu **Open** (atau "Open an Existing Project").
3. Cari dan pilih folder `velapdf` hasil clone tadi, lalu klik **OK**.
4. **Tunggu (Penting):** Android Studio akan mulai melakukan proses *Gradle Sync* dan mendownload semua hal yang dibutuhkan (library, dependencies, dll). Tunggu sampai ada tulisan "Sync System Completed" dan tidak ada loading bar di pojok kanan bawah. Pastikan internetmu aktif dan stabil.

### Langkah 4: Jalankan Aplikasi
1. Colokkan HP Android ke laptop/PC (pastikan mode *Developer Options* & *USB Debugging* di HP sudah aktif) **ATAU** jalankan Emulator bawaan dari Android Studio.
2. Di pojok atas Android Studio, pastikan nama modulnya terpilih sebagai `app` dan target devicenya sudah muncul.
3. Klik tombol **Play (▶️ Run 'app')** berwarna hijau di panel atas (shortcut `Shift + F10`).
4. Tunggu proses build selesai (bisa memakan waktu beberapa menit saat pertama kali), dan aplikasi akan otomatis terbuka di HP/Emulator.

---

## 📂 Project Structure

```
VelaPDF/
├── app/                    # Main application module
│   ├── src/
│   │   └── main/
│   │       ├── java/       # Kotlin source code
│   │       └── res/        # Resources (layouts, drawables, etc.)
│   ├── build.gradle.kts    # App-level build config
│   └── proguard-rules.pro  # ProGuard rules
├── gradle/                 # Gradle wrapper
├── velapdf_template/       # Web template module
├── .gitignore              # Git ignore rules
├── build.gradle.kts        # Root build config
├── settings.gradle.kts     # Gradle settings
└── README.md               # This file
```

---

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**useripx** — [GitHub](https://github.com/useripx) <br>
**zenoszaiys** —  
[Github](https://github.com/zenoszaiys)
**giovan97**  — 
[Github](https://github.com/giovan97)

---

<p align="center">
  Made with ❤️ using Kotlin & Jetpack Compose
</p>
