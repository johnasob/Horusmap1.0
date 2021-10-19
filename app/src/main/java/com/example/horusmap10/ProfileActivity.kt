

package com.example.horusmap10

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.horusmap10.databinding.ActivityHomeBinding
import com.example.horusmap10.databinding.ActivityProfileBinding


class ProfileActivity : AppCompatActivity() {
    //Se crea la vista binding
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loguotButton.setOnClickListener(){
            val logout = Intent(this, MainActivity::class.java)
            startActivity(logout)
            finish()
        }
        binding.backButton.setOnClickListener(){
            val home = Intent(this, HomeActivity::class.java)
            startActivity(home)
            finish()
        }
    }
}