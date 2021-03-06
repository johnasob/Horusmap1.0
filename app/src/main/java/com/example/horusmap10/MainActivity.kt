package com.example.horusmap10


import RESTClient
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.horusmap10.Horusmap1.Horusmap.Companion.prefs
import com.example.horusmap10.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.Authenticator
import java.net.PasswordAuthentication
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var restClient: RESTClient
    private lateinit var thisActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        if(prefs.getAlert()!="Desactivado") {
            Toast.makeText(
                this,
                "Usted se encuentra iniciando sesión",
                Toast.LENGTH_SHORT
            ).show()
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        thisActivity=this

        /** VERIFICACIÓN DE LOGIN */
        if(prefs.getApikey().isNotEmpty()){
            val home = Intent(this, HomeActivity::class.java)
            startActivity(home)
            finish()
        }
        val ip = prefs.getIp()
        restClient = RESTClient("http://$ip/")

        // Boton login
        binding.loginButton.setOnClickListener(){
            validUser()
            validation()
        }
        // Boton regístrate
        binding.registerButton.setOnClickListener(){
            val register = Intent(this, RegisterActivity::class.java)
            register.putExtra("ip",ip)
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
        }
    }

    private fun validUser() {
        val name= binding.userLogin.editText?.text.toString()
        val key= binding.passwordLogin.editText?.text.toString().toCharArray()
        Authenticator.setDefault(object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(
                    name,
                    key
                )
            }
        })
        //GET /auth -> response: API key for user, uses basic HTTP authentication
        restClient.httpGetAsync("/auth")

        GlobalScope.launch {
            val response = restClient.wait()
            runOnUiThread {
                if (response == "ERROR 404") {
                    val toast1 = Toast.makeText(applicationContext, "No existe un usuario llamado $name o ingreso una contraseña erronea", Toast.LENGTH_LONG)
                    toast1.show()
                    binding.userLogin.error = getString(R.string.user) +" "+ getString(R.string.validm)
                } else {
                    binding.userLogin.error = null
                    //Guarda la apikey recibida
                    prefs.saveApikey(response)
                    val toast2 = Toast.makeText(applicationContext, "Acceso exitoso, $name", Toast.LENGTH_LONG)
                    toast2.show()
                    val home = Intent()
                    home.setClassName(thisActivity, "com.example.horusmap10.HomeActivity")
                    startActivity(home)
                    finish()
                }

            }

        }
    }


}

