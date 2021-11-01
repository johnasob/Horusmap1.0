package com.example.horusmap10

import RESTClient
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.PatternsCompat
import com.example.horusmap10.databinding.ActivityEditProfileBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.regex.Pattern


class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var restClient: RESTClient
    private lateinit var thisActivity: EditProfileActivity
    private var apikey: String = ""
    private var name: String = ""
    private var password: String = "3125"
    private var email: String = ""
    private var vision: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        thisActivity=this

        val ip = "192.168.1.11:8080"
        restClient = RESTClient("http://$ip/")
        apikey = intent.getStringExtra("apikey").toString()
        update()

        binding.finishButton.setOnClickListener(){
            validation()
        }

        binding.changeButton.setOnClickListener(){
            binding.changeButton.visibility = View.INVISIBLE
            binding.visionEdit.visibility = View.VISIBLE

            //Se coloca la lista de enfermedades
            val diseases= resources.getStringArray(R.array.diseases)
            val arrayAdapter= ArrayAdapter(this, R.layout.dropdown_menu, diseases)
            with(binding.autoProfile){
                setAdapter(arrayAdapter)
            }
        }


    }


    private fun validateUser(): Boolean {
        val user = binding.userEdit.editText?.text.toString()
        val validatorRegex = Pattern.compile(
            "^" +
                    "(?=\\S+$)" +           //no white spaces
                    ".{5,}" +               //at least 4 characters
                    "$"
        )

        return if (user.isEmpty()){
            true
        }else if(!validatorRegex.matcher(user).matches()){
            binding.userEdit.error = getString(R.string.user) +" "+ getString(R.string.validm)
            false
        }else{
            binding.userEdit.error = null
            name = user
            true
        }
    }

    private fun validateEmail(): Boolean {
        val email_edit = binding.emailEdit.editText?.text.toString()


        return if (email_edit.isEmpty()){
            true
        }else if(!PatternsCompat.EMAIL_ADDRESS.matcher(email_edit).matches()){
            binding.emailEdit.error = getString(R.string.email) +" "+ getString(R.string.validm)
            false
        }else{
            binding.emailEdit.error = null
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
            if( !binding.visionEdit.editText?.text.toString().isEmpty()) {
                vision = binding.visionEdit.editText?.text.toString()

            }
            restClient.httpPutAsync("/update/user?auth=$apikey", "user=$name&password=$password&email=$email&vision=$vision")
            var profile = Intent(this, ProfileActivity::class.java)
            GlobalScope.launch {
                var response = restClient.wait()
                runOnUiThread() {
                    if(response == "200") {

                        val toast1 = Toast.makeText(
                            applicationContext,
                            "actualización exitosa",
                            Toast.LENGTH_LONG
                        )
                        toast1.show()
                        profile.putExtra("apikey", apikey)
                        startActivity(profile)
                        finish()

                    }else{

                        val toast1 = Toast.makeText(
                            applicationContext,
                            response,
                            Toast.LENGTH_LONG
                        )
                        toast1.show()

                    }
                }
            }
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
        }
    }
}
