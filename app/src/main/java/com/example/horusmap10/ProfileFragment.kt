package com.example.horusmap10

import RESTClient
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject



class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var restClient: RESTClient
    private var apikey: String? = null
    private var ip: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            ip = arguments?.getString("ip", )
            apikey = arguments?.getString("apikey")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    private fun update(){
        //ip = "192.168.1.11:8080"
        //apikey = "61718e0887d4577337a2b329"
        restClient = RESTClient("http://$ip/")
        restClient.httpGetAsync("/user/info?auth=$apikey")

        //Se busca acceder a la información actual del servidor
        GlobalScope.launch {
            val myUserInfo = restClient.wait()
            val list = JSONObject(myUserInfo)

            val name: String = list.getString("Name: ")
            val name_t: TextView = requireView().findViewById(R.id.name_profile1)
            name_t.setText(name)

            val password = list.getString("Password: ")

            val email: String = list.getString("Email: ")
            val email_t: TextView = requireView().findViewById(R.id.email_profile1)
            email_t.setText(email)

            val vision: String = list.getString("Vision: ")
            val vision_t: TextView = requireView().findViewById(R.id.vision_profile1)
            vision_t.setText(vision)
        }


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        update()
        var logout: Button = requireView().findViewById(R.id.loguot_button)
        logout.setOnClickListener(){
            var intent = Intent(getActivity(), MainActivity::class.java)
            getActivity()?.startActivity(intent)
            getActivity()?.finish()
        }

    }
}