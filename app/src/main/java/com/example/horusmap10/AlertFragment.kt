package com.example.horusmap10

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.horusmap10.Horusmap1.Horusmap.Companion.prefs


class AlertFragment : Fragment() {

    private lateinit var state: String
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
    @SuppressLint("UseRequireInsteadOfGet", "UseSwitchCompatOrMaterialCode", "CutPasteId")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Toast.makeText(requireContext(),"EL estado actual de las alertas es:  "+prefs.getAlert(),Toast.LENGTH_SHORT).show()

        val save2: Button = requireView().findViewById(R.id.button_save2)
        save2.setOnClickListener {
            prefs.saveAlert(state)
           actividadContenedora!!.devolverDato6(true)
        }

        val noti: Switch = requireView().findViewById(R.id.switch_alert)
        if(prefs.getAlert() == "Activado"){
            requireView().findViewById<Switch>(R.id.switch_alert).isChecked =true
        }else if(prefs.getAlert() == "Desactivado")
        {
            requireView().findViewById<Switch>(R.id.switch_alert).isChecked =false
        }
        noti.setOnCheckedChangeListener {buttonView, isChecked ->
            if(isChecked){
                Toast.makeText(requireContext(),"Alertas activadas",Toast.LENGTH_SHORT).show()
                state="Activado"
            }else{
                Toast.makeText(requireContext(),"Alertas desactivadas",Toast.LENGTH_SHORT).show()
                state="Desactivado"
            }
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