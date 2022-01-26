package com.example.horusmap10

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val contrast: Button = requireView().findViewById(R.id.routes_button)
        contrast.setOnClickListener {
            actividadContenedora!!.starRoute(true)
        }
        val setting: Button = requireView().findViewById(R.id.settings_button)
        setting.setOnClickListener {
            actividadContenedora!!.settings(true)
        }
        val sos: Button = requireView().findViewById(R.id.emergency_button)
        sos.setOnClickListener {
            actividadContenedora!!.sos(true)
        }
        val logout: Button = requireView().findViewById(R.id.logout_button)
        logout.setOnClickListener {
            actividadContenedora!!.logout(true)
        }
        val mic: ImageButton = requireView().findViewById(R.id.mic_button)
        mic.setOnClickListener {
            actividadContenedora!!.mic(true)
        }


    }

    interface ComunicadorFragmentsHome {
        fun starRoute(dato: Boolean)
        fun settings(dato: Boolean)
        fun sos(dato: Boolean)
        fun mic(dato: Boolean)
        fun logout(dato:Boolean)
    }
    private var actividadContenedora : ComunicadorFragmentsHome? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if ( context is ComunicadorFragmentsHome)
            actividadContenedora = context
        else throw  RuntimeException(context.toString()+"debe implementar comunicador de fragments")
    }

    override fun onDestroy() {
        super.onDestroy()
        actividadContenedora  = null
    }

}