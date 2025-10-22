package com.example.interfaces.Screens

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(
    navController: NavHostController,
    name: String? = "User",
    im: String?,
    dAu: Boolean?
) {
    val context = LocalContext.current
    var ScreenColor by remember { mutableStateOf(R.color.white) }
    var buttoncolors by remember { mutableStateOf(R.color.MainMenuColor) }
    var textcolors by remember { mutableStateOf(R.color.black) }
    var pubcolors by remember { mutableStateOf(R.color.Dark) }
    var AutoturnDark by remember { mutableStateOf(false) }


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

    var expanded1 by remember {mutableStateOf(false)}
    Scaffold (
        containerColor = colorResource(ScreenColor),
        topBar={TopAppBar(
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
                )) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Returning to main menu")
                }
            },
            title = {Text("Activities", color = Color.White, fontWeight = Bold)},
            actions = {

                Box(){
                    IconButton(onClick = {
                        navController.navigate(route = "${AppScreens.chat.name}/$name/$im/${dAu}")
                    }, colors = IconButtonColors(
                        contentColor = Color.White,
                        containerColor = colorResource(buttoncolors),
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.White
                    ) )
                    { Icon(Icons.Default.Person, "Friends") }

                    Badge(
                        modifier = Modifier.offset(x = 8.dp, y = (10).dp), // Ajusta la posición del badge
                        containerColor = Color.Red
                    ) {
                        Text("50", color = Color.White, fontSize = 10.sp)
                    }
                }

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
        )}
    ){paddingValues ->

        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(paddingValues)
        )
        {
            Text("Activities", fontSize = 30.sp, fontWeight = Bold, color = colorResource(buttoncolors))
            Text("Total Activities: 110", fontSize = 20.sp, fontWeight = Bold, color = colorResource(textcolors))
            Text("Activities completed: 41", modifier = Modifier.align(Alignment.CenterHorizontally), color = colorResource(textcolors))
            Text("Activities in progress: 20", modifier = Modifier.align(Alignment.CenterHorizontally), color = colorResource(textcolors))
            Text("Activities created: 124", modifier = Modifier.align(Alignment.CenterHorizontally), color = colorResource(textcolors))
            Text("Activities deleted: 14", modifier = Modifier.align(Alignment.CenterHorizontally), color = colorResource(textcolors))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 5.dp,
                color = colorResource(buttoncolors)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(50.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier=Modifier.fillMaxWidth().padding(10.dp)
            ) {
                Text("Name", fontWeight = Bold, fontSize = 20.sp, color = colorResource(textcolors))
                Text("Activity type", fontWeight = Bold, fontSize = 20.sp, color = colorResource(textcolors))
                Text("State", fontWeight = Bold, fontSize = 20.sp, color = colorResource(textcolors))
            }

            Card(
                modifier = Modifier.fillMaxWidth().height(450.dp),
                shape = RoundedCornerShape(5.dp),
                colors = CardDefaults.cardColors(containerColor= colorResource(pubcolors))
            ){
                val list = Array<String>(110){i -> "${i + 1}"}

                LazyColumn(
                    Modifier.fillMaxWidth().padding(5.dp)
                ) {
                    items(list) { item ->
                        var randomValueType = (1..3).random()
                        var Type = "Casual"
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(
                                20.dp,
                                Alignment.CenterHorizontally
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(5.dp)
                        )
                        {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(
                                    1.dp,
                                    Alignment.CenterHorizontally
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(1.dp).weight(1f)
                            ){
                                Text(item.toString(), fontSize = 18.sp, color = colorResource(textcolors))
                                Text(". Activity" + item.toString(), fontSize = 18.sp,modifier = Modifier
                                    .weight(1f), color = colorResource(textcolors))
                            }

                            when(randomValueType){
                                1->Type = "Casual"
                                2->Type = "Timed"
                                3->Type = "Grouped"
                            }

                            Text(Type, fontSize = 18.sp,modifier = Modifier
                                .weight(0.7f), color = colorResource(textcolors))
                            Text("On Progress", fontSize = 18.sp,modifier = Modifier
                                .weight(1f), color = colorResource(textcolors))
                        }
                    }

                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {

                Button(onClick={
                    navController.navigate(route = "${AppScreens.makeact}/$name/$im/${dAu}")
                },
                    colors=ButtonDefaults.buttonColors
                        (containerColor = colorResource(buttoncolors)),
                    modifier = Modifier.width(180.dp).height(80.dp)){
                    Text("Open activity", fontSize = 15.sp, fontWeight=Bold)
                }

            }

        }

    }


}

