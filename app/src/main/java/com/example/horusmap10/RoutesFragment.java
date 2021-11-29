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
    public LatLng myPosition = new LatLng(4.5557, -75.6573);
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
                    Toast.makeText(requireContext(), _binding.options.getText(), Toast.LENGTH_SHORT).show();
                    if (_binding.options.getTouchables().isEmpty()){
                        Toast.makeText(requireContext(), "vació", Toast.LENGTH_SHORT).show();
                    }
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
        Toast.makeText(requireContext(), "Esta entrando" , Toast.LENGTH_SHORT).show();
        //¿Donde estoy?
      /*  closerPoint(myPosition);
        switch (closePoint.name()){
            case "ingenieria":
                stations = 0;
                break;
            default:
                stations = 8;
                Toast.makeText(requireContext(), "Estas demaciado lejos de la universidad", Toast.LENGTH_SHORT).show();
        }
        if (stations == 0) {
            closerPoint(start);
            int distance = (int) getDistance(start, closePoint.coor());
            if (distance >= 30) {
                Toast.makeText(requireContext(), "Debes acercarte más a la porteria 2 de la Universidad del Quindío" +
                        "para iniciar tu recorrido", Toast.LENGTH_SHORT).show();
            } else {
                sayRoute(distance, closePoint);
            }
        }
*/

    }
    private void sayRoute(int distance, Point closePoint){
        Toast.makeText(requireContext(), "Camina  "+distance+" metros y te encontraras con "+closePoint.name(), Toast.LENGTH_SHORT).show();
        Toast.makeText(requireContext(),"Recuerda tener cuidado ya que es una entrada vehicular y peatonal", Toast.LENGTH_LONG).show();
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
        if(distance[pos] >= 50){
            pos = 7;
        }
        switch (pos){
            case PORTERIA:
                closePoint = new Point(porteria,"porteria");
                break;
            case CAJERO:
                closePoint = new Point(cajero,"cajero");
                break;
            case MEDICINA:
               closePoint = new Point(medicina,"medicina");
                break;
            case CAPILLA:
                closePoint = new Point(capilla,"capilla");
                break;
            case BIBLIOTECA:
                closePoint = new Point(biblioteca,"biblioteca");
                break;
            case ESCALERAS:
                closePoint = new Point(escaleras,"escaleras");
                break;
            case INGENIERIA:
                closePoint = new Point(ingenieria,"ingenieria");
                break;
            default:
                LatLng nulo = new LatLng(0,0);
                closePoint= new Point(nulo,"nulo");
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        /*String text = _binding.options.getText().toString();
        Toast.makeText(requireContext(), text+ "es este", Toast.LENGTH_SHORT).show();
        if (parent.getId() == R.id.options){
            String ruta_selected2 = parent.getItemAtPosition(position).toString();
            if (!ruta_selected2.equals("DALE CLICK PARA ELEGIR UNA DE LAS RUTAS")) {
                ruta_selected = ruta_selected2;
                Toast.makeText(requireContext(), "Usted ha seleccionado la ruta: "+ruta_selected, Toast.LENGTH_SHORT).show();
                if ( ruta_selected =="  *   Porteria a Facultad de ingenieria"){
                    finishPoint = ingenieria;
                    creatRoutePorteria(myPosition);
                }else if (ruta_selected == "  *   Facultad de ingenieroa a porteria "){
                    finishPoint = porteria;
                }else if (ruta_selected == "DALE CLICK PARA ELEGIR UNA DE LAS RUTAS"){
                    Toast.makeText(requireContext(), "Seleccione una ruta", Toast.LENGTH_SHORT).show();
                }

            }
        }*/
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}