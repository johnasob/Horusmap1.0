

package com.example.horusmap10

import RESTClient
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.util.PatternsCompat
import com.example.horusmap10.databinding.ActivityHomeBinding
import com.example.horusmap10.databinding.ActivityProfileBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.nio.file.Files
import java.util.regex.Pattern


class ProfileActivity : AppCompatActivity() {
    //Se crea la vista binding
    private lateinit var binding: ActivityProfileBinding
    private lateinit var restClient: RESTClient
    private lateinit var thisActivity: ProfileActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        thisActivity=this
        val ip = "192.168.1.13:8080"
        //sharedPreferences.getString("ipaddress", "192.168.1.4:8080")""
        restClient = RESTClient("http://$ip/")
        //var apikey = intent.getStringExtra("apikey")
        //restClient.httpGetAsync("/user/info?auth=$apikey")
        //GlobalScope.launch {
          //  val archivos = restClient.wait()
            //val list = Gson().fromJson(archivos, Files::class.java)
            //val listAccess = fun(i: Int): String {return list.files[i].filename}
            //val list2 = List( list.files.size) { item -> listAccess(item) + "\n"}

            //runOnUiThread {

            //}
        //}
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
            edit()
        }
        binding.finishButton.setOnClickListener(){
            validation()
        }
    }
    private fun edit(){
        binding.nameProfile1.visibility = android.view.View.INVISIBLE
        binding.finishButton.visibility = android.view.View.VISIBLE
        binding.backButton.visibility = android.view.View.INVISIBLE
        binding.editButton.visibility = android.view.View.INVISIBLE
        binding.userProfile2.visibility = android.view.View.VISIBLE
        binding.emailProfile2.visibility = android.view.View.VISIBLE
        binding.visionProfile.visibility = android.view.View.VISIBLE
        val diseases= resources.getStringArray(R.array.diseases)
        val arrayAdapter= ArrayAdapter(this, R.layout.dropdown_menu, diseases)
        with(binding.autoProfile){
            setAdapter(arrayAdapter)
        }
    }
    private fun finishEdit(){
        binding.nameProfile1.visibility = android.view.View.VISIBLE
        binding.finishButton.visibility = android.view.View.INVISIBLE
        binding.backButton.visibility = android.view.View.VISIBLE
        binding.editButton.visibility = android.view.View.VISIBLE
        binding.userProfile2.visibility = android.view.View.INVISIBLE
        binding.emailProfile2.visibility = android.view.View.INVISIBLE
        binding.visionProfile.visibility = android.view.View.INVISIBLE
        binding.visionProfile1.visibility = android.view.View.VISIBLE

        Toast.makeText(this, getString(R.string.finish_edit), Toast.LENGTH_SHORT).show()
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
            binding.userProfile2.error = getString(R.string.empty_msj)
            false
        }else if(!validatorRegex.matcher(user).matches()){
            binding.userProfile2.error = getString(R.string.user) +" "+ getString(R.string.validm)
            false
        }else{
            binding.userProfile2.error = null
            true
        }
    }

    private fun validateEmail(): Boolean {
        val email = binding.emailProfile2.editText?.text.toString()


        return if (email.isEmpty()){
            binding.emailProfile2.error = getString(R.string.empty_msj)
            false
        }else if(!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailProfile2.error = getString(R.string.email) +" "+ getString(R.string.validm)
            false
        }else{
            binding.emailProfile2.error = null
            true
        }
    }

    private fun validation(){
        val result = arrayOf(validateUser(),validateEmail())
        if (false in result){
            return
        }else{
            finishEdit()
        }
    }
}