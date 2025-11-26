package com.example.interfaces.Screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AddAlert
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.interfaces.Navigation.AppScreens
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.maps.android.compose.*
import org.json.JSONObject
import java.io.InputStream
import java.lang.Math.toDegrees
import kotlin.collections.plus
import kotlin.math.roundToInt
import com.example.interfaces.R
import com.example.interfaces.ViewModel.AvailableUsersViewModel
import com.example.interfaces.ViewModel.LocationViewModel
import com.example.interfaces.ViewModel.MyAct
import com.example.interfaces.ViewModel.MyActivityViewModel
import com.example.interfaces.ViewModel.MyAlert
import com.example.interfaces.ViewModel.MyAlertViewModel
import com.example.interfaces.ViewModel.MyUserViewModel
import com.example.interfaces.ViewModel.UserStateMonViewModel
import com.example.interfaces.ViewModel.availabilityViewModel
import com.google.android.gms.maps.model.MapStyleOptions
import org.json.JSONArray
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.Date
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

private lateinit var geocoder: Geocoder

// Modelo de datos para las ubicaciones del JSON
data class Location(
    val name: String,
    val latitude: Double,
    val longitude: Double
)
data class SavePunto(
    val punto: LatLng = LatLng(0.0, 0.0),
    val titulo: String = ""
)




class SaveMarker(val titulo: String, val pt: LatLng){
    fun toJSON() : JSONObject {
        val obj = JSONObject()
        obj.put("Titulo", titulo)

        val inicioObj = JSONObject()
        inicioObj.put("latitude", pt.latitude)
        inicioObj.put("longitude", pt.longitude)

        obj.put("Punto",inicioObj)

        Log.i("MARKERSSAVED","Las coords en toJSON SaveMarker son $pt")

        return obj
    }
}

fun writeJSONMarker(ValuesPunto : SavePunto, context: Context) {
    //Nombre del archivo
    val filename = "SavedMarkers.json"

    val file = File(
        context.getExternalFilesDir(null), filename
    )

    //Se revisan los datos del archivo en caso de que ya exista, si no, envia un arreglo JSON vacio
    val elementosJSON = if(file.exists() && file.length().toInt() != 0){
        val texto: String = file.readText()
        JSONArray(texto)
    } else {
        JSONArray()
    }

    //Crea objeto SaveR con los datos de inicio del parametro y la fecha y hora actuales
    Log.i("MARKERSSAVED","Las coords en writeJSONMarker son ${ValuesPunto.punto}")

    val Ruta = SaveMarker(
        ValuesPunto.titulo,
        ValuesPunto.punto
    )

    elementosJSON.put(Ruta.toJSON())

    //Escribe los elementos en el archivo
    file.writeText(elementosJSON.toString())
}

fun loadMarkers(context: Context): MutableList<SavePunto> {
    val markers = mutableListOf<SavePunto>()
    val filename = "SavedMarkers.json"
    val file = File(context.getExternalFilesDir(null), filename)

    if (!file.exists() || file.length() == 0L) {
        return markers
    }

    try {
        val jsonText = file.readText().trim()
        if (jsonText.isEmpty()) {
            return markers
        }
        val jsonArray = JSONArray(jsonText)
        for (i in 0..jsonArray.length() - 1) {
            val jobject = jsonArray.getJSONObject(i)
            val titulo = jobject.getString("Titulo")
            val puntoObj = jobject.getJSONObject("Punto")
            val lat = puntoObj.getDouble("latitude")
            val lng = puntoObj.getDouble("longitude")
            markers.add(SavePunto(LatLng(lat, lng), titulo))
        }
    } catch (e: Exception) {
        Log.e("LOAD_MARKERS", "Error leyendo JSON: ${e.message}")
    }
    return markers
}

@Composable
fun TimerE(
    Distance: String,
    identification: String?,
    viewModel: MyActivityViewModel = viewModel(),
    TextC: Any,
    TextM: Any
) {
    var time by remember { mutableStateOf(0L) }

    var isRunning by remember { mutableStateOf(false) }

    var startTime by remember { mutableStateOf(0L) }

    val context = LocalContext.current

    val keyboardController = LocalSoftwareKeyboardController.current


    Row(modifier = Modifier.padding(15.dp),
        verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.Center)
    {
        Card(colors = CardColors(
            containerColor = Color.LightGray,
            contentColor = Color.DarkGray,
            disabledContentColor = Color.DarkGray,
            disabledContainerColor = Color.LightGray
        )){
            Text(formatTime(time), style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(8.dp))
        }

        Spacer(modifier = Modifier.width(5.dp))

        val formated = formatTime(time)
        Row{
            IconButton(onClick = {
                if(isRunning){
                    isRunning = false
                }
                else{
                    startTime = System.currentTimeMillis() - time
                    isRunning = true
                    keyboardController?.hide()
                }

            }, modifier = Modifier.size(40.dp)
                .size(40.dp).shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(12.dp),
                    clip = false
                ),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = colorResource(TextM as Int),
                contentColor = colorResource(TextC as Int)
            )){
                if(isRunning){
                    Icon(Icons.Default.Pause, "Pause Timer")
                }
                else{
                    Icon(Icons.Default.PlayArrow, "Continue Timer")
                }
            }

            Spacer(modifier = Modifier.width(5.dp))
            IconButton(onClick = {
                if(identification != null){

                    viewModel.saveAct(MyAct(identification, (100000..999999).random().toString(), formated, Distance))
                }
                time = 0
                isRunning = false

            }, modifier = Modifier

                .size(40.dp).shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(12.dp),
                    clip = false
                ),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = colorResource(TextM as Int),
                    contentColor = colorResource(TextC as Int)
                )){
                Icon(Icons.Default.Stop, "Pause Timer")
            }

        }
    }

    LaunchedEffect(isRunning) {
        while(isRunning){
            delay(1000)
            time = System.currentTimeMillis() - startTime
        }
    }
}

@Composable
fun formatTime(timeMi : Long): String{

    val hours = TimeUnit.MILLISECONDS.toHours(timeMi)
    val min = TimeUnit.MILLISECONDS.toMinutes(timeMi) % 60
    val sec = TimeUnit.MILLISECONDS.toSeconds(timeMi) % 60

    val StringReturned = "$hours:$min:$sec"
    return StringReturned
}


@Composable
fun MyBotBar(navController: NavHostController, TextC: Int, TextM: Int) {
    BottomAppBar(modifier = Modifier.height(80.dp).shadow(elevation = 10.dp, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)), containerColor = colorResource(TextM),tonalElevation = 6.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = {navController.navigate(AppScreens.profile.name)}, modifier = Modifier.padding(horizontal = 20.dp).size(50.dp)) {
                Icon(Icons.Outlined.AccountCircle, contentDescription = "Account")
            }
            IconButton(onClick = {}, modifier = Modifier.padding(horizontal = 20.dp).size(50.dp)) {
                Icon(Icons.Filled.Home, contentDescription = "Activity", tint = colorResource(TextC))
            }
            IconButton(onClick = {
                navController.navigate(AppScreens.Chats.name)
            }, modifier = Modifier.padding(horizontal = 20.dp).size(50.dp)) {
                Icon(Icons.Outlined.ChatBubbleOutline , contentDescription = "Chat")
            }
        }
    }
}
@SuppressLint("ServiceCast")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun Punto2Screen(
    navController: NavHostController,
    viewModel: LocationViewModel = viewModel(),
    AlviewModel: MyAlertViewModel = viewModel()
) {

    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser

    val availableVM: AvailableUsersViewModel = viewModel()
    val monitorVM: UserStateMonViewModel = viewModel()

    var menuExpanded by remember { mutableStateOf(false) }
    val availabilityVM: availabilityViewModel = viewModel()
    var isAvailable = availabilityVM.availability.collectAsState().value

    val statuspermiso = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    val statuspermisouubic = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)


    val database = FirebaseDatabase.getInstance()
    val usersRef = database.getReference("users")

    //FOR THE MAP
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val rotationSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) }
    val lightSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) }
    val pressureSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) }
    val humSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) }

    var azimuth by remember { mutableStateOf(0f) }
    var lux by remember { mutableStateOf(Float.MAX_VALUE) }
    var pressureVal by remember { mutableStateOf(0f) }
    var humidityVal by remember { mutableStateOf(0f) }
    var isRainy by remember { mutableStateOf(false) }
    val userAvailabilityVM: availabilityViewModel = viewModel()




    // rotation -> azimuth listener (update UI)
    val rotationListener = remember {
        object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    val orientationAngles = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)
                    val az = ((toDegrees(orientationAngles[0].toDouble()).toFloat() + 360f) % 360f)
                    azimuth = az
                }
            }
        }
    }
    val direction = getDirectionLetter(azimuth)
    val grados = azimuth.roundToInt()




    val pressureListener = remember {
        object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_PRESSURE) {
                    pressureVal = event.values[0]
                    isRainy = pressureVal < 1000f
                }
            }
        }
    }

    val humidityListener = remember {
        object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_RELATIVE_HUMIDITY) {
                    humidityVal = event.values[0]
                }
            }
        }
    }

    //--COLORES
    var MainColor by remember { mutableStateOf(R.color.white) }
    var MainTextcolor by remember { mutableStateOf(R.color.black) }

    //--------_

    val sensorLuz = remember{sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)}
    val sensorListener = remember {
        object : SensorEventListener{
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
                    val luz = event.values[0]

                    //Se define que estilo de mapa se usara
                    //ActualStyle = if() MapLight else MapDark
                    if(luz >= 2000){
                        MainColor = R.color.white
                        MainTextcolor = R.color.black
                    }else{
                        MainColor = R.color.MainMenuColor
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



    DisposableEffect(Unit) {
        // register
        rotationSensor?.let { sensorManager.registerListener(rotationListener, it, SensorManager.SENSOR_DELAY_GAME) }
        lightSensor?.let { sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_NORMAL) }
        pressureSensor?.let { sensorManager.registerListener(pressureListener, it, SensorManager.SENSOR_DELAY_NORMAL) }
        humSensor?.let { sensorManager.registerListener(humidityListener, it, SensorManager.SENSOR_DELAY_NORMAL) }

        onDispose {
            sensorManager.unregisterListener(rotationListener)
            sensorManager.unregisterListener(sensorListener)
            sensorManager.unregisterListener(pressureListener)
            sensorManager.unregisterListener(humidityListener)
        }
    }

    if(isAvailable==null){
        isAvailable = false
    }
    val previousState = remember { mutableStateMapOf<String, Boolean>() }
    LaunchedEffect(Unit) {
        availableVM.attachMonitor(monitorVM)
        statuspermisouubic.launchPermissionRequest()
    }
    LaunchedEffect(Unit) {
        availableVM.attachMonitor(monitorVM)
        statuspermiso.launchPermissionRequest()
    }




    if(statuspermisouubic.status.isGranted){
        startLocationUpdates(context, viewModel)

        Scaffold(
            topBar = {
            },
            bottomBar = { MyBotBar(navController, TextC = MainTextcolor, TextM = MainColor) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)

        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                // Mapa de Google Maps
                GoogleMapViewComposable(viewModel = viewModel, context = context, TextC = MainTextcolor, TextM = MainColor)

                // Indicador de estado de disponibilidad
                Row(modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(15.dp)){
                    Card(
                        modifier = Modifier
                            .padding(top=110.dp, start = 10.dp).shadow(elevation = 10.dp, shape = RoundedCornerShape(12.dp), clip = false),
                        onClick = { userAvailabilityVM.toggleAvailability() },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAvailable) Color(0xFF4CAF50) else Color(0xFF9E9E9E)
                        ),
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (isAvailable) Icons.Default.CheckCircle else Icons.Default.Person,
                                "Estado",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (isAvailable) "Disponible" else "No Disponible",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                }



            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .height(500.dp)
                        .padding(bottom = 100.dp, end = 260.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 20.dp)
                            .align(Alignment.BottomStart),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Bottom
                    ) {

                        Column(modifier = Modifier.padding(8.dp)) {

                            Box {
                                Text(
                                    text = direction,
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.BottomStart),
                                    color = colorResource(MainTextcolor)
                                )
                            }

                            Box {
                                Text(
                                    text = "$grados°",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.align(Alignment.BottomStart),
                                    color = colorResource(MainTextcolor)
                                )
                            }
                            Text(
                                "Clima: ${if (isRainy) "lluvioso" else "soleado"}",
                                fontSize = 15.sp,
                                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                                color = colorResource(MainTextcolor)
                            )

                            Text("Humedad: ${humidityVal}%", fontSize = 15.sp, color = colorResource(MainTextcolor))
                        }

                        Spacer(modifier = Modifier.width(150.dp))
                    }
                }
            }
        }
    }
    else{
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically)
                    ,modifier = Modifier.padding(paddingValues)){
                    Text("Alguno de los permisos solicitados no fue aceptado, intente de nuevo mas tarde...")
                    Button(onClick = {
                        firebaseAuth.signOut()
                        navController.navigate(AppScreens.Login.name)
                    }){
                        Text("Volver")
                    }
                }
            }
        }
    }

}

@Composable
fun GoogleMapViewComposable(
    viewModel: LocationViewModel,
    context: Context,
    viewModelAl: MyAlertViewModel = viewModel(),
    UviewModel: MyUserViewModel = viewModel(),
    TextC: Int,
    TextM: Int
) {
    val alertsList by viewModelAl.alerts.collectAsState()

    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    val users by UviewModel.users.collectAsState()
    val userinDB = users.find { it.mail.equals(currentUser?.email, ignoreCase = true)}

    val alerts = alertsList.map { alert ->
        MarkerAlert(
            LatLng(alert.latitude, alert.longitude),
            alert.Type
        )
    }


    var alertCoords by remember { mutableStateOf<List<MarkerAlert>>(emptyList()) }
    val locationState by viewModel.state.collectAsState()
    val currLoc = LatLng(locationState.latitude, locationState.longitude)
    var longClickMarker = rememberMarkerState(position = currLoc)
    var longClickMarkerTitle by remember {mutableStateOf("")}
    var locs by remember { mutableStateOf(ArrayList<LatLng>()) }
    var check by remember { mutableStateOf(false) }
    var markers by remember {mutableStateOf<MyMarker?>(null)}
    var routePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }

    val MapLight = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.light_style)
    }
    //Oscuro
    val MapDark = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.night_style)
    }

    var ActualStyle by remember { mutableStateOf(MapLight) }

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



    // Posición de la cámara centrada en la ubicación del usuario o Bogotá por defecto
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(
                if (locationState.latitude != 0.0) locationState.latitude else 4.6097,
                if (locationState.longitude != 0.0) locationState.longitude else -74.0817
            ),
            15f
        )
    }

    LaunchedEffect(routePoints) {
        if (routePoints.isNotEmpty()) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(routePoints.last(), 17f)
            )
        }
    }

    // Actualizar la cámara cuando cambie la ubicación
    LaunchedEffect(locationState.latitude, locationState.longitude) {
        if (locationState.latitude != 0.0 && locationState.longitude != 0.0) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(locationState.latitude, locationState.longitude),
                    15f
                )
            )
        }
    }
    writeJSONObject(context, latitude = locationState.latitude, longitude= locationState.longitude)
    var place by remember { mutableStateOf("") }
    var ruta by remember {mutableStateOf(PolyLine())}
    var markerSaveTitles by remember { mutableStateOf("") }

    var markerSavedLoc by remember { mutableStateOf(LatLng(0.0,0.0))}

    var expand by remember {mutableStateOf(false)}
    var expandt by remember {mutableStateOf(false)}

    var ListOfMarkers by remember{mutableStateOf(loadMarkers(context))}

    var Distance by remember{mutableStateOf("")}


    GoogleMap(

        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = false,
            mapStyleOptions = ActualStyle
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            myLocationButtonEnabled = false
        ),
        onMapLongClick = {position->
            routePoints = emptyList()
            longClickMarker.position = position
            val address = findAddress(context,position)
            address?.let{
                longClickMarkerTitle = address
            }
            markers = MyMarker(position, longClickMarkerTitle)
            Log.i("checkAddress", address.toString())
            val lats= position.latitude
            val longs = position.longitude
            Log.i("checkLats", lats.toString())
            Log.i("checkLongs", longs.toString())
            val distance = distance(locationState.latitude, locationState.longitude, lats, longs)
            Toast.makeText(context, "Distancia: ${distance} km", Toast.LENGTH_SHORT).show()
            loadFromURL(context, "${locationState.latitude},${locationState.longitude}", "${lats},${longs}", ruta){routePoints= PolyUtil.decode(ruta.pLine)}
            routePoints = PolyUtil.decode(ruta.pLine)
            Distance = "${distance}KM"
        }

    ) {
        if (locationState.latitude != 0.0 && locationState.longitude != 0.0) {
            Marker(
                state = MarkerState(position = LatLng(locationState.latitude, locationState.longitude)),
                title = "Mi Ubicación",
                snippet = "Lat: %.4f, Lng: %.4f".format(
                    locationState.latitude,
                    locationState.longitude
                ),
                icon = BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_CYAN
                ),
            )
        }
        for (m in alerts) {
            Marker(
                state = MarkerState(position = m.position),
                title = m.title,
                snippet =m.snippet
            )

        }
        markers?.let{m ->
            Log.i("checkMarker", m.toString())
            Marker(
                state = MarkerState(position = m.position),
                title = m.title,
                snippet = m.snippet,
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
            )

        }
        if (routePoints.isNotEmpty()) {
            Polyline(points = routePoints, color = Color.Magenta)
        }


    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically)
    ) {
        TextField(
            value = place,
            onValueChange = { place = it },
            label = { Text("address") },
            singleLine = true,
            shape = RoundedCornerShape(30.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                routePoints = emptyList()
                if (place.isNotEmpty()) {
                    val destination = findLocation(context,place)
                    Log.i("checkDestination", destination.toString())
                    if (destination != null) {
                        loadFromURL(
                            context,
                            "${locationState.latitude},${locationState.longitude}",
                            "${destination.latitude},${destination.longitude}",
                            ruta
                        ){
                            routePoints = PolyUtil.decode(ruta.pLine)
                        }
                        markers = MyMarker(destination, place)
                        routePoints = PolyUtil.decode(ruta.pLine)
                        val distance = distance(locationState.latitude, locationState.longitude, destination.latitude, destination.longitude)
                        Toast.makeText(context, "Distancia: ${distance} km", Toast.LENGTH_SHORT).show()
                        markerSaveTitles = place
                        markerSavedLoc = destination
                        Distance = "${distance}KM"
                        Log.i("MARKERSSAVED","Las coords son $markerSavedLoc")
                    }
                }
            },),
            modifier = Modifier.height(45.dp).width(480.dp).shadow(elevation = 10.dp, shape = RoundedCornerShape(12.dp), clip = false).padding(end = 15.dp),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color.Black,
                focusedLabelColor = Color.Gray,
                unfocusedLabelColor = Color.Gray
            )
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterHorizontally)
        ){


            Box(modifier = Modifier.padding(5.dp)){

                IconButton(onClick = { expand = true },
                    modifier = Modifier
                        .padding(start = 2.dp, end = 2.dp)
                        .size(50.dp).shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(12.dp),
                            clip = false
                        ),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = colorResource(TextM),
                        contentColor = colorResource(TextC)
                    )) {
                    Icon(Icons.Default.Save, contentDescription = "More options")
                }

                DropdownMenu(
                    expanded = expand,
                    onDismissRequest = {expand = false},
                    offset = DpOffset(x = (-100).dp, y = 0.dp)
                ) {
                    DropdownMenuItem(
                        text = {Text("")},
                        onClick={},
                        trailingIcon = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(9.dp, Alignment.Top),
                                modifier = Modifier.fillMaxSize()
                            ){
                                TextField(
                                    value = markerSaveTitles,
                                    onValueChange = { markerSaveTitles = it },
                                    label = { Text("Nombre de Marcador") },
                                    modifier = Modifier.
                                    padding(start = 5.dp, top = 2.dp, end = 5.dp),
                                    singleLine = true,
                                    shape = RoundedCornerShape(15.dp),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = colorResource(R.color.lightGF),
                                        unfocusedContainerColor = colorResource(R.color.lightGT),
                                        focusedLabelColor = Color.Black,
                                        unfocusedLabelColor = Color.Black,
                                        focusedTextColor = Color.White
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onSearch = {
                                            //Se crea locación en base a lo hallado de place por el geocoder
                                            val location = findLocation(context, place)
                                            //De no ser nula la locación
                                            location?.let {
                                                //Ajusta la posición al nuevo marcador
                                                cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 18F)

                                                val distance = distance(locationState.latitude, locationState.longitude, location.latitude, location.longitude)
                                                Toast.makeText(
                                                    context,
                                                    "Distancia: $distance KM",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                Distance = "${distance}KM"


                                                markers = MyMarker(location, markerSaveTitles)
                                                loadFromURL(
                                                    context,
                                                    "${locationState.latitude},${locationState.longitude}",
                                                    "${location.latitude},${location.longitude}",
                                                    ruta
                                                ){
                                                    routePoints = PolyUtil.decode(ruta.pLine)
                                                }

                                            }
                                        }
                                    )
                                )
                                IconButton(
                                    onClick = {
                                        Log.i("MARKERSSAVED","Las coords son $markerSavedLoc")
                                        if(markerSaveTitles != ""){
                                            val savedLoc : SavePunto = SavePunto(markerSavedLoc, markerSaveTitles)
                                            writeJSONMarker(savedLoc,context)
                                            ListOfMarkers = loadMarkers(context)
                                        }
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.Default.AddCircle, "Save Marker")
                                }
                            }

                        }

                    )
                }


            }

            Box(modifier = Modifier.padding(5.dp).size(80.dp)){

                IconButton(onClick = { expandt = true },
                    modifier = Modifier
                        .padding(start = 2.dp, end = 2.dp)
                        .size(50.dp).shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(12.dp),
                            clip = false
                        ),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = colorResource(TextM),
                        contentColor = colorResource(TextC)
                    )) {
                    Icon(Icons.Default.AddCircle, contentDescription = "More options")
                }

                DropdownMenu(
                    expanded = expandt,
                    onDismissRequest = {expandt = false},
                    offset = DpOffset(x = (-150).dp, y = 0.dp)
                ) {
                        Column(
                            modifier = Modifier
                                .height(150.dp).width(200.dp)
                                .verticalScroll(rememberScrollState())
                                .padding(start = 5.dp, top = 2.dp, end = 5.dp)
                        ) {
                            Text("Ubicaciones Guardadas")
                            ListOfMarkers.forEach { item ->
                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val markerpos = item.punto
                                            val address = findAddress(context, markerpos)
                                            address?.let {
                                                markers = MyMarker(item.punto, item.titulo, "")
                                                val resultado = distance(
                                                    locationState.latitude,
                                                    locationState.longitude,
                                                    markerpos.latitude,
                                                    markerpos.longitude
                                                )
                                                Toast.makeText(
                                                    context,
                                                    "Distancia: $resultado KM",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                Distance = "${resultado}KM"

                                                loadFromURL(
                                                    context,
                                                    "${locationState.latitude},${locationState.longitude}",
                                                    "${markerpos.latitude},${markerpos.longitude}",
                                                    ruta
                                                ){
                                                    routePoints = PolyUtil.decode(ruta.pLine)
                                                }

                                            }
                                            cameraPositionState.position =
                                                CameraPosition.fromLatLngZoom(item.punto, 18F)
                                            expandt = false
                                        }
                                ) {
                                    Text(item.titulo)
                                }
                            }
                        }
                }

            }

            TimerE(Distance, userinDB?.identification, TextC = TextC, TextM = TextM)

        }


    }
    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp)) {
            IconButton(
                onClick = { expanded = true },
                modifier = Modifier.size(70.dp).shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(12.dp),
                    clip = false
                ),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = colorResource(TextM),
                    contentColor = colorResource(TextC)
                )
            ) {
                Icon(
                    Icons.Outlined.AddAlert,
                    contentDescription = "Add Alert",
                    modifier = Modifier.size(40.dp),

                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {

                DropdownMenuItem(
                    text = { Text("Imprudencia") },
                    onClick = {
                        val nAlCoord = MarkerAlert(
                            LatLng(locationState.latitude, locationState.longitude),
                            "Imprudencia"
                        )
                        alertCoords = alertCoords + nAlCoord

                        val newAl = MyAlert(
                            Type = "Imprudencia",
                            latitude = locationState.latitude,
                            longitude = locationState.longitude,
                            identification = (100000..999999).random().toString()
                        )
                        viewModelAl.saveAlert(newAl)
                        expanded = false
                    }
                )

                DropdownMenuItem(
                    text = { Text("Peligro") },
                    onClick = {
                        val nAlCoord = MarkerAlert(
                            LatLng(locationState.latitude, locationState.longitude),
                            "Peligro"
                        )
                        alertCoords = alertCoords + nAlCoord
                        val newAl = MyAlert(
                            Type = "Peligro",
                            latitude = locationState.latitude,
                            longitude = locationState.longitude,
                            identification = (100000..999999).random().toString()
                        )
                        viewModelAl.saveAlert(newAl)
                        expanded = false
                    }
                )

                DropdownMenuItem(
                    text = { Text("Bloqueo") },
                    onClick = {
                        val nAlCoord = MarkerAlert(
                            LatLng(locationState.latitude, locationState.longitude),
                            "Bloqueo"
                        )
                        alertCoords = alertCoords + nAlCoord
                        val newAl = MyAlert(
                            Type = "Bloqueo",
                            latitude = locationState.latitude,
                            longitude = locationState.longitude,
                            identification = (100000..999999).random().toString(),
                        )
                        viewModelAl.saveAlert(newAl)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Obra") },
                    onClick = {
                        val nAlCoord =
                            MarkerAlert(LatLng(locationState.latitude, locationState.longitude), "Obra")
                        alertCoords = alertCoords + nAlCoord
                        val newAl = MyAlert(
                            Type = "Obra",
                            latitude = locationState.latitude,
                            longitude = locationState.longitude,
                            identification = (100000..999999).random().toString(),
                        )
                        viewModelAl.saveAlert(newAl)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Baches") },
                    onClick = {
                        val nAlCoord = MarkerAlert(
                            LatLng(locationState.latitude, locationState.longitude),
                            "Baches"
                        )
                        alertCoords = alertCoords + nAlCoord
                        val newAl = MyAlert(
                            Type = "Baches",
                            latitude = locationState.latitude,
                            longitude = locationState.longitude,
                            identification = (100000..999999).random().toString(),
                        )
                        viewModelAl.saveAlert(newAl)
                        expanded = false
                    }
                )
            }
        }

    }

}

fun loadLocationsFromJson(context: Context): List<Location> {
    return try {
        val inputStream: InputStream = context.assets.open("locations.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonString)
        val locations = mutableListOf<Location>()

        // Usar locationsArray del JSON
        val locationsArray = jsonObject.getJSONArray("locationsArray")

        for (i in 0 until locationsArray.length()) {
            val locationObj = locationsArray.getJSONObject(i)
            locations.add(
                Location(
                    name = locationObj.getString("name"),
                    latitude = locationObj.getDouble("latitude"),
                    longitude = locationObj.getDouble("longitude")
                )
            )
        }
        locations
    } catch (e: Exception) {
        emptyList()
    }
}

fun updateUserAvailability(isAvailable: Boolean,onResult: (Boolean?) -> Unit) {
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val email = firebaseUser?.email ?: return

    val database = FirebaseDatabase.getInstance()
    val usersRef = database.getReference("users")

    // Buscar el usuario por email
    usersRef.get().addOnSuccessListener { snapshot ->
        for (child in snapshot.children) {
            val mail = child.child("mail").getValue(String::class.java)
            if (mail == email) {


                val userId = child.key ?: continue

                usersRef.child(userId)
                    .child("available")
                    .setValue(isAvailable)
                    .addOnSuccessListener {

                        usersRef.child(userId)
                            .child("available")
                            .get()
                            .addOnSuccessListener { result ->
                                onResult(result.getValue(Boolean::class.java))
                            }
                    }
                    .addOnFailureListener {
                        onResult(null)
                    }

                return@addOnSuccessListener
            }
        }
        onResult(null)
    }
}


fun startLocationUpdates(context: Context, viewModel: LocationViewModel) {
    val locationClient = LocationServices.getFusedLocationProviderClient(context)

    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        5000L
    ).setWaitForAccurateLocation(true).setMinUpdateDistanceMeters(20f).build()

    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val email = firebaseUser?.email

    if (email == null) {
        Log.e("Location", "No hay usuario autenticado")
        return
    }

    val database = FirebaseDatabase.getInstance()
    val usersRef = database.getReference("users")

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { location ->
                viewModel.update(location.latitude, location.longitude)

                usersRef.get().addOnSuccessListener { snapshot ->
                    for (child in snapshot.children) {
                        val mail = child.child("mail").getValue(String::class.java)
                        if (mail == email) {

                            val userId = child.key

                            usersRef.child("$userId/latitude").setValue(location.latitude)
                            usersRef.child("$userId/longitude").setValue(location.longitude)

                            break
                        }
                    }
                }
            }
        }
    }

    // --- permisos ---
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        locationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
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
data class MyMarker(val position: LatLng,
                    val title: String = "Marker", val snippet: String ="")
class MyLocation(val date: Date, val latitude: Double, val longitude: Double){
    fun toJSON(): JSONObject {
        val obj = JSONObject();
        obj.put("latitude", latitude)
        obj.put("longitude", longitude)
        obj.put("date", date)
        return obj
    }
}
fun writeJSONObject(context: Context,latitude: Double, longitude: Double){
    val myLocation = MyLocation(
        Date(System.currentTimeMillis()),
        latitude,
        longitude
    )


    val filename = "locations.json"
    val file = File(context.getExternalFilesDir(null), filename)
    val locations = if(file.exists()){
        JSONArray(file.readText())
    }
    else{
        JSONArray()
    }
    locations.put(myLocation.toJSON())
    val output = BufferedWriter(FileWriter(file))
    output.write(locations.toString())
    Log.i("LOCATION", "File modified at path: " + file)
    output.close()

}


fun findAddress (context: Context,location : LatLng):String?{
    geocoder = Geocoder(context)
    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 2)
    if(addresses != null && !addresses.isEmpty()){
        val addr = addresses.get(0)
        val locname = addr.getAddressLine(0)
        return locname
    }
    return null
}
fun findLocation(context:Context,address : String):LatLng?{
    geocoder = Geocoder(context)
    val addresses = geocoder.getFromLocationName(address, 2)
    if(addresses != null && !addresses.isEmpty()){
        val addr = addresses.get(0)
        val location = LatLng(addr.
        latitude, addr.
        longitude)
        return location
    }
    return null
}
data class PolyLine(var pLine: String="")

data class MarkerAlert(val position: LatLng, val title: String = "Marker", val snippet: String ="")
fun loadRoadHistory(context : Context) : ArrayList<LatLng> {
    val coordinates = ArrayList<LatLng>()
    val filename = "locations.json"
    val file = File(context.getExternalFilesDir(null), filename)
    val jsonString = file.readText()
    val coordsArray = JSONArray(jsonString)
    for (i in 0..coordsArray.length()-1) {
        val jsonObject = coordsArray.getJSONObject(i)
        val lat = jsonObject.getDouble("latitude")
        val long = jsonObject.getDouble("longitude")
        val point = LatLng(lat, long)
        coordinates.add(point)
    }
    return coordinates
}
fun loadFromURL(context: Context, origin: String, destination: String, ruta: PolyLine, rutaR: (String)->Unit) {
    val queue = Volley.newRequestQueue(context)
    var url = "https://maps.googleapis.com/maps/api/directions/json?"
    val origen = "origin=${origin}"
    val destino = "&destination=${destination}"
    val key = "&key=AIzaSyAyx-zXTIrl3QZcIpgMr6KYsyvJpxsnT48"
    Log.i("urlCheck", url + origen + destino + key)
    val req = StringRequest(url + origen + destino + key, {
        val data = it
        Log.i("RESTGM", data)
        var json = JSONObject(data)
        var jsonRutas = json.getJSONArray("routes")
        Log.i("RESTGM-Routes", jsonRutas.toString())
        for (i in 0..jsonRutas.length() - 1) {
            val jsonObjectRutas = jsonRutas.getJSONObject(i)
            Log.i("RESTGM-Rutas", jsonObjectRutas.toString())
            val jsonObjectPolyL = jsonObjectRutas.getJSONObject("overview_polyline")
            Log.i("RESTGM-PolyL", jsonObjectPolyL.toString())
            val polyLinePoints = jsonObjectPolyL.getString("points")
            Log.i("RESTGM-Points", polyLinePoints.toString())
            ruta.pLine = polyLinePoints.toString()
            rutaR(polyLinePoints.toString())
        }
    }, {
        Log.e("RESTGM", "Error in Google Maps API Request ${it.cause}")
    })
    queue.add(req)
}
