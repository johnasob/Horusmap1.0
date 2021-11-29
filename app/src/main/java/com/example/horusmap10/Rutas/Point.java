package com.example.horusmap10.Rutas;

import com.google.android.gms.maps.model.LatLng;

public class Point {
    LatLng punto;
    String name;
    public Point(LatLng punto, String name) {
        this.punto = punto;
        this.name = name;
    }
    public String name(){
        return name;
    }
    public LatLng coor(){
        return punto;
    }


}
