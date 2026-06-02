# VelaPDF - Android Native Implementation Guide (MVVM) - Updated with State Handling

Aplikasi ini menggunakan **Kotlin**, **MVVM architecture**, dan **ViewBinding**.

## 1. UI State Management (ViewModel)

Gunakan `StateFlow` atau `LiveData` untuk mengelola state di `ImageToPdfViewModel`:

```kotlin
sealed class ConversionState {
    object Idle : ConversionState()
    object Loading : ConversionState()
    data class Success(val fileUri: Uri) : ConversionState()
    data class Error(val message: String) : ConversionState()
}

class ImageToPdfViewModel : ViewModel() {
    private val _conversionState = MutableStateFlow<ConversionState>(ConversionState.Idle)
    val conversionState: StateFlow<ConversionState> = _conversionState

    fun convertImage(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            _conversionState.value = ConversionState.Loading
            try {
                // Logika PdfDocument di sini
                val resultUri = repository.savePdf(bitmap)
                _conversionState.value = ConversionState.Success(resultUri)
            } catch (e: Exception) {
                _conversionState.value = ConversionState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }
}
```

## 2. Activity Implementation (ViewBinding)

Observasi state di `ImageToPdfActivity`:

```kotlin
lifecycleScope.launchWhenStarted {
    viewModel.conversionState.collect { state ->
        when (state) {
            is ConversionState.Loading -> {
                binding.loadingOverlay.visibility = View.VISIBLE
                binding.btnConvert.isEnabled = false
            }
            is ConversionState.Success -> {
                binding.loadingOverlay.visibility = View.GONE
                binding.btnConvert.isEnabled = true
                showSuccessDialog(state.fileUri)
            }
            is ConversionState.Error -> {
                binding.loadingOverlay.visibility = View.GONE
                binding.btnConvert.isEnabled = true
                Toast.makeText(this@ImageToPdfActivity, state.message, Toast.LENGTH_LONG).show()
            }
            else -> {
                binding.loadingOverlay.visibility = View.GONE
            }
        }
    }
}
```

## 3. Layout updates (XML)

Pastikan layout memiliki elemen berikut:
- `ProgressBar` di dalam `FrameLayout` dengan background semi-transparan untuk loading overlay.
- `MaterialButton` dengan status `android:enabled` yang terikat pada state.
