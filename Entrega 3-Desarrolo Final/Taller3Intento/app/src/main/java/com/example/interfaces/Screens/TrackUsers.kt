package com.example.interfaces.Screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.interfaces.R
import com.example.interfaces.Services.Punto5.Companion.sendUserAvailableNotification
import com.example.interfaces.ViewModel.AvailableUsersViewModel
import com.example.interfaces.ViewModel.MyUserViewModel
import com.example.interfaces.ViewModel.MyUser
import com.example.interfaces.ViewModel.UserStateMonViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.android.compose.*

fun distance(lat1 : Double, long1: Double, lat2:Double, long2:Double) : Double{
    //Genera diferencia entre latitudes y longitudes en radianes
    val latDistance = Math.toRadians(lat1 - lat2)
    val lngDistance = Math.toRadians(long1 - long2)
    //Genera una formual segun datos
    val a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)+
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    val result = 6378 * c;
    return Math.round(result*100.0)/100.0;
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackUserScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: AvailableUsersViewModel = viewModel(),
    trackedMail: String?,
    onDone:()->Unit
) {

    val context = LocalContext.current

    // We observe the list of available users from the ViewModel
    val availableUsers by viewModel.availableUsers.collectAsState()

    // This holds the previous states only inside this screen
    val previousState = remember { mutableStateMapOf<String, Boolean>() }
    // Find the user you want to track
    val trackedUser = availableUsers.firstOrNull { it.mail == trackedMail }

// Your own location (You probably already manage this somewhere)
    var myLocation by remember { mutableStateOf<LatLng?>(null) }

// Default map configs if not provided
    val uiSettings = remember { MapUiSettings(zoomControlsEnabled = true) }
    val properties = remember { MapProperties(isMyLocationEnabled = true) }
    val monitorVM: UserStateMonViewModel = viewModel()

    //--COLORES
    var MainColor by remember { mutableStateOf(com.example.interfaces.R.color.white) }
    var MainTextcolor by remember { mutableStateOf(com.example.interfaces.R.color.black) }

    val MapLight = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.light_style)
    }
    //Oscuro
    val MapDark = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.night_style)
    }

    var ActualStyle by remember { mutableStateOf(MapLight) }
    //--------_
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val sensorLuz = remember{sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)}
    val sensorListener = remember {
        object : SensorEventListener{
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
                    val luz = event.values[0]

                    //Se define que estilo de mapa se usara
                    ActualStyle = if(luz >= 2000) MapLight else MapDark
                    if(luz >= 2000){
                        MainColor = com.example.interfaces.R.color.white
                        MainTextcolor = com.example.interfaces.R.color.black
                    }else{
                        MainColor = com.example.interfaces.R.color.MainMenuColor
                        MainTextcolor = R.color.white
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(Unit) {
        sensorManager.registerListener(
            sensorListener,
            sensorLuz,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }


    LaunchedEffect(Unit) {
        viewModel.attachMonitor(monitorVM)
    }


    Scaffold(
        containerColor = colorResource(MainColor),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.MainMenuColor),
                    titleContentColor = Color.White
                ),
                title = { Text("Rastreando usuario") },
                navigationIcon = {
                    IconButton(onClick = { onDone() }) {
                        Icon(Icons.Default.ArrowBack, "Atrás", tint = Color.White)
                    }
                }


            )
        }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val cameraPositionState = rememberCameraPositionState()

                LaunchedEffect(trackedUser) {
                    if (trackedUser != null) {
                        val tLoc = LatLng(trackedUser.latitude, trackedUser.longitude)
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(tLoc, 15f)
                    }


                }

                LaunchedEffect(myLocation, trackedUser) {
                    if (myLocation != null &&
                        trackedUser != null &&
                        trackedUser.latitude != 0.0 &&
                        trackedUser.longitude != 0.0
                    ) {
                        val dist = distance(
                            myLocation!!.latitude,
                            myLocation!!.longitude,
                            trackedUser.latitude,
                            trackedUser.longitude
                        )

                        Toast.makeText(
                            context,
                            "Distancia: $dist km",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }



                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    properties = MapProperties(
                        mapStyleOptions = ActualStyle
                    ),
                    uiSettings = uiSettings,
                    cameraPositionState = cameraPositionState
                ) {

                    myLocation?.let { loc ->
                        Marker(
                            state = MarkerState(position = loc),
                            title = "Yo"
                        )

                        LaunchedEffect(loc) {
                            cameraPositionState.position =
                                CameraPosition.fromLatLngZoom(loc, 15f)
                        }

                    }

                    if (trackedUser != null &&
                        trackedUser.latitude != 0.0 &&
                        trackedUser.longitude != 0.0
                    ) {
                        val tLoc = LatLng(trackedUser.latitude, trackedUser.longitude)

                        Marker(
                            state = MarkerState(position = tLoc),
                            title = "${trackedUser.name} ${trackedUser.lastName}"
                        )


                    }
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun requestLocation(
    context: android.content.Context,
    onLocationReceived: (Location) -> Unit
) {
    val fused = LocationServices.getFusedLocationProviderClient(context)

    fused.lastLocation.addOnSuccessListener { loc ->
        if (loc != null) {
            onLocationReceived(loc)
        } else {
            val req = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                2000
            ).build()

            fused.requestLocationUpdates(
                req,
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        val l = result.lastLocation ?: return
                        onLocationReceived(l)
                        fused.removeLocationUpdates(this)
                    }
                },
                Looper.getMainLooper()
            )
        }
    }
}
