package com.example.horusmap10;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.location.Location.*;
import static android.widget.Toast.*;
import static com.example.horusmap10.Horusmap1.Horusmap.prefs;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.support.v4.app.INotificationSideChannel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
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

import com.example.horusmap10.Horusmap1.Horusmap;
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

import java.text.DecimalFormat;
import java.util.Objects;

import kotlinx.coroutines.Delay;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.GlobalScope;

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
    public LatLng myPosition;
    public int stations = 0;
    private GoogleMap mMap;
    private final line marker = new line(mMap);
    private LocationManager location;
    private Alerts alertas;
    private Polyline Ruta1;
    int counter=0;
    int lastStation=7;
    private LatLng posAnterior =new LatLng(0,0);
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        public void onLocationChanged(Location location) {
            myPosition= new LatLng(location.getLatitude(),location.getLongitude());
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

            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(@NonNull Location location) {
                    mMap.clear();
                    myPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap = marker.addMarkers(mMap);
                    //mMap.addMarker(new MarkerOptions().position(myPosition));

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 18),100, null);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        choiseOption(myPosition);
                    }
                }
            });
/*
            LocationManager locationManager = (LocationManager) requireContext().getSystemService(LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                @SuppressLint("MissingPermission")
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onLocationChanged(Location location) {
                    // se accede a las opciones de ubicación
                   //Toast.makeText(requireContext(), "POSICIÓN: "+myPosition.latitude+", "+myPosition.longitude+"\nESTATION:"+stations, Toast.LENGTH_LONG).show();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            choiseOption(myPosition);
                        }
                    });

                }
            };

 */}


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

    public double getDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radio de la tierra en  kilómetros
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c*1000;
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
    /**
     * Metodo que encuentra la distancia entre dos coordenadas
     */

    private void getStation(LatLng myPosition){
        double longitude = myPosition.longitude;
        double latitude = myPosition.latitude;

        // ESTACIÓN PORTERIA
        if((myPosition.latitude>=4.556050)&&(myPosition.latitude<=4.556571)&&(myPosition.longitude>=-75.6584360)&&(myPosition.longitude<=-75.6582500)){
            stations =0;
        }
        //ESTACIÓN CAJERO
        if((myPosition.latitude>=4.556174)&&(myPosition.latitude<=4.55647)&&(myPosition.longitude>=-75.6587100)&&(myPosition.longitude<=-75.6584360)){
            stations =1;
        }
        //ESTACIÓN MEDICINA
        if((myPosition.latitude>=4.556174)&&(myPosition.latitude<=4.5565700)&&(myPosition.longitude>=-75.6590400)&&(myPosition.longitude<=-75.6587100)){
            stations =2;
        }
        //ESTACIÓN BIBLIOTECA
        if((myPosition.latitude>=4.556174)&&(myPosition.latitude<=4.5565700)&&(myPosition.longitude>=-75.659639)&&(myPosition.longitude<=-75.6590400)){
            stations =3;
        }
        //ESTACIÓN ESCALERAS
        if((myPosition.latitude>=4.556174)&&(myPosition.latitude<=4.556700)&&(myPosition.longitude>=-75.6599500)&&(myPosition.longitude<=-75.659637)){
            stations =4;
        }
        //ESTACIÓN INTENIERIA
        if((myPosition.latitude>=4.5560932)&&(myPosition.latitude<=4.556700)&&(myPosition.longitude>=-75.6603760)&&(myPosition.longitude<=-75.6599500)){
            stations =5;
        }
        //ESTACIÓN INTERNA
        if((myPosition.latitude>=4.555584)&&(myPosition.latitude<=4.5560932)&&(myPosition.longitude>=-75.660376)&&(myPosition.longitude<=-75.659985)){
            stations =6;
        }
    }
    /**
     * Calculo de el punto más cercano al punto dado
     */
    private void closerPoint(LatLng start) {
        double[] distance = new double[7];
        distance[0] =  getDistance(start, line.porteria);
        distance[1] =  getDistance(start, line.cajero);
        distance[2] =  getDistance(start, line.medicina);
        distance[3] =  getDistance(start, line.capilla);
        distance[4] =  getDistance(start, line.biblioteca);
        distance[5] =  getDistance(start, line.escaleras);
        distance[6] =  getDistance(start, line.ingenieria);
        Double min = distance[0];
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
                //SECTOR PORTERIA
                    if (distance[pos] <= 8) {
                        closePoint = new Point(line.porteria, "porteria exacto");
                    } else {
                        closePoint = new Point(line.porteria, "porteria");
                    }
                break;
            case line.CAJERO:
                //SECTOR CAJERO
                    if (distance[pos] <= 8) {
                        closePoint = new Point(line.cajero, "cajero exacto");
                    } else {
                        closePoint = new Point(line.cajero, "cajero");
                    }
                break;
            case line.MEDICINA:
                //SECTOR MEDICINA BIBLIOTECA
                    if (distance[pos] <= 8) {
                        closePoint = new Point(line.medicina, "medicina exacto");
                    } else {
                        closePoint = new Point(line.medicina, "medicina");
                    }
                break;
            case line.CAPILLA:
                // SECTOR MEDICINA BIBLIOTECA
                    if (distance[pos] <= 3) {
                        closePoint = new Point(line.capilla, "capilla exacto");
                    } else {
                        closePoint = new Point(line.capilla, "biblioteca");
                    }
                break;
            case line.BIBLIOTECA:
                //SECTOR MEDICINA BIBLIOTECA
                    if (distance[pos] <= 8) {
                        closePoint = new Point(line.biblioteca, "biblioteca exacto");
                    } else {
                        closePoint = new Point(line.biblioteca, "biblioteca");
                    }
                break;
            case line.ESCALERAS:
                //SECTOR ESCALERAS
                    if (distance[pos] <= 8) {
                        closePoint = new Point(line.escaleras, "escaleras exacto");
                    } else {
                        closePoint = new Point(line.escaleras, "escaleras");
                    }
                break;
            case line.INGENIERIA:
                //SECTOR INGENIERIA AFUERAS
                    if (distance[pos] <= 8) {
                        closePoint = new Point(line.ingenieria, "ingenieria exacto");
                    } else {
                        closePoint = new Point(line.ingenieria, "ingenieria");
                    }
                break;
            default:
                //SECTOR DENTRO
                    LatLng nulo = new LatLng(0, 0);
                    stations=7;
                    closePoint = new Point(nulo, "estás demasiado lejos");
                break;
        }
      }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void choiseOption(LatLng myPosition){
        String opcion=((StartRouteActivity) Objects.requireNonNull(getActivity())).rutas();
        mostrador = prefs.getMostrador();
        notificacion = prefs.getAlert();
        sounds  = prefs.getSounds();
        if ((_binding.options.getText().toString().equals("Porteria a Facultad de ingenieria"))||(opcion=="porteria")){

            if(mostrador != "porteria") {

                        if(notificacion != "Desactivado") {
                            ((StartRouteActivity) Objects.requireNonNull(getActivity())).toteitor("Usted ha seleccionado la ruta porteria a Facultad de ingeniería");

                        }
                        if(sounds != "Dectivado"&&(mostrador!="porteria")){
                            playsound("start");
                        }
                mostrador = "porteria";
                prefs.saveMostrador(mostrador);
                    }

            creatRoutePorteria(myPosition);
        }
        if ((_binding.options.getText().toString().equals("Facultad de ingenieria a porteria"))||(opcion=="ingenieria")){
            if (mostrador != "ingenieria") {
                if(prefs.getAlert()!="Desativado"){
                    ((StartRouteActivity) Objects.requireNonNull(getActivity())).toteitor("Has seleccionado la ruta: " +
                    "Facultad de ingeniería a Porteria");
                }
                if(sounds != "Dectivado"&&(mostrador!="ingenieria")){
                    playsound("start");
                }
                mostrador = "ingenieria";
                prefs.saveMostrador(mostrador);
            }

            creatRouteIngenieria(myPosition);

        }
        if (_binding.options.getText().toString().equals("Navegación Interna")){
            ((StartRouteActivity) Objects.requireNonNull(getActivity())).toteitor("Iniciando navegación interior");
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

        closerPoint(myPosition);
        Polyline Ruta2;
        int distance=0;
        int distanceFinish = 0;
        String aviso="";
        getStation(myPosition);
        switch (closePoint.name()) {
            case "porteria":

                if(stations==0) {
                    Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.porteria, line.cajero,
                            line.medicina,line.maria, line.biblioteca, line.escaleras, line.ingenieria));
                    distanceFinish=(int)(getDistance(myPosition,line.porteria)+getDistance(line.porteria,line.cajero)+ getDistance(line.cajero,line.medicina)+
                            getDistance(line.medicina,line.maria)+getDistance(line.maria,line.biblioteca)+getDistance(line.biblioteca,line.escaleras)+
                            getDistance(line.escaleras,line.ingenieria));
                    aviso = aviso + "Estás a: "+((int)(getDistance(myPosition,line.porteria)))+ "metros de la portería\n";
                }else{
                    Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.cajero,
                            line.medicina,line.maria, line.biblioteca, line.escaleras, line.ingenieria));
                    distanceFinish=(int)(getDistance(myPosition,line.cajero)+ getDistance(line.cajero,line.medicina)+
                            getDistance(line.medicina,line.maria)+getDistance(line.maria,line.biblioteca)+ getDistance(line.biblioteca,line.escaleras)+
                            getDistance(line.escaleras,line.ingenieria));
                    aviso = aviso + "Estás pasando la portería\n";
                }

                break;
            case "porteria exacto":

                if(stations==0) {
                    Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.porteria, line.cajero,
                            line.medicina,line.maria, line.biblioteca, line.escaleras, line.ingenieria));
                    distanceFinish=(int)(getDistance(myPosition,line.porteria)+getDistance(line.porteria,line.cajero)+ getDistance(line.cajero,line.medicina)+
                            getDistance(line.medicina,line.maria)+getDistance(line.maria,line.biblioteca)+ getDistance(line.biblioteca,line.escaleras)+
                            getDistance(line.escaleras,line.ingenieria));
                    aviso = aviso + "Haz llegado a la porteria\n";
                }else{
                    Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.cajero,
                            line.medicina,line.maria, line.biblioteca, line.escaleras, line.ingenieria));
                    distanceFinish=(int)(getDistance(myPosition,line.cajero)+ getDistance(line.cajero,line.medicina)+
                            getDistance(line.medicina,line.maria)+getDistance(line.maria,line.biblioteca)+ getDistance(line.biblioteca,line.escaleras)+
                            getDistance(line.escaleras,line.ingenieria));
                    aviso = aviso + "Estás en la porteria\n";
                }

                break;
            case "cajero":

                if(stations==1) {
                    Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.cajero,
                            line.medicina,line.maria, line.biblioteca, line.escaleras, line.ingenieria));
                    distanceFinish=(int)(getDistance(myPosition,line.cajero)+ getDistance(line.cajero,line.medicina)+
                            getDistance(line.medicina,line.maria)+getDistance(line.maria,line.biblioteca)+ getDistance(line.biblioteca,line.escaleras)+
                            getDistance(line.escaleras,line.ingenieria));
                    aviso = aviso + "Estás cerca a la entrada de la facultad de salud\n";
                }else{
                    Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.medicina, line.biblioteca,
                            line.escaleras, line.ingenieria));
                    distanceFinish=(int)( getDistance(myPosition,line.medicina)+ getDistance(line.medicina,line.maria)+getDistance(line.maria,line.biblioteca)+
                            getDistance(line.biblioteca,line.escaleras)+ getDistance(line.escaleras,line.ingenieria));
                    aviso = aviso + "Estás pasando la facultad de salud\n";
                }

                break;
            case "cajero exacto":

                if(stations==1) {
                    Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.cajero,
                            line.medicina,line.maria, line.biblioteca, line.escaleras, line.ingenieria));
                    distanceFinish=(int)(getDistance(myPosition,line.cajero)+ getDistance(line.cajero,line.medicina)+
                            getDistance(line.medicina,line.maria)+getDistance(line.maria,line.biblioteca)+getDistance(line.biblioteca,line.escaleras)+
                            getDistance(line.escaleras,line.ingenieria));
                    aviso = aviso + "Estás justo enfrente de la entrada de salud\n";
                }else{
                    Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.medicina,line.maria, line.biblioteca,
                            line.escaleras, line.ingenieria));
                    distanceFinish=(int)(getDistance(myPosition,line.medicina)+ getDistance(line.medicina,line.maria)+
                            getDistance(line.maria,line.biblioteca)+getDistance(line.biblioteca,line.escaleras)+
                            getDistance(line.escaleras,line.ingenieria));
                    aviso = aviso + "Estás justo enfrente de la entrada de salud camino a la biblioteca\n";
                }

                break;
            case "medicina":

                if(stations==2) {
                    Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.medicina,line.maria, line.biblioteca,
                            line.escaleras, line.ingenieria));
                    distanceFinish=(int)(getDistance(myPosition,line.medicina)+
                            getDistance(line.medicina,line.maria)+getDistance(line.maria,line.biblioteca)+ getDistance(line.biblioteca,line.escaleras)+
                            getDistance(line.escaleras,line.ingenieria));
                    aviso = aviso + "Te estás moviendo cerca a la facultad de ciencias de la salud\n";
                }else{
                    Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.biblioteca,
                            line.escaleras, line.ingenieria));
                    distanceFinish=(int)(getDistance(myPosition,line.biblioteca)+ getDistance(line.biblioteca,line.escaleras)+
                            getDistance(line.escaleras,line.ingenieria));
                    aviso = aviso + "Estás acercandote a la biblioteca\n";
                }break;
            case "medicina exacto":
                aviso = aviso + "Estás caminando de la facultad de medicina a la biblioteca\n";
                if(stations==2) {
                    Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.medicina, line.biblioteca,
                            line.escaleras, line.ingenieria));
                    distanceFinish=(int)(getDistance(myPosition,line.medicina)+
                            getDistance(line.medicina,line.maria)+getDistance(line.maria,line.biblioteca)+ getDistance(line.biblioteca,line.escaleras)+
                            getDistance(line.escaleras,line.ingenieria));
                }else{
                    Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.biblioteca,
                            line.escaleras, line.ingenieria));
                    distanceFinish=(int)(getDistance(myPosition,line.biblioteca)+ getDistance(line.biblioteca,line.escaleras)+
                            getDistance(line.escaleras,line.ingenieria));
                }break;
            case "biblioteca":


                if(stations==3) {
                    Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.biblioteca,
                            line.escaleras, line.ingenieria));
                    distanceFinish=(int)(getDistance(myPosition,line.biblioteca)+ getDistance(line.biblioteca,line.escaleras)+
                            getDistance(line.escaleras,line.ingenieria));
                    aviso = aviso + "Estás llegando a la entrada de la biblioteca CRAI\n";
                }else{
                    Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.escaleras, line.ingenieria));
                    distanceFinish=(int)(getDistance(myPosition,line.escaleras)+ getDistance(line.escaleras,line.ingenieria));
                    aviso = aviso + "Estás pasando la biblioteca\n";
                } break;
            case "biblioteca exacto":
                aviso = aviso + "Estás a pocos metros de la entrada de la biblioteca\n";
                if(stations==3) {
                    distanceFinish=(int)(getDistance(myPosition,line.biblioteca)+ getDistance(line.biblioteca,line.escaleras)+
                            getDistance(line.escaleras,line.ingenieria));
                }else{
                    Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.escaleras, line.ingenieria));
                    distanceFinish=(int)(getDistance(myPosition,line.escaleras)+ getDistance(line.escaleras,line.ingenieria));
                }break;
            case "escaleras":


                if(stations==4) {
                    Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.escaleras, line.ingenieria));
                    distanceFinish=(int)( getDistance(myPosition,line.escaleras)+getDistance(line.escaleras,line.ingenieria));
                    aviso = aviso + "Estás llegando a las escaleras cercanas a ingeniería\n";
                }else{
                    Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition,  line.ingenieria));
                    distanceFinish=(int)(getDistance(myPosition,line.ingenieria));
                    aviso = aviso + "Estás llegando a la ingeniería, te cuidado con las escaleras y la bajada \n";
                }break;
            case "escaleras exacto":

                aviso = aviso + "Estás cerca a las escaleras cercanas a ingeniería\n";
                if(stations==4) {
                    Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.escaleras, line.ingenieria));
                    distanceFinish=(int)( getDistance(myPosition,line.escaleras)+ getDistance(line.escaleras,line.ingenieria));
                }else{
                    Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition,  line.ingenieria));
                    distanceFinish=(int)(getDistance(myPosition,line.ingenieria));
                }break;
            case "ingenieria":

                aviso = aviso + "Estás cerca de la facultad de ingeniería\n";
                if(stations==5) {
                    Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.ingenieria));
                    distanceFinish=(int)(getDistance(myPosition,line.ingenieria));
                }
                break;
            case "ingenieria exacto":
                aviso = aviso + "Estás entrando a la facultad de ingeniería\n";
                break;
            default:
                distanceFinish= (int)(getDistance(myPosition,line.porteria));
                break;
        }

        switch (stations){
            case 0:
                if (getDistance(myPosition,line.porteria) >= 80) {
                       aviso=aviso+"Debes acercarte más a la porteria" +
                                "para iniciar tu recorrido\n";
                } else {
                       aviso=aviso+"Ten cuidado ya que es una entrada vehicular y peatonal\n";
                }
                break;
            case 1:
                    aviso=aviso+"Camina " + ((int)(getDistance(myPosition,line.cajero))) + " metros y te encontraras con el cajero davivienda, de la entrada de salud\n";
                break;
            case 2:
                    aviso=aviso+"Estás a: " + ((int)(getDistance(myPosition,line.maria)+getDistance(line.maria,line.biblioteca)))  + " metros  de la biblioteca\n";
                break;
            case 3:
                    aviso=aviso+"Sigue caminando: " + ((int)(getDistance(myPosition,line.biblioteca)))  + " metros y llegaras a la biblioteca\n";
                break;
            case 4:
                    aviso=aviso+"Camina: " + ((int)(getDistance(myPosition,line.escaleras))) + " metros  y llegaras a las escaleras cercanas a" +
                             "ingenieria y biblioteca\n";
                break;
            case 5:
                    aviso=aviso+"En :" + ((int)(getDistance(myPosition,line.ingenieria))) + " metros, estaras llegando a la entrada de la facultad de ingenieria\n";
                break;
            case 6:
                aviso=aviso+" Estás en la facultad de ingenieria, espera un momento para iniciar la navegación interior\n";
                mostrador = "terminado";
                prefs.saveMostrador(mostrador);
                ((StartRouteActivity) Objects.requireNonNull(getActivity())).toteitor("Iniciando navegación interior");
                ((StartRouteActivity) Objects.requireNonNull(getActivity())).InDoorNotification();
                break;
            default:
                    aviso=aviso+"Usted esta fuera de la Universidad del Quindío\n";
                break;
        }
        if((distanceFinish>=0)&&(getDistance(posAnterior,myPosition)<=10)) {
            aviso = aviso + " Usted se encuentra a: " +   distanceFinish + " metros de Ingeniería\n";

        }
        if(notificacion != "Desactivado") {
            counter++;
            if ((lastStation != stations)&&counter==1) {
                //((StartRouteActivity) Objects.requireNonNull(getActivity())).toster(aviso);
                if(counter==1) {
                    //Toast.makeText(requireContext(), aviso, LENGTH_LONG).show();
                    ((StartRouteActivity) Objects.requireNonNull(getActivity())).toteitor(aviso);
                }else if (counter>=2){
                    counter=0;
                }
                //notification(aviso);
            } else if (lastStation == stations) {

                if(counter==1){
                    //Toast.makeText(requireContext(), aviso, LENGTH_LONG).show();
                    ((StartRouteActivity) Objects.requireNonNull(getActivity())).toteitor(aviso);
                }else if(counter>=2){
                    counter=0;
                }
            }
        }
        aviso="";
        posAnterior =myPosition;
        lastStation =stations;

    }
    private void creatRouteIngenieria(LatLng start){
        /*alertas = new Alerts();
        alertas.RunThread("COMPROBADO",requireContext(),requireActivity(),myPosition,mMap,start);
        */
        closerPoint(myPosition);
        Polyline Ruta2;
        int distance=0;
        int distanceFinish = 0;
        String aviso="";
        getStation(myPosition);
        // switch close point
        switch (closePoint.name()){
            case "ingenieria":

                if(stations==6) {
                    Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.ingenieria, line.escaleras,line.biblioteca,line.maria, line.medicina, line.cajero, line.porteria));
                    distanceFinish=(int)(getDistance(myPosition,line.ingenieria)+getDistance(line.porteria,line.cajero)+ getDistance(line.cajero,line.medicina)+
                            getDistance(line.medicina,line.maria)+getDistance(line.maria,line.biblioteca)+ getDistance(line.biblioteca,line.escaleras)+
                            getDistance(line.escaleras,line.ingenieria));
                    aviso = aviso + "Acercate a la puerta de ingeniería\n";
                }else{
                    Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.escaleras, line.biblioteca,line.maria, line.medicina, line.cajero, line.porteria));
                    distanceFinish=(int)(getDistance(line.porteria,line.cajero)+ getDistance(line.cajero,line.medicina)+
                            getDistance(line.medicina,line.maria)+getDistance(line.maria,line.biblioteca)+ getDistance(line.biblioteca,line.escaleras)+
                            getDistance(myPosition,line.escaleras));
                    aviso = aviso + "Ten cuidado con las escaleras saliendo de ingeniería\n";
                }

                break;
            case "ingenieria exacto":

                if(stations==6) {
                    Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.ingenieria, line.escaleras, line.biblioteca,line.maria, line.medicina, line.cajero, line.porteria));
                    aviso= aviso+"Usted esta cerca a la entrada del bloque de ingeniería\n";
                }else{
                    Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.escaleras, line.biblioteca,line.maria, line.medicina, line.cajero, line.porteria));
                    distanceFinish=(int)(getDistance(line.porteria,line.cajero)+ getDistance(line.cajero,line.medicina)+
                            getDistance(line.medicina,line.maria)+getDistance(line.maria,line.biblioteca)+ getDistance(line.biblioteca,line.escaleras)+
                            getDistance(myPosition,line.escaleras));
                    aviso= aviso+"Estás a la biblioteca";
                }
                break;
            case "escaleras":
                if(stations==5) {
                    Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.escaleras, line.biblioteca,line.maria, line.medicina, line.cajero, line.porteria));
                    distanceFinish=(int)(getDistance(line.porteria,line.cajero)+ getDistance(line.cajero,line.medicina)+
                            getDistance(line.medicina,line.maria)+getDistance(line.maria,line.biblioteca)+ getDistance(line.biblioteca,line.escaleras)+
                            getDistance(line.escaleras,myPosition));
                    aviso= aviso+"Estás cerca de las escaleras a la biblioteca CRAI\n";
                }else{
                    Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.biblioteca,line.maria, line.medicina, line.cajero, line.porteria));
                    distanceFinish=(int)(getDistance(line.porteria,line.cajero)+ getDistance(line.cajero,line.medicina)+
                            getDistance(line.medicina,line.maria)+getDistance(line.maria,line.biblioteca)+ getDistance(line.biblioteca,myPosition));
                    aviso= aviso+"Sigue caminando para llegar a la biblioteca";
                }

                break;
            case "escaleras exacto":
                if(stations==5) {
                    Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.escaleras, line.biblioteca,line.maria, line.medicina, line.cajero, line.porteria));
                    distanceFinish=(int)(getDistance(line.porteria,line.cajero)+ getDistance(line.cajero,line.medicina)+
                            getDistance(line.medicina,line.maria)+getDistance(line.maria,line.biblioteca)+ getDistance(line.biblioteca,line.escaleras)+
                            getDistance(line.escaleras,myPosition));
                }else{
                    Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.biblioteca,line.maria, line.medicina, line.cajero, line.porteria));
                    distanceFinish=(int)(getDistance(line.porteria,line.cajero)+ getDistance(line.cajero,line.medicina)+
                            getDistance(line.medicina,line.maria)+getDistance(line.maria,line.biblioteca)+ getDistance(line.biblioteca,myPosition));
                }
                aviso=aviso+"Haz llegado a las escaleras cercanas a la biblioteca CRAI\n";
                break;

            case "biblioteca":

                if(stations==4) {
                    Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,  line.biblioteca,line.maria, line.medicina, line.cajero, line.porteria));
                    distanceFinish=(int)(getDistance(line.porteria,line.cajero)+ getDistance(line.cajero,line.medicina)+
                            getDistance(line.medicina,line.maria)+getDistance(line.maria,line.biblioteca)+ getDistance(line.biblioteca,myPosition));
                    aviso=aviso+"Estás cerca de la biblioteca\n";
                }else{
                    Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,  line.medicina, line.cajero, line.porteria));
                    distanceFinish=(int)(getDistance(line.porteria,line.cajero)+ getDistance(line.cajero,line.medicina)+ getDistance(line.medicina,myPosition));
                    aviso=aviso+"Estás pasando la biblioteca\n";
                }break;
            case "biblioteca exacto":

                if(stations==4) {
                    Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,  line.biblioteca,line.maria, line.medicina, line.cajero, line.porteria));
                    distanceFinish=(int)(getDistance(line.porteria,line.cajero)+ getDistance(line.cajero,line.medicina)+
                            getDistance(line.medicina,line.maria)+getDistance(line.maria,line.biblioteca)+ getDistance(line.biblioteca,myPosition));

                }else{
                    Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,  line.medicina, line.cajero, line.porteria));
                    distanceFinish=(int)(getDistance(line.porteria,line.cajero)+ getDistance(line.cajero,line.medicina)+ getDistance(line.medicina,myPosition));
                }
                aviso=aviso+"Estás cerca de la entrada de biblioteca \n" ;
                break;
            case "medicina":
                if(stations==3) {
                    Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.medicina, line.cajero, line.porteria));
                    distanceFinish=(int)(getDistance(line.porteria,line.cajero)+ getDistance(line.cajero,line.medicina)+
                            getDistance(myPosition,line.medicina));
                    aviso=aviso+"Te estás acercando a la facultad de salud\n";
                }else{
                    Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.cajero, line.porteria));
                    distanceFinish=(int)(getDistance(line.porteria,line.cajero)+ getDistance(line.cajero,myPosition));
                    aviso=aviso+"Te estás pasando por la facultad de salud\n";
                }break;
            case "medicina exacto":
                if(stations==3) {
                    Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.medicina, line.cajero, line.porteria));
                    distanceFinish=(int)(getDistance(line.porteria,line.cajero)+ getDistance(line.cajero,line.medicina)+
                            getDistance(myPosition,line.medicina));
                }else{
                    Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.cajero, line.porteria));
                    distanceFinish=(int)(getDistance(line.porteria,line.cajero)+ getDistance(line.cajero,myPosition));
                }
                aviso=aviso+"Te encuentras muy cerca de la facultad de salud\n";
                break;
            case "cajero":

                if(stations==2) {
                    Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.cajero, line.porteria));
                    distanceFinish=(int)(getDistance(line.porteria,line.cajero)+ getDistance(myPosition,line.cajero));
                    aviso=aviso+"Estás a unos metros del cajero de la entrada de salud\n";
                }else{
                    Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,  line.porteria));
                    distanceFinish=(int)(getDistance(line.porteria,myPosition));
                    aviso=aviso+"Estás pasando la entrada de salud\n";
                }break;
            case "cajero exacto":
                aviso=aviso+"Estás muy cerca del cajero davivienda y la entrada de salud\n";
                if(stations==2) {
                    Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.cajero, line.porteria));
                    distanceFinish=(int)(getDistance(line.porteria,line.cajero)+ getDistance(myPosition,line.cajero));
                }else{
                    Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,  line.porteria));
                    distanceFinish=(int)(getDistance(line.porteria,myPosition));
                }break;
            case "porteria":
                if(stations==1) {
                    Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.porteria));
                    aviso=aviso+"Estás llegando a la porteria\n";
                    distanceFinish=(int)(getDistance(myPosition,line.porteria));
                }else{
                    distanceFinish =0;
                    aviso=aviso+"Estás cerca a la porteria y saliste de la Universidad\n";
                }
                break;
            case "porteria exacto":
                if(stations==1) {
                    Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition, line.porteria));
                    distanceFinish=(int)(getDistance(myPosition,line.porteria));
                }else{
                    distanceFinish =0;
                }
                 aviso=aviso+"Haz llegado a la porteria\n";
                break;
            default:
                break;
        }

        switch (stations){
            case 6:
                if (getDistance(myPosition,line.ingenieria) >= 100) {
                    if(prefs.getAlert() =="Activado"){
                        aviso=aviso+"Acercarte a la entrada de la facultad de ingeniería" +
                                        "para iniciar tu recorrido\n";
                    }

                } else {
                    aviso=aviso+"Ten cuidado ya en pocos metros hay escaleras\n";
                }
                break;
            case 5:
                aviso=aviso+"Camina "+((int)(getDistance(myPosition,line.escaleras)))+" metros para finalizar las escaleras cercanas a la biblioteca \n";
                break;
            case 4:
                aviso=aviso+"Estás a: "+((int)(getDistance(myPosition,line.biblioteca)))+ " metros  de la biblioteca CRAI\n";
                break;
            case 3:
                aviso=aviso+"Sigue  caminando "+((int)(getDistance(myPosition,line.medicina)))+" metros y llegarás a la facultad de salud\n";
                break;
            case 2:

                aviso=aviso+"Continua caminando "+((int)(getDistance(myPosition,line.cajero)))+" metros  y llegarás a entrada de la facultad de salud\n";
                break;
            case 1:
                aviso=aviso+" Estás a "+((int)(getDistance(myPosition,line.porteria)))+" metros de la porteria\n";
                break;
            case 0:
                aviso=aviso+" Tu recorrido a terminado, puedes cerrar la horusmap\n";
                mostrador = "terminado";
                prefs.saveMostrador(mostrador);
                break;
            default:
                aviso=aviso+"Debes acercarte al campus de la Universidad del Quindío \n";
                break;
        }
        if((distanceFinish>=6)&&(getDistance(posAnterior,myPosition)<=10)) {
            aviso = aviso + "Usted se encuentra a: " + (distanceFinish-5) + " metros de la Porteria";
        }
        if(notificacion != "Desactivado") {
            counter++;
            if ((lastStation != stations)&&counter==1) {
                //((StartRouteActivity) Objects.requireNonNull(getActivity())).toster(aviso);
                if(counter==1) {
                    //Toast.makeText(requireContext(), aviso, LENGTH_LONG).show();
                    ((StartRouteActivity) Objects.requireNonNull(getActivity())).toteitor(aviso);
                }else if (counter>=2){
                    counter=0;
                }
                //notification(aviso);
            } else if (lastStation == stations) {

                if(counter==1){
                    //Toast.makeText(requireContext(), aviso, LENGTH_LONG).show();
                    ((StartRouteActivity) Objects.requireNonNull(getActivity())).toteitor(aviso);
                }else if(counter>=2){
                    counter=0;
                }
            }
        }
        aviso="";
        posAnterior = myPosition;
        lastStation =stations;


    }

    void notification(String cadena){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((StartRouteActivity) Objects.requireNonNull(getActivity())).shownotify(cadena);
            }
        });

    }




}