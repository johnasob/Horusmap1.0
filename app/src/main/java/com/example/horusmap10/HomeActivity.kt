package com.example.horusmap10



import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.appcompat.app.AppCompatActivity
import com.example.horusmap10.Horusmap1.Horusmap.Companion.prefs
import com.example.horusmap10.databinding.ActivityHomeBinding
import java.util.*

class HomeActivity : AppCompatActivity() {

    private val  RQ_SPEECH_REC = 102
    private lateinit var binding: ActivityHomeBinding
    private var apikey: String? = null
    private lateinit var thisActivity: HomeActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        /**parametros iniciales con biding*/
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val ip = prefs.getIp()
        /*Se recibe el apykey del usuario*/
        apikey = prefs.getApikey()

        /**INICIAR RUTA*/
        binding.routesButton.setOnClickListener {
            val routes = Intent(this, StartRouteActivity::class.java)
            startActivity(routes)
        }

        /**SETTINGS*/
        binding.settingsButton.setOnClickListener {


        }
        /**CERRAR SESIÓN*/
        binding.logoutButton.setOnClickListener {
            //Inicia login
            val login = Intent(this, MainActivity::class.java)
            prefs.clearAll()
            startActivity(login)
            finish()
        }
        /**EMERGENCIA*/
        binding.emergencyButton.setOnClickListener {
            val sos = Intent(this, SosActivity::class.java)
            startActivity(sos)
        }
        /**Reconocimiento de voz*/
        binding.micButton.setOnClickListener {
            askSpeechInput()
        }



    }
    // VERIFICACIÓN DE ENTRADA DE AUDIO
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RQ_SPEECH_REC && resultCode == Activity.RESULT_OK){
            val result: ArrayList<String>? = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val input = result?.get(0).toString()
            voice_option(input)
        }
    }
    // LECTURA DE AUDIO
    private fun askSpeechInput() {

            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Dí algo")
            startActivityForResult(i,RQ_SPEECH_REC)
    }

    // RECONOCIMIENTO DE COMANDOS DE VOZ
    private  fun  voice_option(input: String) {

        val list1 = resources.getStringArray(R.array.comand_route)
        val list2 = resources.getStringArray(R.array.comand_confi)
        val list3 = resources.getStringArray(R.array.comand_logout)
        val list4 = resources.getStringArray(R.array.comand_sos)
        val list5 = resources.getStringArray(R.array.comand_back)
        val list6 = resources.getStringArray(R.array.comand_off)

        //start routes
        for (i in list1.indices) {
            if (input == list1[i]) {
                val intent = Intent(this, StartRouteActivity::class.java)
                intent.putExtra("apikey", apikey)
                startActivity(intent)
            }
        }
        //star settings
        for (i in list2.indices) {
            if (input == list2[i]) {
                /*val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("apikey", apikey)
                startActivity(intent)*/
            }
        }
        //start logout
        for (i in list3.indices) {
            if (input == list3[i]) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("apikey", apikey)
                startActivity(intent)
                finish()
            }
        }
        //start sos
        for (i in list4.indices) {
            if (input == list4[i]) {
                val intent = Intent(this, SosActivity::class.java)
                intent.putExtra("apikey", apikey)
                startActivity(intent)
            }
        }
        /**BORRAR ESTE APARTADO CUANDO SEPA COMO GUARDAR SESIÓN INICIADA*/
        for (i in list5.indices) {
            if (input == list5[i]) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("apikey", apikey)
                startActivity(intent)
                finish()
            }
        }
        //cerrar aplicacion
        for (i in list6.indices) {
            if (input == list6[i]) {
                onDestroy()
            }
        }
    }


}