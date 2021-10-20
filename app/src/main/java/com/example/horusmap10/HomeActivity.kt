package com.example.horusmap10

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.horusmap10.databinding.ActivityHomeBinding
import com.example.horusmap10.databinding.ActivityRegisterBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var apikey = intent.getStringExtra("apikey")

        binding.routesButton.setOnClickListener(){
            val routes = Intent(this, StartRouteActivity::class.java)
            startActivity(routes)
        }
        binding.settingsButton.setOnClickListener(){
            //Como prueba meti profile pero va settings
            val settings = Intent(this, ProfileActivity::class.java)
            settings.putExtra("apikey", apikey)
            startActivity(settings)
        }
        binding.logoutButton.setOnClickListener(){
            //Inicia login
            val login = Intent(this, MainActivity::class.java)
            startActivity(login)
            finish()
        }
        binding.emergencyButton.setOnClickListener(){
            val sos = Intent(this, SosActivity::class.java)
            startActivity(sos)
        }
    }

}