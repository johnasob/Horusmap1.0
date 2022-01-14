package com.example.horusmap10

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class ContrastFragment : Fragment() {

    private lateinit var pruebaColor: String
    private lateinit var window: Window

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window = requireActivity().window
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contrast, container, false)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val default: Button = requireView().findViewById(R.id.button_default)
        val contrast: Button = requireView().findViewById(R.id.button_contraste)
        val gray: Button = requireView().findViewById(R.id.button_gray)
        val save: Button = requireView().findViewById(R.id.button_save)

        save.setOnClickListener {

            actividadContenedora!!.devolverDato4(true)
        }
        default.setOnClickListener {
            Toast.makeText(requireContext(),getString(R.string.prueba_color),Toast.LENGTH_LONG).show()
        }
        contrast.setOnClickListener {
        }
        gray.setOnClickListener {
        }

    }

    interface ComunicadorFragments4 {
        fun devolverDato4(dato: Boolean)
    }
    private var actividadContenedora : ComunicadorFragments4? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if ( context is ComunicadorFragments4)
            actividadContenedora = context
        else throw  RuntimeException(context.toString()+"debe implementar comunicador de fragments")
    }

    override fun onDestroy() {
        super.onDestroy()
        actividadContenedora  = null
    }

}