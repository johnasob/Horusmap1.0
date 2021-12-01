package com.example.horusmap10.Rutas;

import com.example.horusmap10.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class line {
    public line(GoogleMap googleMap) {
        line.googleMap = googleMap;
    }

    public static GoogleMap googleMap;
    public Polyline Ruta1;
    private static final int COLOR_BLACK_ARGB = 0xFD0000;
    private static final int PRIORITY_HIGH_ACCURACY = 100;
    public final LatLng ingenieria = new LatLng(4.5560932, -75.6601713);
    public final LatLng porteria = new LatLng(4.556286, -75.658436);
    public final LatLng biblioteca = new LatLng(4.5565694, -75.6596335);
    public final LatLng medicina = new LatLng(4.55646, -75.65904);
    public final LatLng cajero = new LatLng(4.55638, -75.65871);
    public final LatLng capilla = new LatLng(4.55642, -75.65933);
    public final LatLng escaleras = new LatLng(4.55630, -75.65995);
    public final LatLng maria = new LatLng(4.5564872, -75.6593398);
    public PolylineOptions lineRoute(LatLng myLocation, int stations){
        if (stations == 1) {
            PolylineOptions Ruta1 = new PolylineOptions().add(myLocation,cajero,medicina,biblioteca,escaleras,ingenieria);
            return Ruta1;
        }else{
            PolylineOptions Ruta1 = new PolylineOptions().add(myLocation,porteria,cajero,medicina,biblioteca,escaleras,ingenieria);
            return Ruta1;
        }
    }
    public GoogleMap addMarkers(GoogleMap googleMap){
        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_porteria)).position(porteria).title("Portería 2 de la Universidad del Quindío"));
        googleMap.addMarker(new MarkerOptions().position(ingenieria).title("Facultad de ingeniería").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_inge)));
        googleMap.addMarker(new MarkerOptions().position(escaleras).title("Escaleras").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_escaleras)));
        googleMap.addMarker(new MarkerOptions().position(biblioteca).title("Biblioteca").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_biblioteca)));
        googleMap.addMarker(new MarkerOptions().position(medicina).title("Facultad de medicina").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_medicine)));
        googleMap.addMarker(new MarkerOptions().position(maria).title("Laboratorio al aire libre Maria Cano").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_artes)));

        return googleMap;
    }

}