package com.example.horusmap10.Horusmap1

import android.content.Context

class Prefs(val context: Context) {
    val state= context.getSharedPreferences("MisDatos",0)

    fun saveApikey(apikey: String){
        state.edit().putString("apikey",apikey).apply()
    }
    fun getIp():String{
        return state.getString("ip","192.168.1.26:8080")!!
    }
    fun getApikey():String{
        return state.getString("apikey","")!!
    }
    fun clearAll(){
        state.edit().clear().apply()
    }

    fun saveMostrador(mostrador: String){
        state.edit().putString("mostrador",mostrador).apply()
    }
    fun getMostrador():String{
        return state.getString("mostrador","")!!
    }
}