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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.horusmap10.Rutas.line;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


public class RoutesFragment extends Fragment implements LocationListener, AdapterView.OnItemSelectedListener {

    View vista;
    Activity actividad;
    Context context;

    private String ruta_selected;
    private Spinner spinner;
    private static final int COLOR_BLACK_ARGB = 0xFF2E512D;
    private static final int PRIORITY_HIGH_ACCURACY = 100;
    private static final int PORTERIA = 0;
    private static final int CAJERO = 1;
    private static final int MEDICINA = 2;
    private static final int CAPILLA = 3;
    private static final int BIBLIOTECA = 4;
    private static final int ESCALERAS = 5;
    private static final int INGENIERIA = 6;
    private LatLng closePoint;
    private LatLng finishPoint;
    private final LatLng ingenieria = new LatLng(4.5560932, -75.6601713);
    private final LatLng porteria = new LatLng(4.556286, -75.658436);
    private final LatLng biblioteca = new LatLng(4.5565694, -75.6596335);
    private final LatLng medicina = new LatLng(4.55646, -75.65904);
    private final LatLng cajero = new LatLng(4.55638, -75.65871);
    private final LatLng capilla = new LatLng(4.55642, -75.65933);
    private final LatLng escaleras = new LatLng(4.55630, -75.65995);
    private final LatLng maria = new LatLng(4.5564872, -75.6593398);
    private LatLng position;
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
                    position = new LatLng(4.556286, -75.658436);
                    //position = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                   // mMap.addMarker(new MarkerOptions().position(position).title("mi posición").icon(BitmapDescriptorFactory.fromBitmap(unos)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position,20),2000,null);
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    showmarkers(mMap,position);



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
        vista =  inflater.inflate(R.layout.fragment_routes, container, false);
        spinner = vista.findViewById(R.id.options);
        spinner.setOnItemSelectedListener(this);
        String[] route = vista.getResources().getStringArray(R.array.routes);
        ArrayAdapter adapter = new ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item,route);
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
        Polyline Ruta1 = googleMap.addPolyline(marker.lineRoute(myLocation));
        // Cambia el color de la ruta a negro
        Ruta1.setColor(COLOR_BLACK_ARGB);
        // Agrega los marcadores de los punto estrategicos de la ruta porteria-ingeniería
        googleMap = marker.addMarkers(googleMap);
    }



    private void creatRoute(LatLng start){
        //¿Donde estoy?
        int closePoint = closerPoint(start);
        //Porteria bloque de ingeniería

        //Ingeniería portería
    }
    private void sayRoute(){

    }
    private  void navegation(){

    }
    private void searchProblems(){

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
    private int closerPoint(LatLng start) {
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
        switch (pos){
            case PORTERIA:
                closePoint = porteria;
                Toast.makeText(requireContext(), "La portería esta a "+distance[0]+" metros de usted", Toast.LENGTH_SHORT).show();
                return PORTERIA;
            case CAJERO:
                closePoint = cajero;
                Toast.makeText(requireContext(), "El cajero davivienda del bloque de medicina esta a "+distance[1]+" metros de usted", Toast.LENGTH_SHORT).show();
                return CAJERO;
            case MEDICINA:
                closePoint = medicina;
                Toast.makeText(requireContext(), "El bloque de medicina esta a "+distance[2]+" metros de usted", Toast.LENGTH_SHORT).show();
                return MEDICINA;
            case CAPILLA:
                closePoint =  capilla;
                Toast.makeText(requireContext(), "La iglesia universitaría  esta a "+distance[3]+" metros de usted", Toast.LENGTH_SHORT).show();
                return CAPILLA;
            case BIBLIOTECA:
                closePoint = biblioteca;
                Toast.makeText(requireContext(), "La biblioteca esta "+distance[4]+" metros de usted", Toast.LENGTH_SHORT).show();
                return BIBLIOTECA;
            case ESCALERAS:
                closePoint = escaleras;
                Toast.makeText(requireContext(), "Las escaleras estan a  "+distance[5]+" metros de usted a un costado de su ruta", Toast.LENGTH_SHORT).show();
                return ESCALERAS;
            case INGENIERIA:
                closePoint = ingenieria;
                Toast.makeText(requireContext(), "El bloque de ingeniería esta a  "+distance[6]+" metros de usted", Toast.LENGTH_SHORT).show();
                return INGENIERIA;
            default:
                Toast.makeText(requireContext(), "Usted esta fuera de rango", Toast.LENGTH_SHORT).show();
                return 7;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.options){
            String ruta_selected2 = parent.getItemAtPosition(position).toString();
            if (!ruta_selected2.equals("DALE CLICK PARA ELEGIR UNA DE LAS RUTAS")) {
                ruta_selected = ruta_selected2;
                Toast.makeText(requireContext(), ruta_selected, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}