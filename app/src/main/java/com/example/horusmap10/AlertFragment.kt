package com.example.horusmap10

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment


class AlertFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alert, container, false)
    }
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val save2: Button = requireView().findViewById(R.id.button_save2)
        save2.setOnClickListener {

            actividadContenedora!!.devolverDato6(true)
        }

    }

    interface ComunicadorFragments6 {
        fun devolverDato6(dato: Boolean)
    }
    private var actividadContenedora : ComunicadorFragments6? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if ( context is ComunicadorFragments6)
            actividadContenedora = context
        else throw  RuntimeException(context.toString()+"debe implementar comunicador de fragments")
    }

    override fun onDestroy() {
        super.onDestroy()
        actividadContenedora  = null
    }

}