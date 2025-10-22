package com.example.interfaces.Screens


import android.annotation.SuppressLint
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.interfaces.Navigation.AppScreens
import com.example.interfaces.R



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat(navController: NavHostController, name: String? = "BasicChats", im: String?, dAu: Boolean?) {
    var expanded1 by remember {mutableStateOf(false)}
    var AutoturnDark by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var ScreenColor by remember { mutableStateOf(R.color.white) }
    var buttoncolors by remember { mutableStateOf(R.color.MainMenuColor) }
    var textcolors by remember { mutableStateOf(R.color.black) }
    var pubcolors by remember { mutableStateOf(R.color.Dark) }



    val sensorManager = remember {
        context.getSystemService(SENSOR_SERVICE) as SensorManager
    }
    val lightSensor = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }




    val sensorListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
                    val lux = event.values[0]
                    if(dAu == true) {
                        AutoturnDark = if (lux < 2000) true else false
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }



    if(dAu == true && AutoturnDark){
        buttoncolors = R.color.DarkButtons
        ScreenColor = R.color.Dark
        textcolors = R.color.white
        pubcolors = R.color.Dark
    }
    else{
        buttoncolors = R.color.MainMenuColor
        ScreenColor = R.color.white
        textcolors = R.color.black
        pubcolors = R.color.BackLight
    }




    DisposableEffect(Unit) {
        sensorManager.registerListener(sensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    Scaffold(
        containerColor = colorResource(ScreenColor),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(buttoncolors),
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(route = "${AppScreens.MainScreen.name}/defaultUser/default/default/${dAu}")
                    }, colors = IconButtonColors(
                        contentColor = Color.White,
                        containerColor = colorResource(buttoncolors),
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.White
                    )
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Returning to main menu")
                    }
                },
                title = {Text("Chats", color = Color.White, fontWeight = Bold)},
                actions = {


                    Box(){
                        var expanded by remember { mutableStateOf(false) }
                        IconButton(onClick = {
                            if(expanded1){
                                expanded1 = false
                            }
                            else{
                                expanded1 = true
                            }

                        }, colors = IconButtonColors(
                            contentColor = Color.White,
                            containerColor = colorResource(buttoncolors),
                            disabledContainerColor = Color.White,
                            disabledContentColor = Color.White
                        ) )
                        { Icon(Icons.Default.Notifications, "Friends") }

                        Badge(
                            modifier = Modifier.offset(x = 8.dp, y = (10).dp), // Ajusta la posición del badge
                            containerColor = Color.Red
                        ) {
                            Text("15", color = Color.White, fontSize = 10.sp)
                        }

                        DropdownMenu(
                            expanded = expanded1,
                            onDismissRequest = { expanded1 = false}
                        ) {
                            DropdownMenuItem(
                                text = {Text("Friend User4456 has sent a tracker to you")},
                                onClick = {navController.navigate(route = "${AppScreens.seefriendtrack.name}/$name/$im/${dAu}")}
                            )
                        }
                    }

                }
            )
        }
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(2.dp)
        ) {

            Row(Modifier.fillMaxSize()) {


                Column(
                    modifier = Modifier
                        .width(150.dp) // ancho fijo
                        .fillMaxHeight()
                        .background(color=colorResource(buttoncolors))
                ) {
                    Text(
                        "Friends",
                        color = Color.White,
                        fontWeight = Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                    //Lista para armar los falsos amigos
                    val users = (1..50).map { "User #$it" } // lista de prueba
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(users) { user ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { }
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(user, color = Color.White)
                            }
                        }
                    }
                }

                Column(

                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .background(colorResource(pubcolors)),

                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("User #1", fontWeight = Bold)
                    }

                    LazyColumn(

                        modifier = Modifier

                            .padding(8.dp)
                    ) {
                        items(listOf("Hola", "¿Lo completaste?", "Me avisas")) { msg ->
                            Text(
                                text = msg,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .background(
                                        Color(0xFFDADADA),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp)
                            )
                        }
                    }
                    LazyColumn(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        items(listOf("Hola", "¿Cómo estás?", "Si, ya esta", "¡Acabamos!")) { msg ->
                            Text(
                                text = msg,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .background(
                                        color = colorResource(buttoncolors),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

