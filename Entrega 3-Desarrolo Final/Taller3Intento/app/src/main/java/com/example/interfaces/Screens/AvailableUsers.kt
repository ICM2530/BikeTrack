package com.example.interfaces.Screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.interfaces.Navigation.AppScreens
import com.example.interfaces.R
import com.example.interfaces.Services.Punto5.Companion.sendUserAvailableNotification
import com.example.interfaces.ViewModel.AvailableUsersViewModel
import com.example.interfaces.ViewModel.MyUser
import com.example.interfaces.ViewModel.MyUserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// ============================================
// VISTA 1: LISTA DE USUARIOS DISPONIBLES
// ============================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailableUsersListScreen(
    navController: NavHostController,
    viewModel: AvailableUsersViewModel = viewModel()
) {
    val context = LocalContext.current

    // Real-time available users provided by the ViewModel
    val availableUsers by viewModel.availableUsers.collectAsState()

    // To detect changes in availability and notify user
    val previousState = remember { mutableStateMapOf<String, Boolean>() }


    // Dark mode configuration
    var screenColor by remember { mutableStateOf(R.color.white) }
    var buttonColors by remember { mutableStateOf(R.color.MainMenuColor) }
    var textColors by remember { mutableStateOf(R.color.black) }
    var cardColors by remember { mutableStateOf(R.color.white) }

    //--COLORES
    var MainColor by remember { mutableStateOf(R.color.white) }
    var MainTextcolor by remember { mutableStateOf(R.color.black) }

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
                    //ActualStyle = if() MapLight else MapDark
                    if(luz >= 2000){
                        MainColor = R.color.white
                        MainTextcolor = R.color.black
                        cardColors = R.color.white
                    }else{
                        MainColor = R.color.MainMenuColor
                        MainTextcolor = R.color.white
                        cardColors = R.color.lightGF
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


    Scaffold(
        containerColor = colorResource(MainColor),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(MainColor),
                ),
                title = {
                    Text(
                        "Available Users",
                        color = colorResource(MainTextcolor),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = colorResource(MainTextcolor)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (availableUsers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "No users",
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Text(
                        "No hay usuarios",
                        color = colorResource(MainTextcolor),
                        fontSize = 18.sp
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Text(
                    text = "Available Users",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(MainColor),
                    modifier = Modifier.padding(16.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(availableUsers) { user ->
                        AvailableUserCard(
                            user = user,
                            navController = navController,
                            buttonColors = MainColor,
                            cardColors = cardColors,
                            textColors = MainTextcolor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AvailableUserCard(
    user: MyUser,
    navController: NavHostController,
    buttonColors: Int,
    cardColors: Int,
    textColors: Int
) {
    val context= LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(cardColors))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen de perfil
            if (user.uri.isNotEmpty() && user.uri != "default") {
                val changed = user.uri.replace('L', '/').replace('A', ':').replace('M', '_')
                val uriD = changed.toUri()
                Image(
                    painter = rememberAsyncImagePainter(uriD),
                    contentDescription = "Profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.userbasic),
                    contentDescription = "Profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información del usuario
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${user.name} ${user.lastName}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(textColors)
                )
                Text(
                    text = user.mail,
                    fontSize = 13.sp,
                    color = colorResource(textColors)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.Green)
                    )
                    Text(
                        text = "Available Now",
                        fontSize = 12.sp,
                        color = colorResource(buttonColors),
                        fontWeight = FontWeight.Medium
                    )
                }
            }


            Button(
                onClick = {
                    // Navegar a TrackUser con el email del usuario
                    val intent = Intent(context, TrackUserAct::class.java)
                    intent.putExtra("trackedMail",user.mail)
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.MainMenuColor)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .width(90.dp)
                    .height(60.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "View Location",
                        modifier = Modifier.size(18.dp)
                    )
                    Text("View", fontSize = 12.sp)
                }
            }
        }
    }
}