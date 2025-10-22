package com.example.interfaces.Screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Looper
import android.os.StrictMode
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.interfaces.Navigation.AppScreens
import com.example.interfaces.Navigation.Navigation
import com.example.interfaces.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.lang.Math.toDegrees
import kotlin.collections.forEach
import kotlin.math.roundToInt

import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager


class MakeActivity : ComponentActivity(), SensorEventListener {
    lateinit var locationClient : FusedLocationProviderClient
    private lateinit var sensorManager: SensorManager
    private var rotationVectorSensor: Sensor? = null
    private var azimuth by mutableStateOf(0f)
    val locationRequest = createLocationRequest()
    lateinit var locationCallback : LocationCallback


    fun startLocationUpdates(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED){
            locationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    val locationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ActivityResultCallback{
            if(it ){
                //Hay Permiso
                startLocationUpdates()
            }else{
                //No hay permiso
            }
        }
    )

    fun stopLocationUpdates(){
        locationClient.removeLocationUpdates(locationCallback)
    }
    val locationViewModel = LocationViewModel()
    var currentAzimuth: Float
        get() = azimuth
        set(value) { azimuth = value }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().userAgentValue = "AndroidApp"

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        locationPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        locationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = createLocationCallback(locationViewModel)

        setContent {
            GPSDataUpdates(locationViewModel, azimuth) // Your navigation host
        }
    }
    override fun onResume() {
        super.onResume()
        rotationVectorSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

            val orientationAngles = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientationAngles)

            val azimuthInDeg = (toDegrees(orientationAngles[0].toDouble()).toFloat() + 360) % 360
            azimuth = azimuthInDeg
        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    @Composable
    fun GPSDataUpdates(viewModel: LocationViewModel, azimuth : Float){
        val state = viewModel.state.collectAsState()
        var active by remember { mutableStateOf(true) }
        var alertCoords by remember { mutableStateOf<List<GeoPoint>>(emptyList()) }
        val direction = getDirectionLetter(azimuth)
        val grados = azimuth.roundToInt()
        var ScreenColor by remember { mutableStateOf(R.color.white) }
        var buttoncolors by remember { mutableStateOf(R.color.MainMenuColor) }
        var textcolors by remember { mutableStateOf(R.color.black) }
        var pubcolors by remember { mutableStateOf(R.color.Dark) }
        var AutoturnDark by remember { mutableStateOf(false) }
        val barometerSensor = remember {
            sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)}
        var IsRainy by remember{mutableStateOf(false)}
        val barsensorListener = remember {
            object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event?.sensor?.type == Sensor.TYPE_PRESSURE) {
                        val result = event.values[0]
                        Log.i("Barom", result.toString())
                        IsRainy = if (result < 1000) true else false
                    }
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
        }
        DisposableEffect(Unit) {
            sensorManager.registerListener(barsensorListener, barometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
            onDispose {
                sensorManager.unregisterListener(barsensorListener)
            }
        }
        val humSensor = remember {
            sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)
        }
        var res by remember{mutableStateOf(0f)}
        val humsensorListener = remember {
            object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event?.sensor?.type == Sensor.TYPE_RELATIVE_HUMIDITY) {
                        val result = event.values[0]
                        Log.i("HUm", result.toString())
                        res = result
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
        }
        DisposableEffect(Unit) {
            sensorManager.registerListener(humsensorListener, humSensor, SensorManager.SENSOR_DELAY_NORMAL)
            onDispose {
                sensorManager.unregisterListener(humsensorListener)
            }
        }
        Box(modifier = Modifier.fillMaxSize()){

            if(active){
                OsmDroidMap(viewModel, modifier = Modifier.fillMaxSize(), alertCoords)
            }
            Column(modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.Bottom) {
                //MyRow("Latitude:", state.value.latitude.toString())
                //MyRow("Longitude:", state.value.longitude.toString())
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Box(modifier = Modifier.fillMaxWidth()){
                            Text(text = direction,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(androidx.compose.ui.Alignment.BottomStart))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(modifier = Modifier.fillMaxWidth()){

                            Text(
                                text = "$grados°",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.align(androidx.compose.ui.Alignment.BottomStart)
                            )
                        }
                        Text("Clima:${if (IsRainy) " lluvioso" else " soleado"}", fontSize = 15.sp, modifier = Modifier.padding(8.dp))
                    }

                }
                Text("Humedad: ${res.toString()}%", fontSize = 15.sp)
                Row(modifier = Modifier.fillMaxSize().padding(8.dp)){
                    Button(onClick = {val nAlCoord = GeoPoint(state.value.latitude, state.value.longitude)
                        alertCoords = alertCoords + nAlCoord}, modifier = Modifier.weight(12f), colors = ButtonDefaults.buttonColors
                        (containerColor = colorResource(buttoncolors))) { Text("Alerta") }

                    if(active) {
                        Button(onClick = {
                            stopLocationUpdates()
                            active = false
                        }, modifier = Modifier.weight(10f), colors = ButtonDefaults.buttonColors
                            (containerColor = colorResource(buttoncolors))) { Text("Stop", color = Color.Red ) }

                    }
                    else{
                        Button(onClick = {
                            startLocationUpdates()
                            active = true
                        },modifier = Modifier.weight(10f), colors =ButtonDefaults.buttonColors
                            (containerColor = colorResource(buttoncolors))) { Text("Start", color = Color.Green) }
                    }
                }
            }
        }

    }
    fun createLocationCallback(viewModel: LocationViewModel) : LocationCallback {
        val callback = object : LocationCallback(){
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                val location = result.lastLocation
                location?.let{
                    Log.i("LocationApp", location.latitude.toString())
                    Log.i("LocationApp", location.longitude.toString())
                    viewModel.update(location.latitude, location.longitude)
                }
            }
        }
        return callback
    }
    fun createLocationRequest() : LocationRequest {
        val request = LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setWaitForAccurateLocation(true)
            .build()
        return request
    }
    @Composable
    fun MyRow(label : String, value : String){
        Row(modifier = Modifier.padding(10.dp)){
            Text(label, color = Color.Blue, fontSize = 25.sp)
            Text(value,  fontSize = 25.sp)
        }
    }
    @Composable
    fun OsmDroidMap(viewModel: LocationViewModel, modifier: Modifier = Modifier, alertCoords: List<GeoPoint>) {
        val context = LocalContext.current
        var longClickLocation by remember { mutableStateOf<GeoPoint?>(null) }
        val state = viewModel.state.collectAsState()
        AndroidView(
            modifier = modifier,
            factory = {
                val mapView = MapView(context)
                mapView.setTileSource(TileSourceFactory.MAPNIK)
                mapView.setMultiTouchControls(true)
                mapView.controller.setZoom(12.0)
                mapView.controller.setCenter(GeoPoint(state.value.latitude, state.value.longitude))

                val startPoint = GeoPoint(state.value.latitude, state.value.longitude)

                val marker = Marker(mapView)
                marker.position = startPoint
                marker.title = "Tu posicion"
                mapView.tag = marker
                mapView.overlays.add(marker)
                val overlayEvents = MapEventsOverlay(object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                        return false
                    }

                    override fun longPressHelper(p: GeoPoint?): Boolean {
                        if (p != null) {
                            Log.i("MapsTest", "OSM Map Clicked" + p.toString())
                            longClickLocation = GeoPoint(p.latitude, p.longitude)
                        }
                        return true
                    }


                })
                mapView.overlays.add(overlayEvents)
                mapView.controller.setCenter(startPoint)
                mapView
            },
            update = { mapView ->

                val startPoint = GeoPoint(state.value.latitude, state.value.longitude)
                val marker = mapView.tag as Marker

                marker.position = startPoint


                alertCoords.forEach { coord ->
                    val alertMarker = Marker(mapView)
                    alertMarker.position = coord
                    alertMarker.title = "Alerta"
                    alertMarker.icon = context.getDrawable(android.R.drawable.ic_dialog_alert)
                    mapView.overlays.add(alertMarker)
                }
                longClickLocation?.let {
                    val longClickMarker = Marker(mapView)
                    longClickMarker.position = longClickLocation
                    longClickMarker.title = "Long Clicked Place"
                    mapView.overlays.add(longClickMarker)

                    val roadManager = OSRMRoadManager(context, "ANDROID")
                    val points = arrayListOf(startPoint, longClickLocation)
                    val road = roadManager.getRoad(points)
                    var roadOverlay = RoadManager.buildRoadOverlay(road)
                    roadOverlay?.getOutlinePaint()?.setStrokeWidth(10F)
                    mapView.overlays.add(roadOverlay)
                }
                mapView.invalidate()
                // This block runs on recomposition
            }
        )
    }
    fun getDirectionLetter(degrees: Float): String {
        return when (degrees) {
            in 337.5..360.0, in 0.0..22.5 -> "N"
            in 22.5..67.5 -> "NE"
            in 67.5..112.5 -> "E"
            in 112.5..157.5 -> "SE"
            in 157.5..202.5 -> "S"
            in 202.5..247.5 -> "SO"
            in 247.5..292.5 -> "O"
            in 292.5..337.5 -> "NO"
            else -> "?"
        }
    }


}

data class LocationState(val latitude : Double =0.0, val longitude : Double =0.0)
class LocationViewModel : ViewModel(){
    private val _uiState = MutableStateFlow(LocationState())
    val state : StateFlow<LocationState> = _uiState
    fun update(lat : Double, long : Double){
        _uiState.update { it.copy(lat, long) }
    }
}