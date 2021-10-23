package com.example.horusmap10

import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.horusmap10.databinding.ActivityStartRouteBinding
import android.widget.Button


class StartRouteActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityStartRouteBinding
    private var locationManager : LocationManager? = null
    private lateinit var posicion: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStartRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)



        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

        //Listener for button
        binding.button.setOnClickListener { view ->
            try {
                // Request location updates from network and gps

            } catch(ex: SecurityException) {
                Log.d("myTag", "Security Exception, no location available")
            }
        }

    }

    fun onLocationChanged(location: Location) {
        posicion = arrayOf(location.longitude,location.latitude).toString()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val pos_actual = LatLng(posicion[2].code.toDouble(), posicion[1].code.toDouble())
        //mMap.addMarker(MarkerOptions().position(pos_actual).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos_actual))
    }

    fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    fun onProviderEnabled(provider: String) {}
    fun onProviderDisabled(provider: String) {}
}



