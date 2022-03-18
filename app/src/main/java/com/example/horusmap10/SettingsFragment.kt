package com.example.horusmap10

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.horusmap10.Horusmap1.Horusmap


class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(Horusmap.prefs.getAlert()!="Desactivado") {
            Toast.makeText(
                requireContext(),
                "Usted se encuentra en la pestaña de configuraciones",
                Toast.LENGTH_SHORT
            ).show()
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val contrast: Button = requireView().findViewById(R.id.button_profile)
        contrast.setOnClickListener {
            actividadContenedora!!.devolverDato3(true)
        }
        val alert: Button = requireView().findViewById(R.id.button_alert)
        alert.setOnClickListener {
            actividadContenedora!!.devolverDato5(true)
        }
        val sos: Button = requireView().findViewById(R.id.button_sos)
        sos.setOnClickListener {
            actividadContenedora!!.devolverDato9(true)
        }
        val sound: Button = requireView().findViewById(R.id.button_sounds)
        sound.setOnClickListener {
            actividadContenedora!!.devolversounds(true)
        }

    }


    interface ComunicadorFragments3 {
        fun devolverDato3(dato: Boolean)
        fun devolverDato5(dato: Boolean)
        fun devolverDato7(dato: Boolean)
        fun devolverDato9(dato: Boolean)
        fun devolversounds(dato: Boolean)
    }
    private var actividadContenedora : ComunicadorFragments3? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if ( context is ComunicadorFragments3)
            actividadContenedora = context
        else throw  RuntimeException(context.toString()+"debe implementar comunicador de fragments")
    }

    override fun onDestroy() {
        super.onDestroy()
        actividadContenedora  = null
    }

}