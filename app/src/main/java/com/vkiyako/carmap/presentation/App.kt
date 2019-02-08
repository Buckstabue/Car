package com.vkiyako.carmap.presentation

import android.app.Application
import android.util.Log
import io.reactivex.plugins.RxJavaPlugins

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        RxJavaPlugins.setErrorHandler { Log.e("App.class", "RxJavaSetErrorHandler error", it) }
    }
}
