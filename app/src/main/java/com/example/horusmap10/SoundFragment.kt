package com.example.horusmap10

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.horusmap10.Horusmap1.Horusmap.Companion.prefs


class SoundFragment : Fragment() {
    lateinit var mediaPlayer: MediaPlayer
    private lateinit var state: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sound, container, false)
    }

    @SuppressLint("UseRequireInsteadOfGet", "UseSwitchCompatOrMaterialCode", "CutPasteId")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        state = prefs.getSounds()
        Toast.makeText(requireContext(),"EL estado actual de las alertas sonoras es:  "+ prefs.getSounds(),
            Toast.LENGTH_SHORT).show()
        val save: Button = requireView().findViewById(R.id.button_save_sounds)
        save.setOnClickListener {
            prefs.saveSounds(state)
            actividadContenedora!!.sounds(true)
        }

        val noti: Switch = requireView().findViewById(R.id.switch_sounds)
        if(prefs.getSounds() == "Activado"){
            playsound("sound")
            requireView().findViewById<Switch>(R.id.switch_sounds).isChecked =true
        }else if(prefs.getSounds() == "Desactivado")
        {
            requireView().findViewById<Switch>(R.id.switch_sounds).isChecked =false
        }
        noti.setOnCheckedChangeListener {buttonView, isChecked ->
            if(isChecked){
                Toast.makeText(requireContext(),"Alertas sonoras activadas",Toast.LENGTH_SHORT).show()
                playsound("sound")
                state="Activado"
            }else{
                Toast.makeText(requireContext(),"Alertas sonoras desactivadas",Toast.LENGTH_SHORT).show()
                state="Desactivado"
            }
        }

    }
    interface ComunicadorFragmentsSounds {
        fun sounds(dato: Boolean)
    }
    private var actividadContenedora : ComunicadorFragmentsSounds? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if ( context is ComunicadorFragmentsSounds)
            actividadContenedora = context
        else throw  RuntimeException(context.toString()+"debe implementar comunicador de fragments")
    }

    override fun onDestroy() {
        super.onDestroy()
        actividadContenedora  = null
    }
    private fun playsound(archivo: String) {
        mediaPlayer = when (archivo) {
            "start" -> MediaPlayer.create(requireContext(), R.raw.starttono)
            "error" -> MediaPlayer.create(requireContext(), R.raw.errortono)
            "sound" -> MediaPlayer.create(requireContext(), R.raw.dostonos)
            "llegada" -> MediaPlayer.create(requireContext(), R.raw.tonosllegada)
            else -> MediaPlayer.create(requireContext(), R.raw.dostonos)
        }
        mediaPlayer.start() // no need to call prepare(); create() does that for you
    }


}