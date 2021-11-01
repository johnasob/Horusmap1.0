package com.example.horusmap10


import RESTClient
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import android.widget.Toast.makeText as makeText1


class StartRouteActivity : AppCompatActivity(), RoutesFragment.RoutesMetodos{
    private val  RQ_SPEECH_REC = 102
    private lateinit var restClient: RESTClient
    private lateinit var thisActivity: StartRouteActivity
    private var apikey: String? = null
    private var ip: String? = null
    private val routesFragment = RoutesFragment()
    private val questionsFragment = QuestionsFragment()
    private val settingsFragment = SettingsFragment()
    private val profileFragment = ProfileFragment()
    private var navegation: BottomNavigationView? = null
    private var microfono: FloatingActionButton? = null
    //lateinit var back: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_route)


        thisActivity=this
        ip = intent.getStringExtra("ip").toString()
        restClient = RESTClient("http://$ip/")
        apikey = intent.getStringExtra("apikey").toString()



        var bundle=  Bundle()
        bundle.putString("temp","mandado")
        bundle.putString("temp2","mandado2")
        routesFragment.arguments = bundle
        replaceFragment(routesFragment)
        navegation = findViewById(R.id.bottomNavigationView)
        navegation!!.setBackgroundColor(Color.TRANSPARENT)
        navegation?.setOnItemSelectedListener {

            when(it.itemId){
                R.id.routes_fragment -> {
                    var bundle=  Bundle()
                    bundle.putString("temp","mandado")
                    routesFragment.arguments = bundle
                    replaceFragment(routesFragment)
                }
                R.id.questions_fragment -> replaceFragment(questionsFragment)
                R.id.settings_fragment -> replaceFragment(settingsFragment)
                R.id.profile_fragment -> {
                    var bundle=  Bundle()
                    bundle.putString("ip",ip)
                    bundle.putString("apikey",apikey)
                    profileFragment.arguments = bundle
                    replaceFragment(profileFragment)
                }
            }
            true
        }
        microfono = findViewById<FloatingActionButton>(R.id.microfono)

        microfono?.setOnClickListener {
           askSpeechInput()
        }


    }

    private fun replaceFragment(fragment: Fragment){
        if(fragment !=null){
            val transition = supportFragmentManager.beginTransaction()
            transition.replace(R.id.fragmentContainerView, fragment)
            transition.commit()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RQ_SPEECH_REC && resultCode == Activity.RESULT_OK){
            val result: ArrayList<String>? = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val input = result?.get(0).toString()
            voice_option(input)
        }
    }
    private fun askSpeechInput() {

        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Dí algo")
        startActivityForResult(i,RQ_SPEECH_REC)

    }

    private  fun  voice_option(imput: String) {

        val list1 = resources.getStringArray(R.array.comand_route)
        val list2 = resources.getStringArray(R.array.comand_confi)
        val list3 = resources.getStringArray(R.array.comand_profile)
        val list4 = resources.getStringArray(R.array.comand_sos)
        val list5 = resources.getStringArray(R.array.comand_back)
        val list6 = resources.getStringArray(R.array.comand_off)
        val list7 = resources.getStringArray(R.array.comand_question)

        navegation = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        makeText1(thisActivity, "input= "+imput+" list1= "+list2[1], Toast.LENGTH_SHORT).show()
        //Iniciar ruta
        for (i in list1.indices) {
            if (imput == list1[i]) {
                replaceFragment(profileFragment)
            }
        }
        //star settings
        for (i in list2.indices) {
            if (imput == list2[i]) {
                replaceFragment(settingsFragment)
            }
        }
        //start logout
        for (i in list3.indices) {
            if (imput == list3[i]) {
               // replaceFragment(profileFragment)
            }
        }
        //SOS
        for (i in list4.indices) {
            if (imput == list4[i]) {
                val intent = Intent(this, SosActivity::class.java)
                intent.putExtra("apikey", apikey)
                startActivity(intent)
            }
        }
        // VOLVER
        for (i in list5.indices) {
            if (imput == list5[i]) {
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("apikey", apikey)
                startActivity(intent)
                finish()
            }
        }
        //CERRAR APP
        for (i in list6.indices) {
            if (imput == list6[i]) {
                finish()
            }
        }
        //PREGUNTAS FRECUENTES
        for (i in list7.indices) {
            if (imput == list6[i]) {
                //replaceFragment(questionsFragment)
            }
        }
    }

    override fun microButton(click: Boolean?) {
        if(click == true) {
            askSpeechInput()
        }
    }
}



