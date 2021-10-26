package com.example.horusmap10


import RESTClient
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject


class StartRouteActivity : AppCompatActivity() {

    private lateinit var restClient: RESTClient
   // private lateinit var binding: ActivityStartRouteBinding
    private lateinit var thisActivity: StartRouteActivity
    private var apikey: String = ""
    private var name: String = ""
    private var password: String = "3125"
    private var email: String = ""
    private var vision: String = ""
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
        apikey = intent.getStringExtra("apikey").toString()
        //update()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = findNavController(R.id.fragmentContainerView)
        bottomNavigationView.setupWithNavController(navController)


        /*back.setOnClickListener(){

            val home = Intent(this, HomeActivity::class.java)
            home.putExtra("apikey", apikey)
            startActivity(home)
            finish()
        }*/



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
}



