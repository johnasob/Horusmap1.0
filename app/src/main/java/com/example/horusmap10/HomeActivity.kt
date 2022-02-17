package com.example.horusmap10



import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.horusmap10.Horusmap1.Horusmap.Companion.prefs
import java.util.*

class HomeActivity : AppCompatActivity(),HomeFragment.ComunicadorFragmentsHome,SettingsFragment.ComunicadorFragments3
,AlertFragment.ComunicadorFragments6,ProfileFragment.ComunicadorFragments,EditProfileFragment.ComunicadorFragments2,SoundFragment.ComunicadorFragmentsSounds{

    private val  RQ_SPEECH_REC = 102
    private var apikey: String? = null
    private val homeFragment = HomeFragment()
    private val settingsFragment = SettingsFragment()
    private val profileFragment = ProfileFragment()
    private val editProfileFragment = EditProfileFragment()
    private val alertFragment = AlertFragment()
    private val soundFragment = SoundFragment()
    private lateinit var thisActivity: HomeActivity
    private lateinit var back:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        /**parametros iniciales con biding*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        thisActivity=this
        val ip = prefs.getIp()
        /*Se recibe el apykey del usuario*/
        apikey = prefs.getApikey()
        back= findViewById(R.id.back_home_button)
        back.visibility = View.INVISIBLE
        replaceFragment(homeFragment)
        back.setOnClickListener {
            replaceFragment(homeFragment)
            back.visibility = View.INVISIBLE
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
                requestPermission()
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

    @SuppressLint("ObsoleteSdkInt")
    private fun requestPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    call()
                }
                else -> requestPermissionLaucher.launch(android.Manifest.permission.CALL_PHONE)
            }
        }else{
            call()
        }
    }
    private fun call() {
        startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:${getString(R.string.sos_number)}")))
    }
    private val requestPermissionLaucher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted->
        if(isGranted){
            call()
        }else{
            Toast.makeText(this,"Necesitas conceder los permisos primero", Toast.LENGTH_SHORT).show()
        }
    }

    private fun replaceFragment(fragment: Fragment){
        if(fragment !=null){
            val transition = supportFragmentManager.beginTransaction()
            transition.addToBackStack(null)
            transition.replace(R.id.fragmentContainerViewHome, fragment)
            transition.commit()

        }
    }

    override fun starRoute(dato: Boolean) {
        if (dato==true) {
            val routes = Intent(this, StartRouteActivity::class.java)
            startActivity(routes)
        }
    }

    override fun settings(dato: Boolean) {
        if(dato==true){
            replaceFragment(settingsFragment)
            back.visibility = View.VISIBLE
        }
    }

    override fun sos(dato: Boolean) {
        if(dato==true){
            requestPermission()
        }
    }

    override fun mic(dato: Boolean) {
        if(dato==true){
            askSpeechInput()
        }
    }

    override fun logout(dato: Boolean) {
        if(dato==true){
            val login = Intent(this, MainActivity::class.java)
            prefs.clearAll()
            startActivity(login)
            finish()
        }
    }

    override fun devolverDato3(dato: Boolean) {
        if(dato==true){
            replaceFragment(profileFragment)
        }
    }


    override fun devolverDato5(dato: Boolean) {
        if(dato==true){
            replaceFragment(alertFragment)
        }
    }

    override fun devolverDato7(dato: Boolean) {
        TODO("Not yet implemented")
    }

    override fun devolverDato6(dato: Boolean) {
        if(dato==true){
            replaceFragment(settingsFragment)

        }
    }
    override fun devolverDato9(dato: Boolean) {
        if(dato==true){
            requestPermission()
        }
    }

    override fun devolverDato(dato: Boolean) {
        if(dato==true){
            replaceFragment(editProfileFragment)
        }
    }

    override fun finishSesion(dato: Boolean) {
        if (dato==true) {
            val login = Intent(this, MainActivity::class.java)
            prefs.clearAll()
            startActivity(login)
            finish()
        }
    }

    override fun devolverDato2(dato: Boolean) {
        if(dato==true){
            replaceFragment(profileFragment)
        }
    }

    override fun sounds(dato: Boolean) {
        if(dato==true){
            replaceFragment(settingsFragment)
        }
    }

    override fun devolversounds(dato: Boolean) {
        if(dato==true){
            replaceFragment(soundFragment)
        }
    }


}