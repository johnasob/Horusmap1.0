package com.example.horusmap10

import RESTClient
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.horusmap10.Horusmap1.Horusmap.Companion.prefs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject


class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var restClient: RESTClient
    private var apikey: String? = null
    private var ip: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ip = prefs.getIp()
        apikey = prefs.getApikey()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    private fun update(){

        restClient = RESTClient("http://$ip/")
        restClient.httpGetAsync("/user/info?auth=$apikey")


        GlobalScope.launch {
            val myUserInfo = restClient.wait()
            val list = JSONObject(myUserInfo)

            val name: String = list.getString("Name")

            val name_t: TextView = requireView().findViewById(R.id.name_profile1)
            name_t.text = name

            val password = list.getString("Password")

            val email: String = list.getString("Email")
            val email_t: TextView = requireView().findViewById(R.id.email_profile1)
            email_t.text = email

            val vision: String = list.getString("Vision")
            val vision_t: TextView = requireView().findViewById(R.id.vision_profile1)
            vision_t.text = vision
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        update()
        val edit: Button = requireView().findViewById(R.id.edit_button)
        edit.setOnClickListener {

            actividadContenedora!!.devolverDato(true)
        }
        val logout: Button = requireView().findViewById(R.id.out_button)
        logout.setOnClickListener {
            actividadContenedora!!.finishSesion(true)
        }
    }
    interface ComunicadorFragments {
        fun devolverDato(dato: Boolean)
        fun finishSesion(dato: Boolean)
    }
    private var actividadContenedora : ComunicadorFragments? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if ( context is ComunicadorFragments)
        actividadContenedora = context
        else throw  RuntimeException(context.toString()+"debe implementar comunicador de fragments")
    }

    override fun onDestroy() {
        super.onDestroy()
        actividadContenedora  = null
    }


}