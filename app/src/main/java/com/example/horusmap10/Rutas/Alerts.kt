package com.example.horusmap10.Rutas

import android.app.Activity
import android.content.Context
import android.location.Location
import android.widget.Toast
import com.example.horusmap10.Horusmap1.Horusmap
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
private lateinit var closePoint: Point
class Alerts() {

    var stations = 0
    fun RunThread(
        Cadena: String,
        contexto: Context,
        actividad: Activity,
        myPosition: LatLng,
        mMap: GoogleMap,
        start: LatLng
    ) {

        GlobalScope.launch {


            actividad.runOnUiThread {
                closerPoint(myPosition)
                val Ruta2: Polyline
                var distance = 0
                val distanceFinish = getDistance(myPosition, line.ingenieria).toInt()
                when (closePoint!!.name()) {
                    "ingenieria" -> {
                        distance = getDistance(start, closePoint!!.coor()).toInt()
                        Ruta2 = mMap.addPolyline(
                            PolylineOptions().add(
                                myPosition,
                                line.ingenieria,
                                line.escaleras,
                                line.biblioteca,
                                line.medicina,
                                line.porteria
                            )
                        )
                        if (Horusmap.prefs.getAlert() === "Activado") {
                            Toast.makeText(
                                contexto,
                                "Estas cerca de " + closePoint!!.name(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        stations = 0
                    }
                    "ingenieria exacto" -> {
                        distance = getDistance(myPosition, line.biblioteca).toInt()
                        Ruta2 = mMap.addPolyline(
                            PolylineOptions().add(
                                myPosition,
                                line.escaleras,
                                line.biblioteca,
                                line.medicina,
                                line.porteria
                            )
                        )
                        if (Horusmap.prefs.getAlert() === "Activado") {
                            Toast.makeText(
                                contexto,
                                "Haz llegado a la entrada del bloque de ingenieria",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        stations = 1
                    }
                    "escaleras" -> {
                        distance = getDistance(myPosition, line.cajero).toInt()
                        Ruta2 = mMap.addPolyline(
                            PolylineOptions().add(
                                myPosition,
                                line.escaleras,
                                line.biblioteca,
                                line.medicina,
                                line.porteria
                            )
                        )
                        if (Horusmap.prefs.getAlert() === "Activado") {
                            Toast.makeText(
                                contexto,
                                "Estas cerca de las escaleras cercanas a la biblioteca CRAI",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        stations = 1
                    }
                    "escaleras exacto" -> {
                        distance = getDistance(myPosition, line.biblioteca).toInt()
                        Ruta2 = mMap.addPolyline(
                            PolylineOptions().add(
                                myPosition,
                                line.biblioteca,
                                line.medicina,
                                line.porteria
                            )
                        )
                        if (Horusmap.prefs.getAlert() === "Activado") {
                            Toast.makeText(
                                contexto,
                                "Haz llegado a las escaleras cercanas a la biblioteca CRAI",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        stations = 2
                    }
                    "biblioteca" -> {
                        if (Horusmap.prefs.getAlert() === "Activado") {
                            Toast.makeText(
                                contexto,
                                "Esta cerca de la biblioteca",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        distance = getDistance(myPosition, line.biblioteca).toInt()
                        stations = 2
                        Ruta2 = mMap.addPolyline(
                            PolylineOptions().add(
                                myPosition,
                                line.biblioteca,
                                line.medicina,
                                line.porteria
                            )
                        )
                    }
                    "biblioteca exacto" -> {
                        if (Horusmap.prefs.getAlert() === "Activado") {
                            Toast.makeText(
                                contexto,
                                "Estas muy cerca de la biblioteca",
                                Toast.LENGTH_LONG
                            ).show()
                            Toast.makeText(
                                contexto,
                                "continua caminando por la acera podotactil para llegar a la facultad de medicina",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        distance = getDistance(myPosition, line.biblioteca).toInt()
                        Ruta2 = mMap.addPolyline(
                            PolylineOptions().add(
                                myPosition,
                                line.medicina,
                                line.porteria
                            )
                        )
                        stations = 3
                    }
                    "medicina" -> {
                        distance = getDistance(myPosition, line.biblioteca).toInt()
                        if (Horusmap.prefs.getAlert() === "Activado") {
                            Toast.makeText(
                                contexto,
                                "Te estas acercando a la facultad de medicina",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Ruta2 = mMap.addPolyline(
                            PolylineOptions().add(
                                myPosition,
                                line.medicina,
                                line.porteria
                            )
                        )
                        stations = 3
                    }
                    "medicina exacto" -> {
                        distance = getDistance(myPosition, line.escaleras).toInt()
                        Ruta2 = mMap.addPolyline(PolylineOptions().add(myPosition, line.porteria))
                        if (Horusmap.prefs.getAlert() === "Activado") {
                            Toast.makeText(
                                contexto,
                                "Te encuentras muy cerca de la facultad de salud",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        stations = 4
                    }
                    "cajero" -> {
                        distance = getDistance(myPosition, line.escaleras).toInt()
                        if (Horusmap.prefs.getAlert() === "Activado") {
                            Toast.makeText(
                                contexto,
                                "Pronto llegaras al cajero contigua a la entrada principal de la facultad de salud",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Ruta2 = mMap.addPolyline(PolylineOptions().add(myPosition, line.porteria))
                        stations = 4
                    }
                    "cajero exacto" -> {
                        distance = getDistance(myPosition, line.ingenieria).toInt()
                        if (Horusmap.prefs.getAlert() === "Activado") {
                            Toast.makeText(
                                contexto,
                                "Estas muy cerca a la entrada de la facultad de ciencias de la salud",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Ruta2 = mMap.addPolyline(PolylineOptions().add(myPosition, line.porteria))
                        stations = 5
                    }
                    "porteria" -> {
                        distance = getDistance(myPosition, line.ingenieria).toInt()
                        if (Horusmap.prefs.getAlert() === "Activado") {
                            Toast.makeText(
                                contexto,
                                "Estas llegando a la porteria 2 de la Universidad del Quindío",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Ruta2 = mMap.addPolyline(PolylineOptions().add(myPosition, line.porteria))
                        stations = 5
                    }
                    "porteria exacto" -> {
                        if (Horusmap.prefs.getAlert() === "Activado") {
                            Toast.makeText(
                                contexto, "Haz llegado a la porteria 2 de la Universidad" +
                                        "del Quindío", Toast.LENGTH_SHORT
                            ).show()
                        }
                        stations = 6
                    }
                    else -> {
                        distance = getDistance(myPosition, line.porteria).toInt()
                        stations = 8
                    }
                }

                when (stations) {
                    0 -> if (distance >= 30) {
                        if (Horusmap.prefs.getAlert() === "Activado") {
                            Toast.makeText(
                                contexto,
                                "Debes acercarte más a la entrada principal de la facultad de ingeniería" +
                                        "para iniciar tu recorrido",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        if (Horusmap.prefs.getAlert() === "Activado") {
                            Toast.makeText(
                                contexto,
                                "Recuerda tener cuidado ya que es un camino peatonal y vehicular",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    1 -> if (Horusmap.prefs.getAlert() === "Activado") {
                        Toast.makeText(
                            contexto,
                            "Camina " + distance + " metros por la acera podotactil y te encontraras con las escaleras cercanas al la" +
                                    "biblioteca CRAI",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    2 -> if (Horusmap.prefs.getAlert() === "Activado") {
                        Toast.makeText(
                            contexto,
                            "Estas a: $distance metros  de la biblioteca CRAI",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    3 -> if (Horusmap.prefs.getAlert() === "Activado") {
                        Toast.makeText(
                            contexto,
                            "Sigue caminando: " + distance + " metros por la acera podotactil y llegaras a la facultad de ciencias de la " +
                                    "salud",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    4 -> if (Horusmap.prefs.getAlert() === "Activado") {
                        Toast.makeText(
                            contexto,
                            "Continua caminando: " + distance + " metros por la acera podotactil y llegaras a la entrada " +
                                    "principal de la facultad de ciencias de la salud",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    5 -> if (Horusmap.prefs.getAlert() === "Activado") {
                        Toast.makeText(
                            contexto,
                            "En :$distance metros, estaras llegando a la porteria 2 de la Universidad del Quindío",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    6 -> {
                        if (Horusmap.prefs.getAlert() === "Activado") {
                            Toast.makeText(
                                contexto,
                                " Tu recorrido a terminado",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        /**mostrador = "terminado"
                        Horusmap.prefs.saveMostrador(mostrador)*/
                    }
                    else -> if (Horusmap.prefs.getAlert() === "Activado") {
                        Toast.makeText(contexto, "otra excepcion", Toast.LENGTH_LONG).show()
                    }
                }
                if (Horusmap.prefs.getAlert() === "Activado") {
                    Toast.makeText(
                        contexto,
                        "Usted se encuentra a: $distanceFinish metros de la porteria 2 de la Universidad del Quindío",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }


    /**Metodo que encuentra la distancia entre dos coordenadas */
    private fun getDistance(start: LatLng, finish: LatLng): Double {
        //Inicializa el locationrequest que se usa para encontrar la distancia
        val locationRequest = LocationRequest.create()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val distances = FloatArray(1)
        //Calcula la distancia
        Location.distanceBetween(
            start.latitude,
            start.longitude,
            finish.latitude,
            finish.longitude,
            distances
        )
        return distances[0].toDouble()
    }


    /**Calculo de el punto más cercano al punto dado */
    private fun closerPoint(start: LatLng) {
        val distance = DoubleArray(7)
        distance[0] = getDistance(start, line.porteria)
        distance[1] = getDistance(start, line.cajero)
        distance[2] = getDistance(start, line.medicina)
        distance[3] = getDistance(start, line.capilla)
        distance[4] = getDistance(start, line.biblioteca)
        distance[5] = getDistance(start, line.escaleras)
        distance[6] = getDistance(start, line.ingenieria)
        var min = distance[0]
        var pos = 0
        for (i in distance.indices) {
            if (distance[i] <= min) {
                min = distance[i]
                pos = i
            }
        }
        if (distance[pos] >= 80) {
            pos = 7
        }
        when (pos) {
            line.PORTERIA -> if (distance[pos] <= 3) {
                closePoint = Point(line.porteria, "porteria exacto")
            } else {
                closePoint = Point(line.porteria, "porteria")
            }
            line.CAJERO -> if (distance[pos] <= 3) {
                closePoint = Point(line.cajero, "cajero exacto")
            } else {
                closePoint = Point(line.cajero, "cajero")
            }
            line.MEDICINA -> if (distance[pos] <= 3) {
                closePoint = Point(line.medicina, "medicina exacto")
            } else {
                closePoint = Point(line.medicina, "medicina")
            }
            line.CAPILLA -> if (distance[pos] <= 3) {
                closePoint = Point(line.capilla, "capilla exacto")
            } else {
                closePoint = Point(line.capilla, "capilla")
            }
            line.BIBLIOTECA -> if (distance[pos] <= 3) {
                closePoint = Point(line.biblioteca, "biblioteca exacto")
            } else {
                closePoint = Point(line.biblioteca, "biblioteca")
            }
            line.ESCALERAS -> if (distance[pos] <= 3) {
                closePoint = Point(line.escaleras, "escaleras exacto")
            } else {
                closePoint = Point(line.escaleras, "escaleras")
            }
            line.INGENIERIA -> if (distance[pos] <= 3) {
                closePoint = Point(line.ingenieria, "ingenieria exacto")
            } else {
                closePoint = Point(line.ingenieria, "ingenieria")
            }
            else -> {
                val nulo = LatLng(0.0, 0.0)
                closePoint = Point(nulo, "estas demasiado lejos")
            }
        }

    }
}