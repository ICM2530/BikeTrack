package com.example.interfaces


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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
fun SeeFriendTrack(navController: NavHostController, name: String? = "examplefriendtrack") {
    var expanded by remember {mutableStateOf(false)}
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
            title = {Text("User#4456 Location", color = Color.White, fontWeight = Bold)},
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ){
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Share",
                    modifier = Modifier.size(50.dp),
                    tint = colorResource(R.color.MainMenuColor)
                )
                Text("Viewing friend location", fontSize = 25.sp, fontWeight = Bold, color = colorResource(R.color.MainMenuColor))
            }

            Box(
                contentAlignment = Alignment.TopStart
            ){
                Image(
                    painter = painterResource(R.drawable.templatemap),
                    contentDescription = "TemplateMap",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(400.dp).height(200.dp)
                )
            }
            HorizontalDivider(thickness = 10.dp, color = colorResource(R.color.MainMenuColor))
            Text("Friend User Information", fontSize = 25.sp, fontWeight = Bold, color = colorResource(R.color.MainMenuColor))
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,

                ){
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(start= 20.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Image(
                            painter = painterResource(R.drawable.mapwithpoints),
                            contentDescription = "user profile picture",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.size(60.dp)
                        )
                        Text("UserName", fontSize = 20.sp, fontWeight = Bold, color = colorResource(R.color.MainMenuColor))
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Share",
                            modifier = Modifier.size(50.dp),
                            tint = colorResource(R.color.MainMenuColor)
                        )
                        Text("Location Details", fontSize = 20.sp, fontWeight = Bold, color = colorResource(R.color.MainMenuColor))
                        Box(modifier = Modifier.padding(16.dp)){
                            IconButton(onClick = {expanded = !expanded}){
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Details about the location")
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false}
                            ) {
                                DropdownMenuItem(
                                    text = {Text("Initial location: Cra 51 #32 - 45")},
                                    onClick = {}
                                )
                                DropdownMenuItem(
                                    text = {Text("Final location: Cra 7 #45b - 32")},
                                    onClick = {}
                                )
                                DropdownMenuItem(
                                    text = {Text("Current location: Cra 7 #40 - 21")},
                                    onClick = {}
                                )
                                DropdownMenuItem(
                                    text = {Text("State: Moving")},
                                    onClick = {}
                                )
                            }
                        }
                    }
                }
            }
            HorizontalDivider(thickness = 10.dp, color = colorResource(R.color.MainMenuColor), modifier = Modifier.padding(top = 70.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(50.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Top,
            ) {
                Button(onClick={
                    navController.navigate(route = "${AppScreens.MainScreen.name}/$name")
                },
                    shape = RoundedCornerShape(30.dp),
                    colors=ButtonDefaults.buttonColors
                        (containerColor = colorResource(R.color.MainMenuColor)),
                    modifier = Modifier.padding(top = 25.dp).width(200.dp).height(100.dp)){
                    Text("Finish watching", fontSize = 25.sp, fontWeight=Bold, textAlign = TextAlign.Center)
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun FriendTrackPreview() {
    val navController = rememberNavController()
    SeeFriendTrack(navController)
}