package com.example.interfaces


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackView(navController: NavHostController, name: String? = "DefaultAct", acname : String? = "ActivityBasic") {
    var Speed by remember { mutableStateOf("20 km/h")}
    var time by remember { mutableStateOf("5:05")}
    var expanded1 by remember {mutableStateOf(false)}
    Scaffold (
        topBar={TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorResource(R.color.MainMenuColor),
            ),
            navigationIcon = {
                IconButton(onClick = {
                    navController.navigate(route = "${AppScreens.MainScreen.name}/$name")
                }, colors = IconButtonColors(
                    contentColor = Color.White,
                    containerColor = colorResource(R.color.MainMenuColor),
                    disabledContainerColor = Color.White,
                    disabledContentColor = Color.White
                )
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Returning to main menu")
                }
            },
            title = {Text("Current Activity", color = Color.White, fontWeight = Bold)},
            actions = {

                Box(){
                    IconButton(onClick = {
                        navController.navigate(route = "${AppScreens.chat.name}/$name")
                    }, colors = IconButtonColors(
                        contentColor = Color.White,
                        containerColor = colorResource(R.color.MainMenuColor),
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
                        containerColor = colorResource(R.color.MainMenuColor),
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
                            onClick = {navController.navigate(route = "${AppScreens.seefriendtrack.name}/$name")}
                        )
                    }
                }

            }
        )}

    ){ paddingValues ->

        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(2.dp)
        ) {


            Text("Activity: " + acname, fontSize = 35.sp, fontWeight = Bold, color = colorResource(R.color.MainMenuColor))
            Log.i("ACNames",acname.toString() + " " + name.toString())
            Box(contentAlignment = Alignment.TopStart,
                modifier = Modifier.padding(5.dp)){
                Image(
                    painter = painterResource(R.drawable.mapwithpoints),
                    contentDescription = "TemplateMap",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.width(500.dp).height(280.dp)
                )
                Card(modifier = Modifier.wrapContentWidth().height(60.dp),
                    shape = RoundedCornerShape(5.dp)){
                    Text("Initial location: Example address")
                    Text("Final location: Example address")
                    Text("Current location: Example address")
                }
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 5.dp,
                color = colorResource(R.color.MainMenuColor)
            )

            Text("Track Details", fontSize = 30.sp, fontWeight = Bold, color = colorResource(R.color.MainMenuColor))
            Text("Estimated time: 2h, 35m (3:40 pm")
            Text("Current weather: Clear, 17°C")
            Text("Initial location: Example address")
            Text("Final location: Example address")

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 5.dp,
                color = colorResource(R.color.MainMenuColor)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.Start),
                verticalAlignment = Alignment.Top,
            ) {

                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(10.dp)
                ){
                    Text("Current Speed", fontSize = 17.sp, fontWeight = Bold, color = colorResource(R.color.MainMenuColor))
                    Card(content =
                        {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                                Column(horizontalAlignment = Alignment.CenterHorizontally){
                                    Text(Speed, fontSize = 15.sp, fontWeight = Bold, color = colorResource(R.color.MainMenuColor))
                                }

                            }
                        }, modifier = Modifier.width(90.dp).height(40.dp),
                        shape = RoundedCornerShape(5.dp))


                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(10.dp)
                ){
                    Text("Time Remaining", fontSize = 17.sp, fontWeight = Bold, color = colorResource(R.color.MainMenuColor), textAlign = TextAlign.Center)
                    Card(content =
                        {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                                Column(horizontalAlignment = Alignment.CenterHorizontally){
                                    Text(time, fontSize = 15.sp, fontWeight = Bold, color = colorResource(R.color.MainMenuColor))
                                }

                            }
                        }, modifier = Modifier.width(90.dp).height(40.dp),
                        shape = RoundedCornerShape(5.dp))


                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(10.dp)
                ){
                    Text("Track", fontSize = 17.sp, fontWeight = Bold, color = colorResource(R.color.MainMenuColor))
                    Button(onClick={},
                        colors=ButtonDefaults.buttonColors
                            (containerColor = colorResource(R.color.MainMenuColor)),
                        modifier = Modifier.width(125.dp).height(35.dp)){
                        Text("Pause", fontSize = 12.sp, fontWeight=Bold)
                    }

                    Button(onClick={},
                        colors=ButtonDefaults.buttonColors
                            (containerColor = colorResource(R.color.MainMenuColor)),
                        modifier = Modifier.width(125.dp).height(35.dp)){
                        Text("Disable", fontSize = 12.sp, fontWeight=Bold)
                    }
                }
            }
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 2.dp,
                color = colorResource(R.color.MainMenuColor)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                verticalAlignment = Alignment.Top
            ){
                Button(onClick={
                    Speed = "0 km/h"
                    time = "00:00"
                },
                    colors=ButtonDefaults.buttonColors
                        (containerColor = colorResource(R.color.MainMenuColor)),
                    modifier = Modifier.width(125.dp).height(90.dp),
                    shape = RoundedCornerShape(15.dp)){
                    Text("End Track", fontSize = 12.sp, fontWeight=Bold)
                }
                Button(onClick={},
                    colors=ButtonDefaults.buttonColors
                        (containerColor = colorResource(R.color.MainMenuColor)),
                    modifier = Modifier.width(125.dp).height(90.dp),
                    shape = RoundedCornerShape(15.dp)){
                    Text("Show Current Location", fontSize = 12.sp, fontWeight=Bold)
                }
                Button(onClick={
                    navController.navigate(route = "${AppScreens.seeact.name}/$name")
                    Log.i("Moving to", "ActivityList")

                },
                    colors=ButtonDefaults.buttonColors
                        (containerColor = colorResource(R.color.MainMenuColor)),
                    modifier = Modifier.width(125.dp).height(90.dp),
                    shape = RoundedCornerShape(15.dp)){
                    Text("End Activity", fontSize = 12.sp, fontWeight=Bold)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TrackPreview() {
    val navController = rememberNavController()
    TrackView(navController)
}
