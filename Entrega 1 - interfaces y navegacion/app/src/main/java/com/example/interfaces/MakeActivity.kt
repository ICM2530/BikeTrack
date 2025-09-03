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
fun MakeActivity(navController: NavHostController, name: String? = "User") {

    var AcName by remember { mutableStateOf("New Activity") }
    var type by remember { mutableStateOf("Casual") }
    var typedes by remember { mutableStateOf("Casual, no timers nor end times, enjoy the view and the trip") }
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
                )) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Returning to main menu")
                }
            },
            title = {Text(AcName, color = Color.White, fontWeight = Bold)},
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
                Text("Title", fontSize = 25.sp, fontWeight = Bold, color = colorResource(R.color.MainMenuColor))
                TextField(
                    value = AcName,
                    onValueChange = {AcName=it},
                    label = {Text("Activity name:", fontSize = 10.sp)},
                    modifier = Modifier.padding(all = 5.dp).height(60.dp).width(280.dp)
                )
            }

            Text("Activity Type", fontSize = 25.sp, fontWeight = Bold, color = colorResource(R.color.MainMenuColor))

            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,

            ){
                Button(onClick={
                    type = "Casual"
                },
                    colors=ButtonDefaults.buttonColors
                        (containerColor = colorResource(R.color.MainMenuColor)),
                    modifier = Modifier.width(100.dp).height(50.dp)){
                    Text("Casual", fontSize = 15.sp, fontWeight=Bold)
                }

                Button(onClick={
                    type = "Timed"
                },
                    colors=ButtonDefaults.buttonColors
                        (containerColor = colorResource(R.color.MainMenuColor)),
                    modifier = Modifier.width(100.dp).height(50.dp)){
                    Text("Timed", fontSize = 15.sp, fontWeight=Bold)
                }

                Button(onClick={
                    type = "Group"
                },
                    colors=ButtonDefaults.buttonColors
                        (containerColor = colorResource(R.color.MainMenuColor)),
                    modifier = Modifier.width(100.dp).height(50.dp)){
                    Text("Group", fontSize = 15.sp, fontWeight=Bold)
                }

                when(type){
                    "Casual" -> typedes = "Casual, no timers nor end times, enjoy the view and the trip"
                    "Timed" -> typedes = "Timed, against the clock, beat your previous time or create a new one"
                    "Group" -> typedes = "Group, friends invited, travel together and measure your times"
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Top,
            ) {
                Text("Description: ", fontWeight = Bold, textAlign = TextAlign.Center)
                Text(typedes, textAlign = TextAlign.Center)
            }
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 5.dp,
                color = colorResource(R.color.MainMenuColor)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                verticalAlignment = Alignment.Top,
            ) {
                Card(content =
                        {
                            Column(){
                                Text("Start time: 00:00")
                                Text("End time: 00:00")
                                Text("Initial location: Example address")
                                Text("Final location: Example address")
                            }
                        }, modifier = Modifier.width(200.dp).height(120.dp),
                    shape = RoundedCornerShape(5.dp)

                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(10.dp)
                ){
                    Text("Track", fontSize = 20.sp, fontWeight = Bold, color = colorResource(R.color.MainMenuColor))
                    Button(onClick={},
                        colors=ButtonDefaults.buttonColors
                            (containerColor = colorResource(R.color.MainMenuColor)),
                        modifier = Modifier.width(125.dp).height(35.dp)){
                        Text("Best Track", fontSize = 12.sp, fontWeight=Bold)
                    }

                    Button(onClick={},
                        colors=ButtonDefaults.buttonColors
                            (containerColor = colorResource(R.color.MainMenuColor)),
                        modifier = Modifier.width(125.dp).height(35.dp)){
                        Text("Custom Track", fontSize = 12.sp, fontWeight=Bold)
                    }
                }

            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 2.dp,
                color = colorResource(R.color.MainMenuColor)
            )
            Text("Track shown below", fontSize = 25.sp, fontWeight=Bold, color = colorResource(R.color.MainMenuColor))

            Box(
                contentAlignment = Alignment.TopStart
            ){
                Image(
                    painter = painterResource(R.drawable.templatemap),
                    contentDescription = "TemplateMap",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(400.dp).height(200.dp)
                )
                Card(content =
                    {
                        Column(){
                            Text("Initial location: Example address", color=(Color.White))
                            Text("Final location: Example address", color=(Color.White))
                            Text("Estimated time: 2h, 53min", color=(Color.White))
                        }
                    }, modifier = Modifier.width(200.dp).height(70.dp),
                    shape = RoundedCornerShape(2.dp),
                    colors = CardDefaults.cardColors(containerColor=Color.Gray)
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                verticalAlignment = Alignment.Top,
            ) {

                Button(onClick={
                    navController.navigate(route = "${AppScreens.seeact.name}/$name")
                },
                    colors=ButtonDefaults.buttonColors
                        (containerColor = colorResource(R.color.MainMenuColor)),
                    modifier = Modifier.width(130.dp).height(70.dp),
                    shape= RoundedCornerShape(25.dp)){
                    Text("Save and confirm", fontSize = 15.sp, fontWeight=Bold, textAlign = TextAlign.Center)
                }

                Button(onClick={
                    navController.navigate(route = "${AppScreens.seetrack.name}/$name/$AcName")
                },
                    colors=ButtonDefaults.buttonColors
                        (containerColor = colorResource(R.color.MainMenuColor)),
                    modifier = Modifier.width(130.dp).height(70.dp),
                    shape= RoundedCornerShape(25.dp)){
                    Text("Save and begin", fontSize = 15.sp, fontWeight=Bold, textAlign = TextAlign.Center)
                }

            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun MakeActivityPreview() {
    val navController = rememberNavController()
    MakeActivity(navController)
}