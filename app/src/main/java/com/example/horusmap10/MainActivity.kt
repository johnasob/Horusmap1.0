package com.example.horusmap10

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.horusmap10.databinding.ActivityMainBinding
import android.view.View;


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener(){
            validation()
        }
        binding.registerButton.setOnClickListener(){
            val register = Intent(this, RegisterActivity::class.java)
            startActivity(register)
        }

    }

    private fun validateUser(): Boolean {
        val user = binding.userLogin.editText?.text.toString()
        return if (user.isEmpty()){
            binding.userLogin.error = getString(R.string.user) +" "+ getString(R.string.validm)

            false
        }else{
            binding.userLogin.error = null
            true
        }
    }
    private fun validatePassword(): Boolean {
        val password = binding.passwordLogin.editText?.text.toString()
        return if (password.isEmpty()){
            binding.passwordLogin.error = getString(R.string.password) +" "+ getString(R.string.validf)
            false
        }else{
            binding.passwordLogin.error = null
            true
        }
    }

    private fun validation(){
        val result = arrayOf(validateUser(),validatePassword())
        if (false in result){
            return
        }
            val home = Intent(this, HomeActivity::class.java)
            startActivity(home)
            finish()
            Toast.makeText(this, "Validación exitosa,por favor espere", Toast.LENGTH_SHORT).show()

    }

    override fun finish() {
        super.finish()
    }
}