package com.example.horusmap10

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.horusmap10.databinding.ActivityMainBinding
import android.view.View;
import RESTClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.Authenticator
import java.net.PasswordAuthentication

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var restClient: RESTClient
    lateinit var thisActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        thisActivity=this
        val ip = "192.168.1.17"
        //sharedPreferences.getString("ipaddress", "192.168.1.4:8080")""
        restClient = RESTClient("http://$ip/")

        // Boton login
        binding.loginButton.setOnClickListener(){
            validation()
        }
        // Boton regístrate
        binding.registerButton.setOnClickListener(){
            val register = Intent(this, RegisterActivity::class.java)
            startActivity(register)
        }

    }

    private fun validateUser(): Boolean {
        val user = binding.userLogin.editText?.text.toString()
        //Valida si la casilla esta vacía
        return if (user.isEmpty()){
            binding.userLogin.error = getString(R.string.user) +" "+ getString(R.string.validm)

            false
        //Si no esta vacía continua
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
        }else {
            validUser()
            finish()
        }
    }

    private fun validUser() {
        val name= binding.userLogin.editText?.text.toString()
        val key= binding.passwordLogin.editText?.text.toString().toCharArray()
        Authenticator.setDefault(object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(
                    name!!,
                    key!!
                )
            }
        })
        //GET /auth -> response: API key for user, uses basic HTTP authentication
        restClient.httpGetAsync("/auth")

        GlobalScope.launch {

            var response = restClient.wait()
            runOnUiThread {

                if (response == "ERROR 404") {
                    val toast1 = Toast.makeText(
                        applicationContext,
                        "No existe un usuario llamado $name", Toast.LENGTH_LONG
                    )
                    toast1.show()

                } else {
                    val toast2 = Toast.makeText(
                        applicationContext,
                        "Access granted ...", Toast.LENGTH_LONG
                    )
                    toast2.show()
                    val home = Intent()
                    home.setClassName(thisActivity, "com.example.horusmap10.HomeActivity")
                    home.putExtra("apikey", response)
                    startActivity(home)
                }
            }

        }
    }
    override fun finish() {
        super.finish()
    }
}

