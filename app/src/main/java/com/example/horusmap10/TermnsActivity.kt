package com.example.horusmap10

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.horusmap10.databinding.ActivityRegisterBinding
import com.example.horusmap10.databinding.ActivityTermnsBinding


class TermnsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTermnsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermnsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener(){
            onBackPressed()
            finish()
        }
    }
}