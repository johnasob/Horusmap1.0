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
import android.os.Handler
import android.speech.RecognizerIntent
import android.view.Gravity
import android.widget.Button
import android.widget.RemoteViews
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.horusmap10.Horusmap1.Horusmap.Companion.prefs
import com.example.horusmap10.SettingsFragment.ComunicadorFragments3
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_start_route.*
import kotlinx.android.synthetic.main.custom_toast_ok.*
import kotlinx.android.synthetic.main.custom_toast_ok.view.*
import kotlinx.coroutines.launch
import java.util.*

/**PANTALLA DE NAVEGACIÓN EXTERIOR Y SUS RESPECTIVAS PESTAÑAS*/
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
    private val channelName2 = "channelName2"
    private val channelId2 = "channelId2"
    private lateinit var notificationCustomStyle: Notification
    private var notificationCustomStyleID = 5
    private var notificationCustomStyleID2 = 2
    private var ruta: String = "cualquier cosa"
    //NAVEGACIÓN E INICIALIZACIÓN DE LA VISTA DEL FRAGMENTO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_route)


        thisActivity=this
        ip = prefs.getIp()
        apikey = prefs.getApikey()
        restClient = RESTClient("http://$ip/")
        replaceFragment(routesFragment)
        navegation = findViewById(R.id.bottomNavigationView)
        navegation!!.setBackgroundColor(Color.TRANSPARENT)
        createNotificationChannel()
        createNotificationChannel2()
        navegation?.setOnItemSelectedListener {

            when(it.itemId){
                R.id.routes_fragment -> {replaceFragment(routesFragment)
                }
                R.id.questions_fragment -> {replaceFragment(questionsFragment)
                   }
                R.id.settings_fragment -> {replaceFragment(settingsFragment)
                   }
                R.id.profile_fragment -> {
                    val bundle=  Bundle()
                    bundle.putString("ip",ip)
                    bundle.putString("apikey",apikey)
                    profileFragment.arguments = bundle
                    replaceFragment(profileFragment)

                }
            }
            true
        }
        microfono = findViewById<FloatingActionButton>(R.id.microfono)

        microfono?.setOnClickListener { runOnUiThread { askSpeechInput() } }

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
    //IMPRESIÓN DE LA NOTIFICACIÓN DESPLEGABLE
    public fun shownotify(cadena:String){
        val handler = Handler()
        handler.postDelayed(Runnable {
            buildNotificationCustomStyle(cadena)
            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(notificationCustomStyleID, notificationCustomStyle)
        }, 6000)
    }

    private fun replaceFragment(fragment: Fragment){
            val transition = supportFragmentManager.beginTransaction()
            transition.addToBackStack(null)
            transition.replace(R.id.fragmentContainerView, fragment)
            transition.commit()
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
        val profile = resources.getStringArray(R.array.comand_profile)
        val preguntas = resources.getStringArray(R.array.comand_question)
        val edit = resources.getStringArray(R.array.comand_edit)
        val interior = resources.getStringArray(R.array.comand_interior)
        val terminar = resources.getStringArray(R.array.comand_finish)
        val alert = resources.getStringArray(R.array.comand_alert)
        val ruta1 = resources.getStringArray(R.array.comand_rute2)
        val ruta2=resources.getStringArray(R.array.comand_rute1)
        val acti=resources.getStringArray(R.array.comand_activated)
        val noacti=resources.getStringArray(R.array.comand_noactivated)
        //start routes
        for (i in list1.indices) {
            if (input == list1[i]) {
                replaceFragment(routesFragment)
                 }
        }
        //star settings
        for (i in list2.indices) {
            if (input == list2[i]) {
                replaceFragment(settingsFragment)
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
        /**VOLVER*/
        for (i in list5.indices) {
            if (input == list5[i]) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        //cerrar aplicacion
        for (i in list6.indices) {
            if (input == list6[i]) {

            }
        }
        //Perfil
        for (i in profile.indices) {
            if (input == profile[i]) {
                replaceFragment(profileFragment)
            }
        }
        for (i in alert.indices) {
            if (input == alert[i]) {
                replaceFragment(alertFragment)
            }
        }
        //Preguntas  y respuestas
        for (i in preguntas.indices) {
            if (input == preguntas[i]) {
                replaceFragment(questionsFragment)
            }
        }

        //Editar perfil
        for (i in edit.indices) {
            if (input == edit[i]) {
                replaceFragment(editProfileFragment)
            }
        }

        //Navegación inteior
        for (i in interior.indices) {
            if (input == interior[i]) {
                InDoorNotification()
            }
        }
        for (i in terminar.indices) {
            if (input == terminar[i]) {
                editProfileFragment.validation()
                replaceFragment(profileFragment)
            }
        }
        for (i in ruta1.indices) {
            if (input == ruta1[i]) {
                ruta="porteria"
            }
        }

        for (i in ruta2.indices) {
            if (input == ruta2[i]) {
                ruta="ingenieria"
            }
        }
        for (i in acti.indices) {
            if (input == acti[i]) {
                val bundle = Bundle()
                bundle.putString("comand","activar")
                alertFragment.arguments = bundle
            }
        }
        for (i in noacti.indices) {
            if (input == noacti[i]) {
                val bundle = Bundle()
                bundle.putString("comand","desactivar")
                alertFragment.arguments = bundle
            }
        }


    }
/**MÉTODOS PARA NAVEGAR ENTRE FRAGMENTS DEL ACTIVITY*/
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
    /**SOLICITUD DEL PERMISO DE MICROFONO*/
    @SuppressLint("ObsoleteSdkInt")
    private fun requestPermission(){
        Toast.makeText(this,"LLamando a emergencias",Toast.LENGTH_LONG).show()
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
    /**LLAMADO AL 123*/
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
    private fun createNotificationChannel2() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelImportance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId2, channelName2, channelImportance).apply {
                lightColor = Color.RED
                enableLights(true)
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
    /**CREACIÓN NOTIFICACIONES DESPLEGABLES PERSONALIZADAS POR NOSOTROS*/
    private fun buildNotificationCustomStyle(cadena:String) {
        val notificationLayout = RemoteViews(packageName, R.layout.notification_small)
        val notificationLayoutExpanded = RemoteViews(packageName, R.layout.notification_expanded)
        notificationLayout.setTextViewText(R.id.textViewTitle2,"horusmap")
        notificationLayout.setTextViewText(R.id.textViewText2,cadena)
        notificationLayoutExpanded.setTextViewText(R.id.textView1,"horusmap")
        notificationLayoutExpanded.setTextViewText(R.id.textView1a,cadena)

        notificationCustomStyle = NotificationCompat.Builder(this, channelId).also {
            it.setSmallIcon(R.drawable.ic_horus_eye_edit)
            //it.setStyle(NotificationCompat.DecoratedCustomViewStyle())
            it.setCustomContentView(notificationLayout)
            it.setCustomBigContentView(notificationLayoutExpanded)
            it.setPriority(0)
        }.build()
    }
    /**INICIAR NAVEGACIÓN INTERIOR*/
    fun InDoorNotification(){
        val InDoor = Intent(this, InDoor::class.java)
        startActivity(InDoor)
        finish()
    }
    //Se envia la ruta dicha en el microfono
    fun rutas():String{
        return (ruta)
    }
    /**CREACIÓN DE MENSAJE TOAST PERSONALIZADO POR NOSOTROS*/
    fun toteitor(msg: String?) {
        val layout2 = layoutInflater.inflate(R.layout.custom_toast_ok,custom_toast_ok)
        val myToast = Toast(this)
        layout2.cadena.text = msg
        myToast.duration = Toast.LENGTH_LONG
        myToast.setGravity(Gravity.BOTTOM, 0, 200)
        myToast.view = layout2//setting the view of custom toast layout
        lifecycleScope.launch{
            myToast.show()
        }

    }
}



