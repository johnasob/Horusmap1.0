

package com.example.horusmap10

import RESTClient
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.PatternsCompat
import com.example.horusmap10.databinding.ActivityProfileBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.regex.Pattern

//clase que contiene la información tipo json del usuario
data class userInfo(
    val name: String,
    val password: String,
    val email: String,
    val vision: String
    )

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
        val ip = "192.168.1.13:8080"
        restClient = RESTClient("http://$ip/")
        apikey = intent.getStringExtra("apikey").toString()
        //val apikey = "6169add27908455ee5ed8df4"
        restClient.httpGetAsync("/user/info?auth=$apikey")



        //Se busca acceder a la información actual del servidor
        GlobalScope.launch {
            val myUserInfo = restClient.wait()

            val list = JSONObject(myUserInfo)
            name = list.getString("Name: ")
            password = list.getString("Password: ")
            email = list.getString("Email: ")
            vision = list.getString("Vision: ")

            runOnUiThread {
                binding.nameProfile1.text = name
                binding.emailProfile1.text = email
                binding.visionProfile1.text = vision
            }
        }
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
        binding.editButton.setOnClickListener(){
            binding.userProfile2.editText?.hint = name
            binding.emailProfile2.editText?.hint = email
            binding.visionProfile.editText?.hint = vision
            edit()
        }
        binding.finishButton.setOnClickListener(){

            validation()
        }
    }
    private fun edit(){
        binding.nameProfile1.visibility = View.INVISIBLE
        binding.finishButton.visibility = View.VISIBLE
        binding.backButton.visibility = View.INVISIBLE
        binding.editButton.visibility = View.INVISIBLE
        binding.userProfile2.visibility = View.VISIBLE
        binding.emailProfile2.visibility = View.VISIBLE
        binding.visionProfile1.visibility = View.INVISIBLE
        binding.changeButton.visibility = View.VISIBLE

        binding.changeButton.setOnClickListener(){
            binding.changeButton.visibility = View.INVISIBLE
            binding.visionProfile.visibility = View.VISIBLE

            //Se coloca la lista de enfermedades
            val diseases= resources.getStringArray(R.array.diseases)
            val arrayAdapter= ArrayAdapter(this, R.layout.dropdown_menu, diseases)
            with(binding.autoProfile){
                setAdapter(arrayAdapter)
            }
            vision = binding.visionProfile.editText?.text.toString()
        }
    }

    private fun finishEdit(){
        binding.nameProfile1.visibility = View.VISIBLE
        binding.finishButton.visibility = View.INVISIBLE
        binding.backButton.visibility = View.VISIBLE
        binding.editButton.visibility = View.VISIBLE
        binding.userProfile2.visibility = View.INVISIBLE
        binding.emailProfile2.visibility = View.INVISIBLE
        binding.visionProfile.visibility = View.INVISIBLE
        binding.visionProfile1.visibility = View.VISIBLE

        makeText(this, getString(R.string.finish_edit), LENGTH_SHORT).show()
    }
    private fun validateUser(): Boolean {
        val user = binding.userProfile2.editText?.text.toString()
        val validatorRegex = Pattern.compile(
            "^" +
                    "(?=\\S+$)" +           //no white spaces
                    ".{5,}" +               //at least 4 characters
                    "$"
        )

        return if (user.isEmpty()){
            true
        }else if(!validatorRegex.matcher(user).matches()){
            binding.userProfile2.error = getString(R.string.user) +" "+ getString(R.string.validm)
            false
        }else{
            binding.userProfile2.error = null
            name = user
            true
        }
    }

    private fun validateEmail(): Boolean {
        val email_edit = binding.emailProfile2.editText?.text.toString()


        return if (email_edit.isEmpty()){
            true
        }else if(!PatternsCompat.EMAIL_ADDRESS.matcher(email_edit).matches()){
            binding.emailProfile2.error = getString(R.string.email) +" "+ getString(R.string.validm)
            false
        }else{
            binding.emailProfile2.error = null
            email = email_edit
            true
        }
    }

    private fun validation(){
        val result = arrayOf(validateUser(),validateEmail())
        if (false in result){
            return
        }else{
            //si no existe errores finalza el la edición
            restClient.httpPutAsync("/update/user?auth=$apikey", "name=$name&password=$password&email=$email&vision=$vision")
            makeText(this,"name=$name&password=$password&email=$email&vision=$vision", LENGTH_SHORT).show()
            GlobalScope.launch {
                var response = restClient.wait()
                runOnUiThread {
                    makeText(
                        getApplicationContext(),
                        "$response",
                        LENGTH_SHORT

                    ).show()

                }
            }
            finishEdit()
        }
    }
}