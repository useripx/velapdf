package com.velapdf.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VelaPDFApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
