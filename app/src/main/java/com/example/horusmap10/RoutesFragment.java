package com.example.horusmap10;

import static android.content.Context.LOCATION_SERVICE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.location.LocationRequest;



public class RoutesFragment extends Fragment implements LocationListener {

    private View vista;
    private double latitud;
    private double longitud;
    private static final int COLOR_BLACK_ARGB = 0xFF2E512D;
    private static final int PRIORITY_HIGH_ACCURACY = 100;
    private LatLng closePoint;
    private LatLng finishPoint;
    private final LatLng ingenieria = new LatLng(4.55610, -75.66011);
    private final LatLng porteria = new LatLng(4.556286,-75.658436);
    private final LatLng biblioteca = new LatLng(4.556475, -75.659645);
    private final LatLng medicina = new LatLng(4.55646, -75.65904);
    private final LatLng cajero = new LatLng(4.55638, -75.65871);
    private final LatLng capilla = new LatLng(4.55642, -75.65933);
    private final LatLng escaleras = new LatLng(4.55630, -75.65995);
    //Overrides onLocationChanged
    public void onLocationChanged(Location location) {
        latitud = location.getLatitude();
        longitud = location.getLongitude();

    }

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override

        public void onMapReady(GoogleMap googleMap) {
            // Se estrae la ubicación actual de la persona
            LatLng position = new LatLng(latitud, longitud);
            // Muestra los marcadores
            showmarkers(googleMap);
            LatLng prueba = new LatLng(4.556475, -75.659645);
            // Se dibuja un marcador en la posición actual de la persona
            googleMap.addMarker(new MarkerOptions().position(prueba).title("Tu ubicación").icon(bitmapDescriptorFromVector(getActivity().getApplicationContext(), R.drawable.ic_map_person)));
            // Se selecciona el tipo de mapa que se queire dibujar
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            // Se le da una animación a la ubicación actual de la persona de aproximadamente 2segundos y 18 de zoom
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(porteria,18),2000,null);
            closerPoint(prueba);
        }


    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_routes, container, false);

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

    /** Metodo que se encargar de crear el mapa de bits para los marcadores en el mapa*/
    private BitmapDescriptor bitmapDescriptorFromVector (Context context, int vectorResId){
        Drawable vectorDrawable= ContextCompat.getDrawable(context,vectorResId);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap=Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas= new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**Metodo que muestra los marcadores y el camino predeterminado de la porteria 2 a ingeniería*/
    private void showmarkers(GoogleMap googleMap){

        Polyline Ruta1 = googleMap.addPolyline(new PolylineOptions().clickable(true).add(
                new LatLng(4.556286,-75.658436),//porteria//De la portería se sigue recto hasta encontrar un desnivel
                new LatLng(4.556307,-75.658565),//Continúa unos 8pasos/4m y sube una pequeña rampa para empezar la acera podotáctil en la cual continúa hasta el segundo desnivel terminando de pasar por F. Medicina
                new LatLng(4.5564818,-75.6591256),//del desnivel 2 son unos 2.5m y se retoma la acera al subir una pequeña rampa, hasta que justo frente a las escalera de mariacano se llega a un punto de giro.
                new LatLng(4.5564872,-75.6593398),//se gira hacia la izquiera y se avanza 5 baldosas y se retoma a el camino al girar a la derecha hasta llegar al punto de giro para rodear la biblioteca, este punto queda en
                new LatLng(4.5565437,-75.6595571),//Se gira hacia la derecha por 10 aceras y se retoma hacia la izquierda hasta llegar a CRAI
                new LatLng(4.5565694,-75.6596335),// Se encuentra un pequeño desnivel en
                new LatLng(4.5565697,-75.6596845),// Se continúa por 17 aceras hasta llegar al desnivel por entrada del primer parqueadero de la biblioteca
                new LatLng(4.5565554,-75.6597871),//El desnivel termina en
                new LatLng(4.5565139,-75.6598236),//Camina por 21 aceras y se encuentra un desnivel por el segundo parqueadero de la biblioteca
                new LatLng(4.5564454,-75.6599095),//El desnivel termina en
                new LatLng(4.5564083,-75.6599665),//Sigue caminando  hasta que ya se encuentra las escaleras de la entrada de ingeniería, las cuales estan en
                new LatLng(4.5562205,-75.6602367),//Pero sigue derecho hasta
                new LatLng(4.5561025,-75.6603852),//Donde gira unos 100° y se encuentra con un desnivel
                new LatLng(4.5560962,-75.6602427),//y continúa por la acera podotactil hasta la entrada de ing
                new LatLng(4.5560932,-75.6601713)));
        Ruta1.setColor(COLOR_BLACK_ARGB);
        Ruta1.setJointType(JointType.BEVEL);
        googleMap.addMarker(new MarkerOptions().position(porteria).title("Portería 2 de la Universidad del Quindío").icon(bitmapDescriptorFromVector(getActivity().getApplicationContext(), R.drawable.ic_in)));
        googleMap.addMarker(new MarkerOptions().position(ingenieria).title("Facultad de ingeniería").icon(bitmapDescriptorFromVector(getActivity().getApplicationContext(), R.drawable.ic_ingenieria)));
        googleMap.addMarker(new MarkerOptions().position(escaleras).title("Escaleras").icon(bitmapDescriptorFromVector(getActivity().getApplicationContext(), R.drawable.ic_escaleras)));
        googleMap.addMarker(new MarkerOptions().position(biblioteca).title("Biblioteca").icon(bitmapDescriptorFromVector(getActivity().getApplicationContext(), R.drawable.ic_biblioteca)));
        googleMap.addMarker(new MarkerOptions().position(medicina).title("Facultad de medicina").icon(bitmapDescriptorFromVector(getActivity().getApplicationContext(), R.drawable.ic_medicine)));
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
        switch (pos){
            case 0:
                closePoint = porteria;
                break;
            case 1:
                closePoint = cajero;
                break;
            case 2:
                closePoint = medicina;
                break;
            case 3:
                closePoint =  capilla;
                break;
            case 4:
                closePoint = biblioteca;
                break;
            case 5:
                closePoint = escaleras;
                break;
            case 6:
                closePoint = ingenieria;
                break;
        }
    }

    private void creatRoute(){

    }
    private void sayRoute(){

    }
    private  void navegation(){

    }
    private void searchProblems(){

    }

}