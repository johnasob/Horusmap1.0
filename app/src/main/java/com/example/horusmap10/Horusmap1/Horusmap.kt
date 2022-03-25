package com.example.horusmap10.Horusmap1

import android.app.Application
//Clase con el numbre de la app para verificar que parametros preestablecidos estan abiertos
class Horusmap:Application() {

    companion object{
        lateinit var prefs:Prefs
    }
    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(applicationContext)
    }
}