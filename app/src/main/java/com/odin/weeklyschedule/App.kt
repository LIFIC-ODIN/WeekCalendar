package com.odin.weeklyschedule

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)
    }
}