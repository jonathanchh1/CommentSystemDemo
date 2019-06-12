package com.emi.commentdemo

import android.app.Application
import net.danlew.android.joda.JodaTimeAndroid

class MyMessage : Application() {

    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)
    }
}