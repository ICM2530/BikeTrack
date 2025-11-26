package com.example.interfaces.Screens

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.interfaces.Navigation.AppScreens
import com.example.interfaces.R
import com.example.interfaces.ViewModel.MyActivityViewModel
import com.example.interfaces.ViewModel.MyUserViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MyBotBar2(navController: NavHostController, MainColo: Int, MainTextcolo: Int) {
    BottomAppBar(modifier = Modifier.height(80.dp).shadow(elevation = 10.dp, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)), containerColor = colorResource(MainColo),tonalElevation = 6.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = {}, modifier = Modifier.padding(horizontal = 20.dp).size(50.dp)) {
                Icon(Icons.Filled.AccountCircle, contentDescription = "Account", tint = colorResource(MainTextcolo))
            }
            IconButton(onClick = {navController.navigate(AppScreens.punto2.name)}, modifier = Modifier.padding(horizontal = 20.dp).size(50.dp)) {
                Icon(Icons.Outlined.Home, contentDescription = "Activity")
            }
            IconButton(onClick = {navController.navigate(AppScreens.Chats.name)}, modifier = Modifier.padding(horizontal = 20.dp).size(50.dp)) {//parte de Alex para implementar el chat
                Icon(Icons.Outlined.ChatBubbleOutline , contentDescription = "Chat")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    userName: String = "John Doe",
    activities: List<String> = listOf("Activity 1", "Activity 2", "Activity 3"),
    viewModel : MyUserViewModel = viewModel(),
    AviewModel : MyActivityViewModel = viewModel()
) {
    var UserAct by remember { mutableStateOf("") }
    val statuspermiso = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    val users by viewModel.users.collectAsState()
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    val context = LocalContext.current
    val userinDB = users.find { it.mail.equals(currentUser?.email, ignoreCase = true)}
    var actividades = AviewModel.acts.collectAsState()
    val userAct = actividades.value.filter { it.User == userinDB?.identification }





    Log.i("USER IS IN DB", "Usuario ${currentUser?.email} tiene de nombre ${userinDB}")
    var name by remember { mutableStateOf("TemplateUSer") }

    if (userinDB != null) {
        name = userinDB.name + " " + userinDB.lastName
    } else {
        name = "TemplateUser"
    }

    //--COLORES
    var MainColor by remember { mutableStateOf(R.color.white) }
    var MainTextcolor by remember { mutableStateOf(R.color.black) }
    var MainMarkColor by remember { mutableStateOf(R.color.white) }
    var MainBackColor by remember { mutableStateOf(R.color.white) }
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
                        MainMarkColor = R.color.MainMenuColor
                        MainBackColor = R.color.white
                    }else{
                        MainColor = R.color.MainMenuColor
                        MainTextcolor = R.color.white
                        MainMarkColor = R.color.white
                        MainBackColor = R.color.Dark
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
        statuspermiso.launchPermissionRequest()
    }

    Scaffold(
        containerColor = colorResource(MainBackColor),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.MainMenuColor),
                    titleContentColor = Color.White
                ),
                title = { Text("Profile", style = MaterialTheme.typography.titleLarge, color = colorResource(R.color.white)) },
                navigationIcon = {
                    IconButton(onClick = {
                        firebaseAuth.signOut()
                        navController.navigate(AppScreens.Login.name) {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Sign Out", tint = colorResource(R.color.white))
                    }
                }
            )
        },
        bottomBar = { MyBotBar2(navController, MainColor, MainMarkColor) },

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp).align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(MainColor)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(){
                        if(userinDB?.uri != null && userinDB.uri.toString().startsWith("https://firebasestorage")){
                            Image(
                                painter = rememberAsyncImagePainter(userinDB.uri),
                                "Profile Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(100.dp).clip(CircleShape)
                            )
                        }
                        else{
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = "Profile Photo",
                                modifier = Modifier.size(100.dp),
                                tint = Color(0xFF1976D2)
                            )
                        }

                        IconButton(
                            onClick = {
                                navController.navigate("${AppScreens.editProfile.name}/${userinDB?.identification}")
                            }
                            ,modifier = Modifier.size(30.dp).shadow(
                                elevation = 10.dp,
                                shape = RoundedCornerShape(12.dp), clip = false
                            ),

                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(0xFFFFFFFF),
                                contentColor = colorResource(R.color.black)
                            )

                        ) {
                            Icon(Icons.Default.AddAPhoto, "Edición de perfil")
                        }
                    }


                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(top = 8.dp),
                        color = colorResource(MainTextcolor)
                    )

                    Spacer(modifier = Modifier.height(12.dp))


                    Button(
                        onClick = { navController.navigate(AppScreens.AvailableUsers.name) },
                        modifier = Modifier.fillMaxWidth(0.6f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonColors(
                            containerColor = colorResource(MainMarkColor),
                            contentColor = colorResource(MainColor),
                            disabledContainerColor = colorResource(MainMarkColor),
                            disabledContentColor = colorResource(MainColor)
                        )
                    ) {
                        Icon(Icons.Default.Group, contentDescription = "Friends", tint= colorResource(MainColor))
                        Spacer(Modifier.width(8.dp))
                        Text("Friends", color = colorResource(MainColor))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Activities List Header ---
            Text(
                text = "Activities",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 4.dp),
                color = colorResource(MainTextcolor)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // --- Lazy List of User Activities ---
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(userAct) { activity ->
                    var string = "Time: ${activity.time} - Distance: ${activity.distance}"
                    ActivityCard(string)
                }
            }
        }
    }
}

@Composable
fun ActivityCard(activity: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = activity,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

