package com.example.horusmap10.Rutas;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.widget.Toast;

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
    public static final LatLng ingenieria = new LatLng(4.5560932, -75.6601713);
    public static final LatLng porteria = new LatLng(4.556319, -75.658436);
    public static final LatLng biblioteca = new LatLng(4.556486, -75.659639);
    public static final LatLng medicina = new LatLng(4.55646, -75.65904);
    public static final LatLng cajero = new LatLng(4.55638, -75.65871);
    public static final LatLng capilla = new LatLng(4.55642, -75.65933);
    public static final LatLng escaleras = new LatLng(4.55630, -75.65995);
    public static final LatLng maria = new LatLng(4.5564851, -75.659308);
    public static final int PORTERIA = 0;
    public static final int CAJERO = 1;
    public static final int MEDICINA = 2;
    public static final int CAPILLA = 3;
    public static final int BIBLIOTECA = 4;
    public static final int ESCALERAS = 5;
    public static final int INGENIERIA = 6;
    public PolylineOptions lineRoute(LatLng myLocation, int stations){
        PolylineOptions Ruta1;
        switch (stations){
            case 0:
               Ruta1 = new PolylineOptions().add(myLocation,porteria,cajero,medicina,biblioteca,escaleras,ingenieria);
               break;
            case 1:
               Ruta1 = new PolylineOptions().add(myLocation,cajero,medicina,biblioteca,escaleras,ingenieria);
               break;
            case 2:
                Ruta1 = new PolylineOptions().add(myLocation,medicina,biblioteca,escaleras,ingenieria);
                break;
            case 3:
                Ruta1 = new PolylineOptions().add(myLocation,biblioteca,escaleras,ingenieria);
                break;
            case 4:
                Ruta1 = new PolylineOptions().add(myLocation,escaleras,ingenieria);
                break;
            case 5:
               Ruta1 = new PolylineOptions().add(myLocation,ingenieria);
                break;
            default:
                Ruta1 = new PolylineOptions().add(porteria,cajero,medicina,biblioteca,escaleras,ingenieria);
                break;
        }
         return Ruta1;
    }
    public GoogleMap addMarkers(GoogleMap googleMap){
        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_porteria)).position(porteria).title("Portería 2 de la Universidad del Quindío"));
        googleMap.addMarker(new MarkerOptions().position(ingenieria).title("Facultad de ingeniería").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_inge)));
        googleMap.addMarker(new MarkerOptions().position(escaleras).title("Escaleras").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_escaleras)));
        googleMap.addMarker(new MarkerOptions().position(biblioteca).title("Biblioteca").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_biblioteca)));
        googleMap.addMarker(new MarkerOptions().position(medicina).title("Facultad de medicina").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_medicine)));

        return googleMap;
    }

}
