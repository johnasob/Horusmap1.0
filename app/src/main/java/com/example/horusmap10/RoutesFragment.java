package com.example.horusmap10;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.google.android.libraries.places.api.Places;

import java.math.MathContext;


public class RoutesFragment extends Fragment implements LocationListener, AdapterView.OnItemSelectedListener {

    View vista;
    Activity actividad;
    Context context;
    int mostrador = 0;
    private FragmentRoutesBinding _binding;
    private String ruta_selected;
    private AutoCompleteTextView spinner;
    private static final int COLOR_BLACK_ARGB = 0xFF2E512D;
    private static final int PORTERIA = 0;
    private static final int CAJERO = 1;
    private static final int MEDICINA = 2;
    private static final int CAPILLA = 3;
    private static final int BIBLIOTECA = 4;
    private static final int ESCALERAS = 5;
    private static final int INGENIERIA = 6;
    private Point closePoint;
    private LatLng finishPoint;
    private final LatLng ingenieria = new LatLng(4.5560932, -75.6601713);
    private final LatLng porteria = new LatLng(4.556286, -75.658436);
    private final LatLng biblioteca = new LatLng(4.5565694, -75.6596335);
    private final LatLng medicina = new LatLng(4.55646, -75.65904);
    private final LatLng cajero = new LatLng(4.55638, -75.65871);
    private final LatLng capilla = new LatLng(4.55642, -75.65933);
    private final LatLng escaleras = new LatLng(4.55630, -75.65995);
    private final LatLng maria = new LatLng(4.5564872, -75.6593398);
    public LatLng myPosition = new LatLng(4.5563, -75.6584);
    public int stations = 0;
    private GoogleMap mMap;
    private line marker = new line(mMap);


    public void onLocationChanged(Location location) {
        //position = new LatLng(location.getLatitude(), location.getLongitude());
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {

            getLocationPermision();
            mMap = googleMap;
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
            android.location.LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    mMap.clear();
                    //position = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
                    mMap.addMarker(new MarkerOptions().position(myPosition).title("mi posición"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition,20),1500,null);
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    choiseOption();
                    //closerPoint(myPosition);
                }
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    };
    private void getLocationPermision() {
        int permiso = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        if(permiso == PackageManager.PERMISSION_DENIED){
            if(ActivityCompat.shouldShowRequestPermissionRationale((Activity) requireContext(),Manifest.permission.ACCESS_FINE_LOCATION)){
            }else{
                ActivityCompat.requestPermissions((Activity) requireContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
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
        _binding =FragmentRoutesBinding.inflate(getLayoutInflater());
        vista =  _binding.getRoot();
        Places.initialize(requireContext(),"AIzaSyDDkCY1iJggF0pzjtRnYN41TQSejTh3XQE");
        spinner = _binding.options;
        spinner.setOnItemSelectedListener(this);
        String[] route = vista.getResources().getStringArray(R.array.routes);
        ArrayAdapter adapter = new ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,route);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        this.mMap = mMap;
        this.context =  context;
        return vista;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Asks for location service
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        //Listener for button
        try {
            // Request location updates from network and gps
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, this);
        } catch(SecurityException ex) {
            Log.d("myTag", "Security Exception, no location available");
        }
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

    /**Metodo que muestra los marcadores y el camino predeterminado de la porteria 2 a ingeniería*/
    private void showmarkers(GoogleMap googleMap,LatLng myLocation){
        // Crea la ruta de navegación inicial
        Polyline Ruta1 = googleMap.addPolyline(marker.lineRoute(myLocation,stations));
        // Cambia el color de la ruta a negro
        Ruta1.setColor(COLOR_BLACK_ARGB);
        // Agrega los marcadores de los punto estrategicos de la ruta porteria-ingeniería
        googleMap = marker.addMarkers(googleMap);
    }



    private void creatRoutePorteria(LatLng start){

        //¿Donde estoy?
        //myPosition = porteria;
        closerPoint(myPosition);
        int distance=0;
        switch (closePoint.name()){
           case "porteria":
               distance = (int) getDistance(start, closePoint.coor());
               if(mostrador == 0) {
                   Toast.makeText(requireContext(), "Estas cerca de " + closePoint.name(), Toast.LENGTH_LONG).show();
                   mostrador =1;
               }
               stations = 0;
               break;
           case "porteria exacto":
               distance = (int) getDistance(myPosition, cajero);
               if((mostrador == 0)||(mostrador ==2)) {
                   Toast.makeText(requireContext(), "Haz llegado a la porteria 2 de la Universidad del Quindío", Toast.LENGTH_LONG).show();
                   mostrador =3;
               }
               stations = 1;
               break;
           case "cajero":
               distance = (int) getDistance(myPosition, cajero);
                   Toast.makeText(requireContext(), "Estas cerca de " + closePoint.name(), Toast.LENGTH_LONG).show();
                   mostrador = 3;
               stations = 1;
               break;
           case "cajero exacto":
               distance = (int) getDistance(myPosition, biblioteca);
               stations = 2;
               break;
               default:
                   distance = (int) getDistance(myPosition, porteria);
                   stations = 8;
                   break;
       }
        switch (stations){
            case 0:
                showmarkers(mMap,myPosition);
                if (distance >= 30) {
                    if(distance == 1) {
                        Toast.makeText(requireContext(), "Debes acercarte más a la porteria 2 de la Universidad del Quindío" +
                                "para iniciar tu recorrido", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if(distance == 1) {
                        sayRoute(distance, closePoint);
                        Toast.makeText(requireContext(), "Recuerda tener cuidado ya que es una entrada vehicular y peatonal", Toast.LENGTH_LONG).show();
                        mostrador =2;
                    }
                }
                break;
            case 1:
                //mMap.clear();
                showmarkers(mMap,myPosition);
                if (mostrador == 3) {
                    Toast.makeText(requireContext(), "Camina " + distance + " metros por la acera podotactil y te encontraras con el cajero davivienda", Toast.LENGTH_SHORT).show();
                }
        }



    }
    private void sayRoute(int distance, Point closePoint){
        Toast.makeText(requireContext(), "Camina  "+distance+" metros y te encontraras con "+closePoint.name(), Toast.LENGTH_SHORT).show();
    }
    private  void navegation(){

    }
    private void searchProblems(){

    }
    private void closeTofar(LatLng start, LatLng finishPoint){

    }
    /**Metodo que encuentra la distancia entre dos coordenadas*/
    private double getDistance(LatLng start, LatLng finish){
        //Inicializa el locationrequest que se usa para encontrar la distancia
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        float[] distances = new float[1];
        //Calcula la distancia
        Location.distanceBetween(start.latitude,start.longitude,finish.latitude,finish.longitude,distances);
        return distances[0];
    }

    /**Calculo de el punto más cercano al punto dado*/
    private void closerPoint(LatLng start) {
        double[] distance = new double[7];
        distance[0] = getDistance(start, porteria);
        distance[1] = getDistance(start, cajero);
        distance[2] = getDistance(start, medicina);
        distance[3] = getDistance(start, capilla);
        distance[4] = getDistance(start, biblioteca);
        distance[5] = getDistance(start, escaleras);
        distance[6] = getDistance(start, ingenieria);
        double min = distance[0];
        int pos = 0;

        for (int i = 0; i < distance.length-1; i++) {
            if (distance[i] <= min) {
                min = distance[i];
                pos = i;
            }
        }
        if(distance[pos] >= 80){
            pos = 7;
        }
        switch (pos){
            case PORTERIA:
                if(distance[pos] <=3){
                    closePoint = new Point(porteria, "porteria exacto");
                }else {
                    closePoint = new Point(porteria, "porteria");
                }
                break;
            case CAJERO:
                if(distance[pos] <=3){
                    closePoint = new Point(cajero, "cajero exacto");
                }else {
                    closePoint = new Point(cajero, "cajero");
                }
                break;
            case MEDICINA:
                if(distance[pos] <=3){
                    closePoint = new Point(medicina, "medicina exacto");
                }else {
                    closePoint = new Point(medicina, "medicina");
                }
                break;
            case CAPILLA:
                if(distance[pos] <=3){
                    closePoint = new Point(capilla, "capilla exacto");
                }else {
                    closePoint = new Point(capilla, "capilla");
                }
                break;
            case BIBLIOTECA:
                if(distance[pos] <=3){
                    closePoint = new Point(biblioteca, "biblioteca exacto");
                }else {
                    closePoint = new Point(biblioteca, "biblioteca");
                }
                break;
            case ESCALERAS:
                if(distance[pos] <=3){
                    closePoint = new Point(escaleras, "escaleras exacto");
                }else {
                    closePoint = new Point(escaleras, "escaleras");
                }
                break;
            case INGENIERIA:
                if(distance[pos] <=3){
                    closePoint = new Point(ingenieria, "ingenieria exacto");
                }else {
                    closePoint = new Point(ingenieria, "ingenieria");
                }
                break;
            default:
                LatLng nulo = new LatLng(0,0);
                closePoint= new Point(nulo,"estas demasiado lejos");
                break;
        }
    }
    private void choiseOption(){

        if (_binding.options.getText().toString().equals("Porteria a Facultad de ingenieria")){
             creatRoutePorteria(myPosition);
        }
        if (_binding.options.getText().toString().equals("Facultad de ingenieria a porteria")){

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


}