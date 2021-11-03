package com.example.horusmap10.Horusmap1

import android.app.Application

class Horusmap:Application() {

    companion object{
        lateinit var prefs:Prefs
    }
    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(applicationContext)
    }
}