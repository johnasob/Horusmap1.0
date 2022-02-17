package com.example.horusmap10


import RESTClient
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.Button
import android.widget.RemoteViews
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.horusmap10.Horusmap1.Horusmap.Companion.prefs
import com.example.horusmap10.SettingsFragment.ComunicadorFragments3
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_start_route.*
import java.util.*
import android.widget.Toast.makeText as makeText1


class StartRouteActivity : AppCompatActivity(), ProfileFragment.ComunicadorFragments,EditProfileFragment.ComunicadorFragments2,
    ComunicadorFragments3,AlertFragment.ComunicadorFragments6,SoundFragment.ComunicadorFragmentsSounds{
    private val  RQ_SPEECH_REC = 102
    private lateinit var restClient: RESTClient
    private lateinit var thisActivity: StartRouteActivity
    private var apikey: String? = null
    private var ip: String? = null
    private val routesFragment = RoutesFragment()
    private val questionsFragment = QuestionsFragment()
    private val settingsFragment = SettingsFragment()
    private val profileFragment = ProfileFragment()
    private val editProfileFragment = EditProfileFragment()
    private val alertFragment = AlertFragment()
    private val soundFragment = SoundFragment()
    private var navegation: BottomNavigationView? = null
    private var microfono: FloatingActionButton? = null
    private var beacon: Button? = null
    private val channelName = "channelName"
    private val channelId = "channelId"
    private lateinit var notificationCustomStyle: Notification
    private val notificationCustomStyleID = 5
    //lateinit var back: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_route)


        thisActivity=this
        ip = prefs.getIp()
        apikey = prefs.getApikey()
        restClient = RESTClient("http://$ip/")
        beacon = findViewById(R.id.beacon_button)
        replaceFragment(routesFragment)
        findViewById<Button>(R.id.beacon_button).visibility = View.VISIBLE
        navegation = findViewById(R.id.bottomNavigationView)
        navegation!!.setBackgroundColor(Color.TRANSPARENT)
        createNotificationChannel()
        navegation?.setOnItemSelectedListener {

            when(it.itemId){
                R.id.routes_fragment -> {replaceFragment(routesFragment)
                findViewById<Button>(R.id.beacon_button).visibility = View.VISIBLE
                }
                R.id.questions_fragment -> {replaceFragment(questionsFragment)
                    findViewById<Button>(R.id.beacon_button).visibility = View.INVISIBLE}
                R.id.settings_fragment -> {replaceFragment(settingsFragment)
                    findViewById<Button>(R.id.beacon_button).visibility = View.INVISIBLE}
                R.id.profile_fragment -> {
                    val bundle=  Bundle()
                    bundle.putString("ip",ip)
                    bundle.putString("apikey",apikey)
                    profileFragment.arguments = bundle
                    replaceFragment(profileFragment)
                    findViewById<Button>(R.id.beacon_button).visibility = View.INVISIBLE
                }
            }
            true
        }
        microfono = findViewById<FloatingActionButton>(R.id.microfono)

        microfono?.setOnClickListener {
           askSpeechInput()
        }

        back_home_button.setOnClickListener {
            val home = Intent(this, HomeActivity::class.java)
            startActivity(home)
        }
        beacon?.setOnClickListener {
            val InDoor = Intent(this, InDoor::class.java)
            startActivity(InDoor)
            onPause()
        }

    }

    public fun shownotify(cadena:String) {

        buildNotificationCustomStyle(cadena)
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(notificationCustomStyleID, notificationCustomStyle)
    }

    private fun replaceFragment(fragment: Fragment){
        if(fragment !=null){
            val transition = supportFragmentManager.beginTransaction()
            transition.addToBackStack(null)
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
    override fun devolverDato6(dato: Boolean) {
        if(dato==true){
            replaceFragment(settingsFragment)
        }
    }
    override fun devolverDato7(dato: Boolean) {
        TODO("Not yet implemented")
    }


    override fun devolverDato9(dato: Boolean) {
        if(dato==true){

            requestPermission()

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
            Toast.makeText(this,"Necesitas conceder los permisos primero",Toast.LENGTH_SHORT).show()
        }
    }

    /** notificaciones */
    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelImportance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, channelImportance).apply {
                lightColor = Color.RED
                enableLights(true)
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotificationCustomStyle(cadena:String) {
        val notificationLayout = RemoteViews(packageName, R.layout.notification_small)
        val notificationLayoutExpanded = RemoteViews(packageName, R.layout.notification_expanded)
        notificationLayout.setTextViewText(R.id.textViewTitle,"HORUSMAP")
        notificationLayout.setTextViewText(R.id.textViewText,cadena)

        notificationCustomStyle = NotificationCompat.Builder(this, channelId).also {
            it.setSmallIcon(R.drawable.ic_horus_eye_edit)
            //it.setStyle(NotificationCompat.DecoratedCustomViewStyle())
            it.setCustomContentView(notificationLayout)
            it.setCustomBigContentView(notificationLayoutExpanded)
        }.build()
    }

}



