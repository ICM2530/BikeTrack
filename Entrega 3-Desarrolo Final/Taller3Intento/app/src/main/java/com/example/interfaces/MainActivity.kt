package com.example.interfaces

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.example.interfaces.Navigation.Navigation
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database


// HAY DOS CLASES DE PUNTOS EN EL MAPA
// NO PUEDE IMPLEMENTAR SECRETOS
// Ya esta autenticando bien, toca actualizar de que parte viene la ubicación, y de que reciba todos los campos
//


val PATH_USERS = "users/"
val PATH_ALERTS = "alerts/"

val PATH_ACT = "activities/"
val firebaseAuth = FirebaseAuth.getInstance()
val database = Firebase.database
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {

        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Navigation()
        }
    }



}