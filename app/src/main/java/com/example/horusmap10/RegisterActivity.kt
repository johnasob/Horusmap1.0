package com.example.horusmap10

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.util.PatternsCompat
import com.example.horusmap10.databinding.ActivityRegisterBinding
import java.util.regex.Pattern
import java.util.regex.Pattern.compile
import RESTClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    lateinit var restClient: RESTClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val diseases= resources.getStringArray(R.array.diseases)
        val arrayAdapter= ArrayAdapter(this, R.layout.dropdown_menu, diseases)
        with(binding.autoRegister){
            setAdapter(arrayAdapter)
        }
        binding.continueButton.setOnClickListener(){
            validation()
        }
        binding.termsButton.setOnClickListener(){
            val terms = Intent(this, TermnsActivity::class.java)
            startActivity(terms)
        }
    }
    private fun validateUser(): Boolean {
        val user = binding.userRegister.editText?.text.toString()
        val validatorRegex = Pattern.compile(
            "^" +
                    "(?=\\S+$)" +           //no white spaces
                    ".{5,}" +               //at least 4 characters
                    "$"
        )

        return if (user.isEmpty()){
            binding.userRegister.error = getString(R.string.empty_msj)
            false
        }else if(!validatorRegex.matcher(user).matches()){
            binding.userRegister.error = getString(R.string.user) +" "+ getString(R.string.validm)
            false
        }else{
            binding.userRegister.error = null
            true
        }
    }
    private fun validatePassword(): Boolean {
        val password = binding.password1Register.editText?.text.toString()
        val validatorRegex = Pattern.compile(
            "^" +
                    "(?=.*[0-9])" +         // Al menos un numero
                    "(?=\\S+$)" +           //sin espacios en blanco
                    ".{4,}" +               //minimo 4 caracteres
                    "$"
        )

        return if (password.isEmpty()){
            binding.password1Register.error = getString(R.string.empty_msj)
            false
        }else if(!validatorRegex.matcher(password).matches()){
            binding.password1Register.error = getString(R.string.password) +" "+ getString(R.string.validf)
            false
        }else{
            binding.password1Register.error = null
            true
        }
    }
    private fun validatePassword2(): Boolean {
        val password = binding.password2Register.editText?.text.toString()
        val password2 = binding.password1Register.editText?.text.toString()
        val validatorRegex = Pattern.compile(
            "^" +
                    "(?=.*[0-9])" +         // Al menos un numero
                    "(?=\\S+$)" +           //sin espacios en blanco
                    ".{4,}" +               //minimo 4 caracteres
                    "$"
        )

        return if (password != password2){
            binding.password2Register.error = getString(R.string.invalid_password)
            false
        }else if(!validatorRegex.matcher(password).matches()) {
            binding.password2Register.error = getString(R.string.empty_msj)
            false
        }else{
            binding.password2Register.error = null
            true
        }
    }
    private fun validateEmail(): Boolean {
        val email = binding.emailRegister.editText?.text.toString()


        return if (email.isEmpty()){
            binding.emailRegister.error = getString(R.string.empty_msj)
            false
        }else if(!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailRegister.error = getString(R.string.email) +" "+ getString(R.string.validm)
            false
        }else{
            binding.emailRegister.error = null
            true
        }
    }
    private fun validation(){
        val result = arrayOf(validateUser(),validatePassword(),validatePassword2(),validateEmail())
        if (false in result){
            return
        }else if(!binding.checkBox.isChecked) {
            Toast.makeText(this, getString(R.string.terms_validate), Toast.LENGTH_SHORT).show()
        }else {
            registerUser()
            val home = Intent(this, HomeActivity::class.java)
            startActivity(home)
            finish()
        }
    }
    private fun registerUser(){
        val name= binding.userRegister.editText?.text.toString()
        val key= binding.password1Register.editText?.text.toString()
        val email=binding.emailRegister.editText?.text.toString()
        val vision=binding.diseaseRegister.editText?.text.toString()
        restClient.httpPostAsync("/create/user", "user=$name&password=$key&email=$email&vision=$vision")
        //"user=John&password=3125&email=jasolanob@uqvirtual.edu.co&vision=low" http://localhost:8080/create/user
        GlobalScope.launch {
            var response = restClient.wait()

            runOnUiThread {

                if (response == "User already exists") {
                    val toast1 = Toast.makeText(
                        applicationContext,
                        "User is already registered", Toast.LENGTH_LONG
                    )
                    toast1.show()

                } else {
                    val toast2 = Toast.makeText(
                        applicationContext,
                        "$name was registered...api:$response", Toast.LENGTH_LONG
                    )
                    toast2.show()

                    }
                }
            }

            //POST /create/user -> parameters: user and password , response: API key (auth) for this user if it does not exists or "User already exists" otherwise


        }

    override fun finish() {
        super.finish()
    }

}