package com.rajapps.watsappstatussaver.views.activities

import android.app.Application
import com.google.android.gms.ads.MobileAds


class MyBookApp() : Application() {
    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this) { } // admob openad
    }
}