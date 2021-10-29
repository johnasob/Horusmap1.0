package com.example.horusmap10


import RESTClient
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*





class StartRouteActivity : AppCompatActivity(), RoutesFragment.RoutesMetodos{
    private val  RQ_SPEECH_REC = 102
    private lateinit var restClient: RESTClient
    //private lateinit var binding: ActivityStartRouteBinding
    private lateinit var thisActivity: StartRouteActivity
    private var apikey: String = ""
    private var name: String = ""
    private var password: String = "3125"
    private var email: String = ""
    private var vision: String = ""
    private var micButton: Boolean? = false
    private val routesFragment = RoutesFragment()
    private val questionsFragment = QuestionsFragment()
    private val settingsFragment = SettingsFragment()
    private val profileFragment = ProfileFragment()
    private var navegation: BottomNavigationView? = null
    private var microfono: FloatingActionButton? = null
    //lateinit var back: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding = ActivityStartRouteBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_start_route)
        //setContentView(binding.root)
        //back = findViewById(R.id.back)

        thisActivity=this
        val ip = "192.168.1.4:8080"
        restClient = RESTClient("http://$ip/")
        //apikey = intent.getStringExtra("apikey").toString()
        val apikey = "61718e0887d4577337a2b329"
        //update()

        /** PARTE BOTTOMNAVEGATION CON EL MY_NAV
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = findNavController(R.id.fragmentContainerView)

        bottomNavigationView.setupWithNavController(navController)
        Toast.makeText(this,findNavController(R.id.fragmentContainerView).graph.toString(), Toast.LENGTH_LONG).show()
         */
        /*back.setOnClickListener(){

            val home = Intent(this, HomeActivity::class.java)
            home.putExtra("apikey", apikey)
            startActivity(home)
            finish()
        }*/
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
                R.id.profile_fragment -> replaceFragment(profileFragment)
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


    private fun update(){
        restClient.httpGetAsync("/user/info?auth=$apikey")


        //Se busca acceder a la información actual del servidor
        GlobalScope.launch {
            val myUserInfo = restClient.wait()

            val list = JSONObject(myUserInfo)
            name = list.getString("Name: ")
            password = list.getString("Password: ")
            email = list.getString("Email: ")
            vision = list.getString("Vision: ")
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

    private  fun  voice_option(input: String) {

        val list1 = resources.getStringArray(R.array.comand_route)
        val list2 = resources.getStringArray(R.array.comand_confi)
        val list3 = resources.getStringArray(R.array.comand_profile)
        val list4 = resources.getStringArray(R.array.comand_sos)
        val list5 = resources.getStringArray(R.array.comand_back)
        val list6 = resources.getStringArray(R.array.comand_off)
        val list7 = resources.getStringArray(R.array.comand_question)
        //start routes
        for (i in list1.indices) {
            if (input == list1[i]) {
               replaceFragment(routesFragment)

            }else{
                return
            }
        }
        //star settings
        for (i in list2.indices) {
            if (input == list2[i]) {

            }else{
                return
            }
        }
        //start logout
        for (i in list3.indices) {
            if (input == list3[i]) {
               // replaceFragment(profileFragment)
            }
        }
        //SOS
        for (i in list4.indices) {
            if (input == list4[i]) {
                val intent = Intent(this, SosActivity::class.java)
                intent.putExtra("apikey", apikey)
                startActivity(intent)
            }
        }
        // VOLVER
        for (i in list5.indices) {
            if (input == list5[i]) {
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("apikey", apikey)
                startActivity(intent)
                finish()
            }
        }
        //CERRAR APP
        for (i in list6.indices) {
            if (input == list6[i]) {
                finish()
            }
        }
        //PREGUNTAS FRECUENTES
        for (i in list7.indices) {
            if (input == list6[i]) {
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



