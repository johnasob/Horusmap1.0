package com.example.horusmap10;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.horusmap10.Horusmap1.Horusmap.prefs;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.INotificationSideChannel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.horusmap10.Rutas.Alerts;
import com.example.horusmap10.Rutas.Point;
import com.example.horusmap10.Rutas.line;

import com.example.horusmap10.databinding.FragmentRoutesBinding;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Objects;


public class RoutesFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    View vista;
    Activity actividad;
    Context context;
    String mostrador;
    MediaPlayer mediaPlayer;
    String sounds;
    String notificacion;
    private FragmentRoutesBinding _binding;
    private Point closePoint;
    private AutoCompleteTextView spinner;
    public LatLng myPosition = new LatLng(4.556286, -75.658436);
    public int stations = 0;
    private GoogleMap mMap;
    private final line marker = new line(mMap);
    private LocationManager location;
    private Alerts alertas;
    private int station;
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        public void onLocationChanged(Location location) {
        }

        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {

            mMap = googleMap;
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            LocationManager locationManager = (LocationManager) requireContext().getSystemService(LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onLocationChanged(Location location) {
                    mMap.clear();
                    // se accede a las opciones de ubicación
                    myPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap = marker.addMarkers(mMap);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 18), 1000, null);
                    choiseOption(myPosition);

                }
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }


    };

    private void getLocationPermision() {
        int permiso = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permiso == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions((Activity) requireContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getLocationPermision();
        _binding = FragmentRoutesBinding.inflate(getLayoutInflater());
        vista = _binding.getRoot();
        prefs.saveMostrador("inicio");
        spinner = _binding.options;
        spinner.setOnItemSelectedListener(this);
        String[] route = vista.getResources().getStringArray(R.array.routes);
        ArrayAdapter adapter = new ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, route);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        this.mMap = mMap;
        this.context = context;
        return vista;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void sayRoute(int distance, Point closePoint) {
        notification("Camina  " + distance + " metros y te encontraras con " + closePoint.name());
    }

    /**
     * Metodo que encuentra la distancia entre dos coordenadas
     */
    private double getDistance(LatLng start, LatLng finish) {
        //Inicializa el locationrequest que se usa para encontrar la distancia
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        float[] distances = new float[1];
        //Calcula la distancia
        Location.distanceBetween(start.latitude, start.longitude, finish.latitude, finish.longitude, distances);
        return distances[0];
    }

    private void getStation(LatLng myPosition){
        double longitude = myPosition.longitude;
        double latitude = myPosition.latitude;

        // ESTACION PORTERIA
        if((latitude>4.556050)&&(latitude<4.556571)&&(longitude>-75.658250)&&(longitude<-75.658840)){
            station =0;
        }
    }

    /**
     * Calculo de el punto más cercano al punto dado
     */
    private void closerPoint(LatLng start) {
        double[] distance = new double[7];
        distance[0] = getDistance(start, line.porteria);
        distance[1] = getDistance(start, line.cajero);
        distance[2] = getDistance(start, line.medicina);
        distance[3] = getDistance(start, line.capilla);
        distance[4] = getDistance(start, line.biblioteca);
        distance[5] = getDistance(start, line.escaleras);
        distance[6] = getDistance(start, line.ingenieria);
        double min = distance[0];
        int pos = 0;

        for (int i = 0; i < distance.length; i++) {
            if (distance[i] <= min) {
                min = distance[i];
                pos = i;
            }
        }
        if (distance[pos] >= 80) {
            pos = 7;
        }
        switch (pos) {
            case line.PORTERIA:
                if (distance[pos] <= 10) {
                    closePoint = new Point(line.porteria, "porteria exacto");
                } else {
                    closePoint = new Point(line.porteria, "porteria");
                }
                break;
            case line.CAJERO:
                if (distance[pos] <= 10) {
                    closePoint = new Point(line.cajero, "cajero exacto");
                } else {
                    closePoint = new Point(line.cajero, "cajero");
                }
                break;
            case line.MEDICINA:
                if (distance[pos] <= 10) {
                    closePoint = new Point(line.medicina, "medicina exacto");
                } else {
                    closePoint = new Point(line.medicina, "medicina");
                }
                break;
            case line.CAPILLA:
                if (distance[pos] <= 10) {
                    closePoint = new Point(line.capilla, "capilla exacto");
                } else {
                    closePoint = new Point(line.capilla, "capilla");
                }
                break;
            case line.BIBLIOTECA:
                if (distance[pos] <= 10) {
                    closePoint = new Point(line.biblioteca, "biblioteca exacto");
                } else {
                    closePoint = new Point(line.biblioteca, "biblioteca");
                }
                break;
            case line.ESCALERAS:
                if (distance[pos] <= 10) {
                    closePoint = new Point(line.escaleras, "escaleras exacto");
                } else {
                    closePoint = new Point(line.escaleras, "escaleras");
                }
                break;
            case line.INGENIERIA:
                if (distance[pos] <= 10) {
                    closePoint = new Point(line.ingenieria, "ingenieria exacto");
                } else {
                    closePoint = new Point(line.ingenieria, "ingenieria");
                }
                break;
            default:
                LatLng nulo = new LatLng(0, 0);
                closePoint = new Point(nulo, "estas demasiado lejos");
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void choiseOption(LatLng myPosition){

        if (_binding.options.getText().toString().equals("Porteria a Facultad de ingenieria")){

                    mostrador = prefs.getMostrador();
                    notificacion = prefs.getAlert();
                    sounds  = prefs.getSounds();

            if(mostrador != "porteria") {

                        prefs.saveMostrador("porteria");

                        if(notificacion == "Activado") {
                            notification("Usted ha seleccionado la ruta porteria a Facultad de ingeniería");
                        }
                        if(sounds == "Activado"){
                            playsound("start");
                        }
                    }

                    creatRoutePorteria(myPosition);

        }
        if (_binding.options.getText().toString().equals("Facultad de ingenieria a porteria")){
            notification("Has seleccionado la ruta: Facultad de ingeniería a Porteria");
            mostrador = "ingenieria";
            prefs.saveMostrador(mostrador);
            creatRouteIngenieria(myPosition);
        }
        if (_binding.options.getText().toString().equals("Navegación Interna")){
            notification("Iniciando navegación interna");
            ((StartRouteActivity) Objects.requireNonNull(getActivity())).InDoorNotification();
        }
    }
    private void playsound(String archivo) {

        switch (archivo) {
            case "start":
                mediaPlayer = MediaPlayer.create(requireContext(), R.raw.starttono);
                break;
            case "error":
                mediaPlayer = MediaPlayer.create(requireContext(), R.raw.errortono);
                break;
            case "sound":
                mediaPlayer =MediaPlayer.create(requireContext(), R.raw.dostonos);
                break;
            case "llegada":
                mediaPlayer =MediaPlayer.create(requireContext(), R.raw.tonosllegada);
                break;
            default:
                mediaPlayer =MediaPlayer.create(requireContext(), R.raw.dostonos);
                break;
        }
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
    }
    private void creatRoutePorteria(LatLng start){
        notificacion = prefs.getAlert();
        Toast.makeText(requireContext(), notificacion, Toast.LENGTH_SHORT).show();
        sounds  = prefs.getSounds();
        closerPoint(myPosition);
        Polyline Ruta1;
        int distance=0;
        int distanceFinish = (int)getDistance(myPosition,line.ingenieria);
        switch (closePoint.name()) {
            case "porteria":
                distance = (int) getDistance(start, closePoint.coor());
                Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.porteria, line.medicina, line.biblioteca, line.escaleras, line.ingenieria));
                if(notificacion == "Activado") {
                    notification("Estas cerca de ");
                }
                stations = 0;
                break;
            case "porteria exacto":
                distance = (int) getDistance(myPosition, line.biblioteca);
                Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.medicina, line.biblioteca, line.escaleras, line.ingenieria));
                if(notificacion == "Activado") {
                    notification("Haz llegado a la porteria 2 de la Universidad del Quindío");
                }
                stations = 1;
                break;
            case "cajero":
                distance = (int) getDistance(myPosition, line.cajero);
                Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.medicina, line.biblioteca, line.escaleras, line.ingenieria));
                if(notificacion == "Activado") {
                    notification("Estas cerca de la facultad de ciencias de la salud");
                }
                stations = 1;
                break;
            case "cajero exacto":
                distance = (int) getDistance(myPosition, line.biblioteca);

                Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.medicina, line.biblioteca, line.escaleras, line.ingenieria));
                if(notificacion == "Activado") {
                    notification("Estas muy cerca de la entrada de la facultad de ciencias de la salud");
                }
                stations = 2;
                break;
            case "medicina":
                if(notificacion == "Activado") {
                    notification("Te estas moviendo cerca a la facultad de ciencias de la salud");
                }
                distance = (int) getDistance(myPosition, line.biblioteca);
                stations = 2;
                Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.biblioteca, line.escaleras, line.ingenieria));
                break;
            case "medicina exacto":
                if(notificacion == "Activado") {
                    notification("Vas caminando de la facultad de medicina a la biblioteca");
                    notification("continua caminando para llegar a la biblioteca");
                }
                distance = (int) getDistance(myPosition, line.biblioteca);
                Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.biblioteca, line.escaleras, line.ingenieria));
                stations = 3;
                break;
            case "biblioteca":
                distance = (int) getDistance(myPosition, line.biblioteca);
                if(notificacion == "Activado") {
                    notification("Estas llegando a la entrada de la biblioteca CRAI");
                }
                Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.biblioteca,line.escaleras,line.ingenieria));
                stations=3;
                break;
            case "biblioteca exacto":
                distance=(int)getDistance(myPosition,line.escaleras);
                Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.escaleras,line.ingenieria));
                if(notificacion == "Activado") {
                    notification("Haz llegado a la biblioteca CRAI");
                }
                stations=4;
                break;
            case "escaleras":
                distance=(int)getDistance(myPosition,line.escaleras);
                if(notificacion == "Activado") {
                    notification("Estas llegando a las escaleras cercanas a al bloque de ingenieria");
                }
                Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.escaleras,line.ingenieria));
                stations=4;
                break;
            case "escaleras exacto":
                distance=(int)getDistance(myPosition,line.ingenieria);
                if(notificacion == "Activado") {
                    notification("Haz llegado a las escaleras de ingenieria");
                }
                Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.ingenieria));
                stations=5;
                break;
            case "ingenieria":
                distance=(int)getDistance(myPosition,line.ingenieria);
                Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.ingenieria));
                if(notificacion == "Activado") {
                    notification("Estas cerca de la facultad de ingenieria");
                }
                stations=5;
                break;
            case "ingenieria exacto":
                if(notificacion == "Activado") {
                    notification("Haz llegado a la facultad de ingenieria");
                }
                stations=6;
                break;
            default:
                distance = (int) getDistance(myPosition, line.porteria);
                stations = 8;
                break;
        }

        switch (stations){
            case 0:
                /*if(prefs.getSounds() == "Activado") {
                    playsound("sound");
                }*/
                if (distance >= 80) {
                    if(notificacion == "Activado") {
                        notification("Debes acercarte más a la porteria 2 de la Universidad del Quindío" +
                                "para iniciar tu recorrido");
                    }
                } else {
                        sayRoute(distance, closePoint);
                    if(notificacion == "Activado") {
                        notification("Recuerda tener cuidado ya que es una entrada vehicular y peatonal");
                    }
                }
                break;
            case 1:
                if(prefs.getSounds() == "Activado") {
                    playsound("sound");
                }
                if(notificacion == "Activado") {
                    notification("Camina " + distance + " metros por la acera podotactil y te encontraras con la entrada al bloque de salud");
                }
                break;
            case 2:
                if(notificacion == "Activado") {
                    notification("Estas a: " + distance + " metros  de la biblioteca, pasando el bloque de la facultad de ciencias de la salud");
                }
                break;
            case 3:
                if(notificacion == "Activado") {
                    notification("Sigue caminando: " + distance + " metros por la acera podotactil y llegara a la biblioteca");
                }
                break;
            case 4:
                if(notificacion == "Activado") {
                    notification("Continua caminando: " + distance + " metros por la acera podotactil y llegaras a las escaleras contiguas a la entrada" +
                            "del bloque de ingenieria");
                }
                break;
            case 5:
                if(notificacion == "Activado") {
                    notification("En :" + distance + " metros, estaras llegando a la entrada de la facultad de ingenieria");
                }
                break;
            case 6:
                if(notificacion == "Activado") {
                    notification(" Tu recorrido a terminado");
                }
                mostrador = "terminado";
                prefs.saveMostrador(mostrador);
                notification("Iniciando navegación interna");
                ((StartRouteActivity) Objects.requireNonNull(getActivity())).InDoorNotification();
                break;
            default:
                if(notificacion == "Activado") {
                    notification("otra excepcion");
                }
                break;
        }
        if(notificacion == "Activado") {
            notification2("Usted se encuentra a: " + distanceFinish + " metros del bloque de Ingeniería");
        }
    }
    private void creatRouteIngenieria(LatLng start){
        /*alertas = new Alerts();
        alertas.RunThread("COMPROBADO",requireContext(),requireActivity(),myPosition,mMap,start);
        */
        closerPoint(myPosition);
        Polyline Ruta2;
        int distance=0;
        int distanceFinish = (int)getDistance(myPosition,line.ingenieria);

        // switch close point
        switch (closePoint.name()){
            case "ingenieria":
                distance = (int) getDistance(start, closePoint.coor());
                Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.ingenieria,line.escaleras,line.biblioteca,line.medicina,line.porteria));
                if(prefs.getAlert() =="Activado") {
                    notification("Estas cerca de la puerta de la facultad de ingeniería");
                }stations = 0;
                break;
            case "ingenieria exacto":
                distance = (int) getDistance(myPosition, line.biblioteca);
                Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.escaleras,line.biblioteca,line.medicina,line.porteria));
                if(prefs.getAlert() =="Activado") {
                    notification("Haz llegado a la entrada del bloque de ingenieria");
                }
                stations = 1;
                break;
            case "escaleras":
                distance = (int) getDistance(myPosition, line.cajero);
                Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.escaleras,line.biblioteca,line.medicina,line.porteria));
                if(prefs.getAlert() =="Activado") {
                    notification("Estas cerca de las escaleras cercanas a la biblioteca CRAI");
                }
                stations = 1;
                break;
            case "escaleras exacto":
                distance = (int) getDistance(myPosition, line.biblioteca);

                Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.biblioteca,line.medicina,line.porteria));
                if(prefs.getAlert() =="Activado") {
                    notification("Haz llegado a las escaleras cercanas a la biblioteca CRAI");
                }
                stations = 2;
                break;

            case "biblioteca":
                if(prefs.getAlert() =="Activado"){
                notification("Esta cerca de la biblioteca");}
                distance=(int) getDistance(myPosition, line.biblioteca);
                stations=2;
                Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.biblioteca,line.medicina,line.porteria));
                break;
            case "biblioteca exacto":
                if(prefs.getAlert() =="Activado") {
                    notification("Estas muy cerca de la biblioteca");
                    notification("continua caminando por la acera podotactil para llegar a la facultad de medicina");
                }
                distance=(int) getDistance(myPosition, line.biblioteca);
                Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.medicina,line.porteria));
                stations =3;
                break;
            case "medicina":
                distance=(int)getDistance(myPosition,line.biblioteca);
                if(prefs.getAlert() =="Activado") {
                    notification("Te estas acercando a la facultad de medicina");
                }
                Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.medicina,line.porteria));
                stations=3;
                break;
            case "medicina exacto":
                distance=(int)getDistance(myPosition,line.escaleras);
                Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.porteria));
                if(prefs.getAlert() =="Activado") {
                    notification("Te encuentras muy cerca de la facultad de salud");
                }
                stations=4;
                break;
            case "cajero": distance=(int)getDistance(myPosition,line.escaleras);
                if(prefs.getAlert() =="Activado") {
                    notification("Pronto llegaras al cajero contigua a la entrada principal de la facultad de salud");
                }
                Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.porteria));
                stations=4;
                break;
            case "cajero exacto":
                distance=(int)getDistance(myPosition,line.ingenieria);
                if(prefs.getAlert() =="Activado") {
                    notification("Estas muy cerca a la entrada de la facultad de ciencias de la salud");
                }
                Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.porteria));
                stations=5;
                break;
            case "porteria":
                distance=(int)getDistance(myPosition,line.ingenieria);
                if(prefs.getAlert() =="Activado") {
                    notification("Estas llegando a la porteria 2 de la Universidad del Quindío");
                }
                Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.porteria));
                stations=5;
                break;
            case "porteria exacto":
                if(prefs.getAlert() =="Activado"){
                notification("Haz llegado a la porteria 2 de la Universidad" +
                        "del Quindío");}
                stations=6;
                break;
            default:
                distance = (int) getDistance(myPosition, line.porteria);
                stations = 8;
                break;
        }

        switch (stations){
            case 0:
                if (distance >= 30) {
                    if(prefs.getAlert() =="Activado"){
                    notification("Debes acercarte más a la entrada principal de la facultad de ingeniería" +
                            "para iniciar tu recorrido");}
                } else {
                    sayRoute(distance, closePoint);
                        notification("Recuerda tener cuidado ya que es un camino peatonal y vehicular");
                }
                break;
            case 1:
                notification("Camina " + distance + " metros por la acera podotactil y te encontraras con las escaleras cercanas al la" +
                        "biblioteca CRAI");
                break;
            case 2:
                notification("Estas a: " + distance + " metros  de la biblioteca CRAI");

                break;
            case 3:

                notification("Sigue caminando: "+distance+" metros por la acera podotactil y llegaras a la facultad de ciencias de la " +
                        "salud");
                break;
            case 4:

                notification("Continua caminando: "+distance+" metros por la acera podotactil y llegaras a la entrada " +
                        "principal de la facultad de ciencias de la salud");
                break;
            case 5:
                notification("En :" + distance + " metros, estaras llegando a la porteria 2 de la Universidad del Quindío");
                break;
            case 6:
                notification(" Tu recorrido a terminado");

                mostrador = "terminado";
                prefs.saveMostrador(mostrador);
                break;
            default:

                notification("otra excepcion");
                break;
        }
            notification2("Usted se encuentra a: " + distanceFinish + " metros de la porteria 2 de la Universidad del Quindío");

    }
    void notification(String cadena){
        ((StartRouteActivity) Objects.requireNonNull(getActivity())).shownotify(cadena);
    }
    void notification2(String cadena){
        ((StartRouteActivity) Objects.requireNonNull(getActivity())).shownotify2(cadena);
    }


}