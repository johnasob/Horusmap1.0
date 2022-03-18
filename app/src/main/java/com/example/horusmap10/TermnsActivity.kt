package com.example.horusmap10

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.horusmap10.Horusmap1.Horusmap
import com.example.horusmap10.databinding.ActivityTermnsBinding


class TermnsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTermnsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(Horusmap.prefs.getAlert()!="Desactivado") {
            Toast.makeText(
                this,
                "Usted se encuentra en la pestaña de terminos y condiciones",
                Toast.LENGTH_SHORT
            ).show()
        }
        binding = ActivityTermnsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener(){
            onBackPressed()
            finish()
        }
    }
}