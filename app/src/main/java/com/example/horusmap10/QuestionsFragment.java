package com.example.horusmap10;

import static com.example.horusmap10.Horusmap1.Horusmap.prefs;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.horusmap10.Horusmap1.Horusmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
/**PANTALLA DE PREGUNTAS FRECUENTES*/
public class QuestionsFragment extends Fragment {
    private ExpandableListView expLV;
    private ExpLVAdapter adapter;
    private ArrayList<String> listCategoria;
    private Map<String,ArrayList<String>> mapChild;
    private View view;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_questions, container, false);
        expLV =  (ExpandableListView) view.findViewById(R.id.expLV);
        listCategoria = new ArrayList<>();
        mapChild = new HashMap<>();
        cargarDatos();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(prefs.getAlert().toString()!="Desactivado") {
            Toast.makeText(
                    requireContext(),
                    "Usted se encuentra en la pestaña de preguntas y respuestas",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void cargarDatos(){
        /***AQUI VAN LAS PREGUNTAS*/
        ArrayList<String> pregunta1 = new ArrayList<>();
        ArrayList<String> pregunta2 = new ArrayList<>();
        ArrayList<String> pregunta3 = new ArrayList<>();
        ArrayList<String> pregunta4 = new ArrayList<>();
        ArrayList<String> pregunta5 = new ArrayList<>();

        listCategoria.add("¿ Que pasa si la aplicación se cerra repentinamente?");
        listCategoria.add("¿ Por que mi ubicación no coincide?");
        listCategoria.add("¿ Puedo usar la aplicación sin internet?");
        listCategoria.add("¿ Por que falla la navegación interna en la facultad de ingeniería electrónica?");
        listCategoria.add("¿ Puedo usar la app sin ser tener algun tipo de discapacidad visual?");

        pregunta1.add("Debes comunicar tu fallo al siguiente correo:SoporteHorusMap@gmail.com con el asunto \n" +
                "\"fallo de la app horusmap\"");
        pregunta2.add("Si bien el GPS de los celulares cuentan con una buena medida\n" +
                "es posible que en ciertos dispositivos la calidad de este sensor influirá\n" +
                "en la presición con la que este represente su ubicación, se recomienda tener\n" +
                "una buena conexión a internet.");
        pregunta3.add("Puede usarla siempre y cuando no requiera del apartado de editar y \n" +
                "ver datos actuales del usuario, tampoco podra volver a iniciar sesión \n" +
                "si su dispositivo no se encuentra conectado a internet. Es posible que \n" +
                "la presición del GPS baje si se desactiva el internet.");
        pregunta4.add("Si bien, el GPS presenta fallas dentro de edificios , nuestro sistema\n" +
                "esta capacitado para funcionar dentro de la facultad de ingeniería de la Universidad\n" +
                "del Quindío, siempre y cuando su dispositivo movil tenga activado el \n" +
                "bluetooth.");
        pregunta5.add("Claro que lo puede hacer, ya que la app tiene apartados visuales y \n" +
                "alertas que personas con o sin discapacidad visual puedan entender y \n" +
                "usar la app.");

        mapChild.put(listCategoria.get(0),pregunta1);
        mapChild.put(listCategoria.get(1),pregunta2);
        mapChild.put(listCategoria.get(2),pregunta3);
        mapChild.put(listCategoria.get(3),pregunta4);
        mapChild.put(listCategoria.get(4),pregunta5);

        adapter = new ExpLVAdapter(requireContext(),listCategoria,mapChild);
        expLV.setAdapter(adapter);
    }
}