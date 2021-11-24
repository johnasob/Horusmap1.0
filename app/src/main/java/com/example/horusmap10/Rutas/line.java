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
    public PolylineOptions lineRoute(LatLng myLocation ){
         PolylineOptions Ruta1 = new PolylineOptions().add(
                 myLocation,
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
                new LatLng(4.5560932,-75.6601713));
        return Ruta1;
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
