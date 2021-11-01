

package com.example.horusmap10

import RESTClient
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.horusmap10.databinding.ActivityProfileBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject



class ProfileActivity : AppCompatActivity() {


    //Se crea la vista binding
    private lateinit var binding: ActivityProfileBinding
    private lateinit var restClient: RESTClient
    private lateinit var thisActivity: ProfileActivity
    private var apikey: String = ""
    private var name: String = ""
    private var password: String = "3125"
    private var email: String = ""
    private var vision: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        thisActivity=this

        val ip = "192.168.1.11:8080"
        restClient = RESTClient("http://$ip/")
        apikey = intent.getStringExtra("apikey").toString()

        //val apikey = "61718e0887d4577337a2b329"
        update()

        binding.loguotButton.setOnClickListener(){
            val logout = Intent(this, MainActivity::class.java)
            startActivity(logout)
            finish()
        }
        binding.backButton.setOnClickListener(){
            val home = Intent(this, HomeActivity::class.java)
            home.putExtra("apikey", apikey)
            startActivity(home)
            finish()
        }
        binding.editButton.setOnClickListener(){

            val edit= Intent(this, EditProfileActivity::class.java)
            edit.putExtra("apikey", apikey)
            startActivity(edit)
            finish()
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
            binding.nameProfile1.text = name
            binding.emailProfile1.text = email
            binding.visionProfile1.text = vision
        }
    }
}