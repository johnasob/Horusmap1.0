package com.example.horusmap10;

import static android.content.Context.LOCATION_SERVICE;
import static com.example.horusmap10.Horusmap1.Horusmap.prefs;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.android.gms.maps.model.PolylineOptions;

public class RoutesFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    View vista;
    Activity actividad;
    Context context;
    String mostrador = "vacio";
    MediaPlayer mediaPlayer;
    private FragmentRoutesBinding _binding;
    private Point closePoint;
    private AutoCompleteTextView spinner;
    public LatLng myPosition = new LatLng(4.556286, -75.658436);
    public int stations = 0;
    private GoogleMap mMap;
    private final line marker = new line(mMap);
    private LocationManager location;
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
                @Override
                public void onLocationChanged(Location location) {
                    mMap.clear();
                    myPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    // se accede a las opciones de ubicación


                    mMap = marker.addMarkers(mMap);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
                    mMap.addMarker(new MarkerOptions().position(myPosition).title("mi posición"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 18), 1500, null);
                    choiseOption(myPosition);


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
        getLocationPermision();
        _binding =FragmentRoutesBinding.inflate(getLayoutInflater());
        vista =  _binding.getRoot();
        prefs.saveMostrador("inicio");
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

    private void sayRoute(int distance, Point closePoint){
        Toast.makeText(requireContext(), "Camina  "+distance+" metros y te encontraras con "+closePoint.name(), Toast.LENGTH_SHORT).show();
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
        if(distance[pos] >= 80){
            pos = 7;
        }
        switch (pos){
            case line.PORTERIA:
                if(distance[pos] <=3){
                    closePoint = new Point(line.porteria, "porteria exacto");
                }else {
                    closePoint = new Point(line.porteria, "porteria");
                }
                break;
            case line.CAJERO:
                if(distance[pos] <=3){
                    closePoint = new Point(line.cajero, "cajero exacto");
                }else {
                    closePoint = new Point(line.cajero, "cajero");
                }
                break;
            case line.MEDICINA:
                if(distance[pos] <=3){
                    closePoint = new Point(line.medicina, "medicina exacto");
                }else {
                    closePoint = new Point(line.medicina, "medicina");
                }
                break;
            case line.CAPILLA:
                if(distance[pos] <=3){
                    closePoint = new Point(line.capilla, "capilla exacto");
                }else {
                    closePoint = new Point(line.capilla, "capilla");
                }
                break;
            case line.BIBLIOTECA:
                if(distance[pos] <=3){
                    closePoint = new Point(line.biblioteca, "biblioteca exacto");
                }else {
                    closePoint = new Point(line.biblioteca, "biblioteca");
                }
                break;
            case line.ESCALERAS:
                if(distance[pos] <=3){
                    closePoint = new Point(line.escaleras, "escaleras exacto");
                }else {
                    closePoint = new Point(line.escaleras, "escaleras");
                }
                break;
            case line.INGENIERIA:
                if(distance[pos] <=3){
                    closePoint = new Point(line.ingenieria, "ingenieria exacto");
                }else {
                    closePoint = new Point(line.ingenieria, "ingenieria");
                }
                break;
            default:
                LatLng nulo = new LatLng(0,0);
                closePoint= new Point(nulo,"estas demasiado lejos");
                break;
        }
    }
    private void choiseOption(LatLng myPosition){

        if (_binding.options.getText().toString().equals("Porteria a Facultad de ingenieria")){
            /*if(prefs.getMostrador() == "inicio") {
                if(prefs.getAlert() == "Activado") {
                    Toast.makeText(requireContext(), "Has seleccionado la ruta: Porteria a Facultad de ingenieria", Toast.LENGTH_LONG).show();
                }
                if(prefs.getSounds() == "Activado"){
                    playsound("start");
                }
                prefs.saveMostrador("iniciado");
            }*/
            creatRoutePorteria(myPosition);
        }
        if (_binding.options.getText().toString().equals("Facultad de ingenieria a porteria")){
            //Toast.makeText(requireContext(), "Has seleccionado la ruta: Facultad de ingeniería a Porteria", Toast.LENGTH_LONG).show();
            mostrador = "ingenieria";
            prefs.saveMostrador(mostrador);
            creatRouteIngenieria(myPosition);
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
        Polyline Ruta1;
        int distance=0;
        int distanceFinish = (int)getDistance(myPosition,line.ingenieria);
        switch (closePoint.name()){
            case "porteria":
                distance = (int) getDistance(start, closePoint.coor());
                Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.porteria,line.medicina,line.biblioteca,line.escaleras,line.ingenieria));
                //Toast.makeText(requireContext(), "Estas cerca de " + closePoint.name(), Toast.LENGTH_LONG).show();
                stations = 0;
                break;
            case "porteria exacto":
                distance = (int) getDistance(myPosition, line.biblioteca);
                Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.medicina,line.biblioteca,line.escaleras,line.ingenieria));
                //Toast.makeText(requireContext(), "Haz llegado a la porteria 2 de la Universidad del Quindío", Toast.LENGTH_LONG).show();
                stations = 1;
                break;
            case "cajero":
                distance = (int) getDistance(myPosition, line.cajero);
                Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.medicina,line.biblioteca,line.escaleras,line.ingenieria));
                //Toast.makeText(requireContext(), "Estas cerca de la facultad de ciencias de la salud", Toast.LENGTH_LONG).show();
                stations = 1;
                break;
            case "cajero exacto":
                distance = (int) getDistance(myPosition, line.biblioteca);

                Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.medicina,line.biblioteca,line.escaleras,line.ingenieria));
                //Toast.makeText(requireContext(),"Estas muy cerca de la entrada de la facultad de ciencias de la salud",Toast.LENGTH_LONG).show();
                stations = 2;
                break;
            case "medicina":
                //Toast.makeText(requireContext(),"Estas muy cerca de  la facultad de ciencias de la salud",Toast.LENGTH_LONG).show();
                distance=(int) getDistance(myPosition, line.biblioteca);
                stations=2;
                Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.biblioteca,line.escaleras,line.ingenieria));
                break;
            case "medicina exacto":
                //Toast.makeText(requireContext(),"Estas muy cerca de  la facultad de ciencias de la salud",Toast.LENGTH_LONG).show();
                //Toast.makeText(requireContext(),"continua caminando para llegar a la biblioteca",Toast.LENGTH_LONG).show();
                distance=(int) getDistance(myPosition, line.biblioteca);
                Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.biblioteca,line.escaleras,line.ingenieria));
                stations =3;
                break;
            case "biblioteca":
                distance=(int)getDistance(myPosition,line.biblioteca);
                //Toast.makeText(requireContext(), "Estas llegando a la entrada de la biblioteca CRAI", Toast.LENGTH_SHORT).show();
                Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.biblioteca,line.escaleras,line.ingenieria));
                stations=3;
                break;
            case "biblioteca exacto":
                distance=(int)getDistance(myPosition,line.escaleras);
                Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.escaleras,line.ingenieria));
                //Toast.makeText(requireContext(), "Haz llegado a la biblioteca CRAI", Toast.LENGTH_SHORT).show();
                stations=4;
                break;
            case "escaleras":
                distance=(int)getDistance(myPosition,line.escaleras);
                //Toast.makeText(requireContext(), "Estas llegando a las escaleras cercanas a al bloque de ingenieria", Toast.LENGTH_SHORT).show();
                Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.escaleras,line.ingenieria));
                stations=4;
                break;
            case "escaleras exacto":
                distance=(int)getDistance(myPosition,line.ingenieria);
                //Toast.makeText(requireContext(), "Haz llegado a las escaleras de ingenieria", Toast.LENGTH_SHORT).show();
                Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.ingenieria));
                stations=5;
                break;
            case "ingenieria":
                distance=(int)getDistance(myPosition,line.ingenieria);
                Ruta1 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.ingenieria));
                stations=5;
                break;
            case "ingenieria exacto":
                //Toast.makeText(requireContext(), "Haz llegado a la facultad de ingenieria", Toast.LENGTH_SHORT).show();
                stations=6;
                break;
            default:
                distance = (int) getDistance(myPosition, line.porteria);
                stations = 8;
                break;
        }

        switch (stations){
            case 0:
                if(prefs.getSounds() == "Activado") {
                    playsound("sound");
                }
                if (distance >= 30) {
                    if(prefs.getAlert() =="Activado"){
                    Toast.makeText(requireContext(), "Debes acercarte más a la porteria 2 de la Universidad del Quindío" +
                            "para iniciar tu recorrido", Toast.LENGTH_SHORT).show();}
                } else {
                    if(prefs.getAlert() =="Activado") {
                        sayRoute(distance, closePoint);
                        Toast.makeText(requireContext(), "Recuerda tener cuidado ya que es una entrada vehicular y peatonal", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case 1:
                if(prefs.getSounds() == "Activado") {
                    playsound("sound");
                }
                if(prefs.getAlert() =="Activado") {
                    Toast.makeText(requireContext(), "Camina " + distance + " metros por la acera podotactil y te encontraras con la entrada al bloque de salud", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if(prefs.getSounds() == "Activado") {
                    playsound("sound");
                }
                if(prefs.getAlert() =="Activado") {
                    Toast.makeText(requireContext(), "Estas a: " + distance + " metros  de la biblioteca, pasando el bloque de la facultad de ciencias de la salud", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                if(prefs.getSounds() == "Activado") {
                    playsound("sound");
                }
                if(prefs.getAlert() =="Activado") {
                    Toast.makeText(requireContext(), "Sigue caminando: " + distance + " metros por la acera podotactil y llegara a la biblioteca", Toast.LENGTH_SHORT).show();
                }
                break;
            case 4:
                if(prefs.getSounds() == "Activado") {
                    playsound("sound");
                }
                if(prefs.getAlert() =="Activado"){
                Toast.makeText(requireContext(), "Continua caminando: "+distance+" metros por la acera podotactil y llegaras a las escaleras contiguas a la entrada" +
                        "del bloque de ingenieria", Toast.LENGTH_SHORT).show();}
                break;
            case 5:
                if(prefs.getSounds() == "Activado") {
                    playsound("sound");
                }
                if(prefs.getAlert() =="Activado") {
                    Toast.makeText(requireContext(), "En :" + distance + " metros, estaras llegando a la entrada de la facultad de ingenieria", Toast.LENGTH_SHORT).show();
                }
                break;
            case 6:
                if(prefs.getSounds() == "Activado") {
                    playsound("sound");
                }
                if(prefs.getAlert() =="Activado"){
                Toast.makeText(requireContext()," Tu recorrido a terminado",Toast.LENGTH_LONG).show();}
                mostrador = "terminado";
                prefs.saveMostrador(mostrador);
                if(prefs.getSounds() == "Activado"){
                    playsound("llegada");
                }
                break;
            default:
                if(prefs.getSounds() == "Activado") {
                    playsound("error");
                }
                if(prefs.getAlert() =="Activado"){
                Toast.makeText(requireContext(),"otra excepcion",Toast.LENGTH_LONG).show();}
                break;
        }
        if(prefs.getAlert() =="Activado") {
            Toast.makeText(getActivity(), "Usted se encuentra a: " + distanceFinish + " metros del bloque de Ingeniería", Toast.LENGTH_LONG).show();
        }

    }
    private void creatRouteIngenieria(LatLng start){

        closerPoint(myPosition);
        Polyline Ruta2;
        int distance=0;
        int distanceFinish = (int)getDistance(myPosition,line.ingenieria);
        switch (closePoint.name()){
            case "ingenieria":
                distance = (int) getDistance(start, closePoint.coor());
                Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.ingenieria,line.escaleras,line.biblioteca,line.medicina,line.porteria));
                if(prefs.getAlert() =="Activado") {
                    Toast.makeText(requireContext(), "Estas cerca de " + closePoint.name(), Toast.LENGTH_LONG).show();
                }stations = 0;
                break;
            case "ingenieria exacto":
                distance = (int) getDistance(myPosition, line.biblioteca);
                Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.escaleras,line.biblioteca,line.medicina,line.porteria));
                if(prefs.getAlert() =="Activado") {
                    Toast.makeText(requireContext(), "Haz llegado a la entrada del bloque de ingenieria", Toast.LENGTH_LONG).show();
                }
                stations = 1;
                break;
            case "escaleras":
                distance = (int) getDistance(myPosition, line.cajero);
                Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.escaleras,line.biblioteca,line.medicina,line.porteria));
                if(prefs.getAlert() =="Activado") {
                    Toast.makeText(requireContext(), "Estas cerca de las escaleras cercanas a la biblioteca CRAI", Toast.LENGTH_LONG).show();
                }
                stations = 1;
                break;
            case "escaleras exacto":
                distance = (int) getDistance(myPosition, line.biblioteca);

                Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.biblioteca,line.medicina,line.porteria));
                if(prefs.getAlert() =="Activado") {
                    Toast.makeText(requireContext(), "Haz llegado a las escaleras cercanas a la biblioteca CRAI", Toast.LENGTH_LONG).show();
                }
                stations = 2;
                break;

            case "biblioteca":
                if(prefs.getAlert() =="Activado"){
                Toast.makeText(requireContext(),"Esta cerca de la biblioteca",Toast.LENGTH_LONG).show();}
                distance=(int) getDistance(myPosition, line.biblioteca);
                stations=2;
                Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.biblioteca,line.medicina,line.porteria));
                break;
            case "biblioteca exacto":
                if(prefs.getAlert() =="Activado") {
                    Toast.makeText(requireContext(), "Estas muy cerca de la biblioteca", Toast.LENGTH_LONG).show();
                    Toast.makeText(requireContext(), "continua caminando por la acera podotactil para llegar a la facultad de medicina", Toast.LENGTH_LONG).show();
                }
                distance=(int) getDistance(myPosition, line.biblioteca);
                Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.medicina,line.porteria));
                stations =3;
                break;
            case "medicina":
                distance=(int)getDistance(myPosition,line.biblioteca);
                if(prefs.getAlert() =="Activado") {
                    Toast.makeText(requireContext(), "Te estas acercando a la facultad de medicina", Toast.LENGTH_SHORT).show();
                }
                Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.medicina,line.porteria));
                stations=3;
                break;
            case "medicina exacto":
                distance=(int)getDistance(myPosition,line.escaleras);
                Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.porteria));
                if(prefs.getAlert() =="Activado") {
                    Toast.makeText(requireContext(), "Te encuentras muy cerca de la facultad de salud", Toast.LENGTH_SHORT).show();
                }
                stations=4;
                break;
            case "cajero": distance=(int)getDistance(myPosition,line.escaleras);
                if(prefs.getAlert() =="Activado") {
                    Toast.makeText(requireContext(), "Pronto llegaras al cajero contigua a la entrada principal de la facultad de salud", Toast.LENGTH_SHORT).show();
                }
                Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.porteria));
                stations=4;
                break;
            case "cajero exacto":
                distance=(int)getDistance(myPosition,line.ingenieria);
                if(prefs.getAlert() =="Activado") {
                    Toast.makeText(requireContext(), "Estas muy cerca a la entrada de la facultad de ciencias de la salud", Toast.LENGTH_SHORT).show();
                }
                Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.porteria));
                stations=5;
                break;
            case "porteria":
                distance=(int)getDistance(myPosition,line.ingenieria);
                if(prefs.getAlert() =="Activado") {
                    Toast.makeText(requireContext(), "Estas llegando a la porteria 2 de la Universidad del Quindío", Toast.LENGTH_SHORT).show();
                }
                Ruta2 = mMap.addPolyline(new PolylineOptions().add(myPosition,line.porteria));
                stations=5;
                break;
            case "porteria exacto":
                if(prefs.getAlert() =="Activado"){
                Toast.makeText(requireContext(), "Haz llegado a la porteria 2 de la Universidad" +
                        "del Quindío", Toast.LENGTH_SHORT).show();}
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
                    Toast.makeText(requireContext(), "Debes acercarte más a la entrada principal de la facultad de ingeniería" +
                            "para iniciar tu recorrido", Toast.LENGTH_SHORT).show();}
                } else {
                    sayRoute(distance, closePoint);
                    if(prefs.getAlert() =="Activado") {
                        Toast.makeText(requireContext(), "Recuerda tener cuidado ya que es un camino peatonal y vehicular", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case 1:
                if(prefs.getAlert() =="Activado"){
                Toast.makeText(requireContext(), "Camina " + distance + " metros por la acera podotactil y te encontraras con las escaleras cercanas al la" +
                        "biblioteca CRAI", Toast.LENGTH_SHORT).show();}
                break;
            case 2:
                if(prefs.getAlert() =="Activado") {
                    Toast.makeText(requireContext(), "Estas a: " + distance + " metros  de la biblioteca CRAI", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                if(prefs.getAlert() =="Activado"){
                Toast.makeText(requireContext(), "Sigue caminando: "+distance+" metros por la acera podotactil y llegaras a la facultad de ciencias de la " +
                        "salud", Toast.LENGTH_SHORT).show();}
                break;
            case 4:
                if(prefs.getAlert() =="Activado"){
                Toast.makeText(requireContext(), "Continua caminando: "+distance+" metros por la acera podotactil y llegaras a la entrada " +
                        "principal de la facultad de ciencias de la salud", Toast.LENGTH_SHORT).show();}
                break;
            case 5:
                if(prefs.getAlert() =="Activado") {
                    Toast.makeText(requireContext(), "En :" + distance + " metros, estaras llegando a la porteria 2 de la Universidad del Quindío", Toast.LENGTH_SHORT).show();
                }
                break;
            case 6:
                if(prefs.getAlert() =="Activado") {
                    Toast.makeText(requireContext(), " Tu recorrido a terminado", Toast.LENGTH_LONG).show();
                }
                mostrador = "terminado";
                prefs.saveMostrador(mostrador);
                break;
            default:
                if(prefs.getAlert() =="Activado"){
                Toast.makeText(requireContext(),"otra excepcion",Toast.LENGTH_LONG).show();}
                break;
        }
        if(prefs.getAlert() =="Activado") {
            Toast.makeText(requireContext(), "Usted se encuentra a: " + distanceFinish + " metros de la porteria 2 de la Universidad del Quindío", Toast.LENGTH_SHORT).show();
        }

    }
}