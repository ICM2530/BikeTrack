package com.example.interfaces.Screens


import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.interfaces.Navigation.AppScreens
import com.example.interfaces.R


@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun UserMenu(
    navController: NavHostController,
    name: String? = "UserSearched",
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
        buttoncolors = buttoncolors
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
                )
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Returning to main menu")
                }
            },
            title = {Text("User#1431 profile", color = Color.White, fontWeight = Bold)},
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
                    IconButton(onClick = {}, colors = IconButtonColors(
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
                }

            }
        )}

    ){
            paddingValues ->

        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(2.dp)
        ){
            Image(
                painter = painterResource(R.drawable.userbasic),
                contentDescription = "BasicUser",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.size(190.dp)
                    .clip(CircleShape)
            )
            Text(text = "User #1431", fontWeight = Bold, fontSize = 30.sp, color = colorResource(buttoncolors))
            HorizontalDivider(thickness = 5.dp, color = colorResource(buttoncolors))
            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Top,
            ){

                Icon(
                    Icons.Outlined.AccountBox,
                    contentDescription = "Share",
                    modifier = Modifier.size(50.dp),
                    tint = colorResource(buttoncolors)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text("Friends", fontWeight = Bold, fontSize = 30.sp, color = colorResource(buttoncolors))
                    Text(" 131 friends", fontSize = 20.sp)
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text("Followers", fontWeight = Bold, fontSize = 30.sp, color = colorResource(buttoncolors))
                    Text(" 1000 followers", fontSize = 20.sp)
                }
            }
            HorizontalDivider(thickness = 5.dp, color = colorResource(buttoncolors))
            Text(text = "Friends in common", fontWeight = Bold, fontSize = 30.sp, color = colorResource(
                buttoncolors))
            Text(text = "User 1431 has 2 friends in common with you!", fontSize = 10.sp, color = colorResource(
                buttoncolors))
            Row(
                horizontalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Top,
            ){
                Column {
                    Image(
                        painter = painterResource(R.drawable.userbasic),
                        contentDescription = "friends logo",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.size(60.dp)
                    )
                    Text(text = "User 4567", color = colorResource(textcolors))
                }
                Column {
                    Image(
                        painter = painterResource(R.drawable.userbasic),
                        contentDescription = "friends logo",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.size(60.dp)
                    )
                    Text(text = "User 2350", color = colorResource(textcolors))
                }

            }
            Text(text = "Activities", fontWeight = Bold, fontSize = 30.sp, color = colorResource(buttoncolors))
            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Top,
            ){
                Button(onClick={
                    navController.navigate(route = "${AppScreens.seeact.name}/$name/$im/${dAu}")
                },
                    colors=ButtonDefaults.buttonColors
                        (containerColor = colorResource(buttoncolors)),
                    modifier = Modifier.width(250.dp).height(80.dp)){
                    Text("See user activities", fontSize = 23.sp, fontWeight=Bold, textAlign = TextAlign.Center)
                }
            }

            Text("Recent publications", fontSize = 30.sp, fontWeight=Bold, color = colorResource(buttoncolors))

            val list = Array<String>(10){"Example publication"}
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically)
            ){
                items(list){title->
                    Button(onClick = {
                        navController.navigate(route = "${AppScreens.seepublic.name}/$name/$im/${dAu}")
                    },
                        modifier=Modifier.height(300.dp).width(300.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(pubcolors))

                    ){
                        Text(text = title, fontSize = 25.sp, color = colorResource(textcolors))
                    }

                }
            }
        }
    }
}

