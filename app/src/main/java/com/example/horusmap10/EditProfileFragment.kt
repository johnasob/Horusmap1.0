package com.example.horusmap10

import RESTClient
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.util.PatternsCompat
import androidx.fragment.app.Fragment
import com.example.horusmap10.Horusmap1.Horusmap
import com.example.horusmap10.databinding.FragmentEditProfileBinding

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.regex.Pattern


class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {
    private lateinit var _binding: FragmentEditProfileBinding
    private val binding get() = _binding!!
    private lateinit var restClient: RESTClient
    private lateinit var apikey: String
    private lateinit var ip: String
    private lateinit var name: String
    private var password: String = "3125"
    private lateinit var email: String
    private lateinit var vision: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(layoutInflater, container,false )
        ip = Horusmap.prefs.getIp()
        apikey = Horusmap.prefs.getApikey()
        restClient = RESTClient("http://$ip/")
        update()
        binding.finishButton.setOnClickListener(){
             validation()
        }

        binding.changeButton.setOnClickListener(){
            binding.changeButton.visibility = View.INVISIBLE
            binding.visionEdit.visibility = View.VISIBLE

            //Se coloca la lista de enfermedades
            val diseases= resources.getStringArray(R.array.diseases)
            val arrayAdapter= ArrayAdapter(requireContext(), R.layout.dropdown_menu, diseases)
            with(binding.autoProfile){
                setAdapter(arrayAdapter)
            }
        }
        return binding.root
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

    private fun validation() {
        val result = arrayOf(validateUser(), validateEmail())
        if (false in result) {
            return
        } else {
            //si no existe errores finalza el la edición
            if (!binding.visionEdit.editText?.text.toString().isEmpty()) {
                vision = binding.visionEdit.editText?.text.toString()

            }
            Toast.makeText(requireContext(),"name=$name",Toast.LENGTH_LONG).show()
            restClient.httpPutAsync("/update/user?auth=$apikey", "user=$name&password=$password&email=$email&vision=$vision")
            GlobalScope.launch {
                var response = restClient.wait()
               if (response == "200") {
                   actividadContenedora!!.devolverDato2(true)
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
    interface ComunicadorFragments2 {
        fun devolverDato2(dato: Boolean)
    }
    private var actividadContenedora : ComunicadorFragments2? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if ( context is ComunicadorFragments2)
            actividadContenedora = context
        else throw  RuntimeException(context.toString()+"debe implementar comunicador de fragments")
    }

    override fun onDestroy() {
        super.onDestroy()
        actividadContenedora  = null
    }



}