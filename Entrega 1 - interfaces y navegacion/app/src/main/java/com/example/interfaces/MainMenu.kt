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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.interfaces.Navigation.AppScreens


@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun MainMenu(navController: NavHostController, name: String? = "TemplateUser") {
    Scaffold(
        topBar={TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorResource(R.color.MainMenuColor),
            ),
            title = {Text("Home", color = Color.White, fontWeight = Bold)},
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
                        if(expanded){
                            expanded = false
                        }
                        else{
                            expanded = true
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
                        expanded = expanded,
                        onDismissRequest = { expanded = false}
                    ) {
                        DropdownMenuItem(
                            text = {Text("Friend User4456 has sent a tracker to you")},
                            onClick = {navController.navigate(route = "${AppScreens.seefriendtrack.name}/$name")}
                        )
                    }
                }

            }
        )}

    ){
            paddingValues ->

        Column(
            verticalArrangement = Arrangement.spacedBy(18.dp, Alignment.Top),
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
            Text(text = "Hello " + name + "!", fontWeight = Bold, fontSize = 30.sp, color = colorResource(R.color.MainMenuColor))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 5.dp,
                color = colorResource(R.color.MainMenuColor)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Top,
            ){
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text("Friends", fontWeight = Bold, fontSize = 30.sp, color = colorResource(R.color.MainMenuColor))
                    Text(" 131 friends", fontSize = 20.sp)
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text("Followers", fontWeight = Bold, fontSize = 30.sp, color = colorResource(R.color.MainMenuColor))
                    Text(" 200 followers", fontSize = 20.sp)
                }
            }

            Text(text = "Activities", fontWeight = Bold, fontSize = 30.sp, color = colorResource(R.color.MainMenuColor))
            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Top,
            ){
                Button(onClick={
                    navController.navigate(route = "${AppScreens.makeact.name}/$name")
                },
                    colors=ButtonDefaults.buttonColors
                        (containerColor = colorResource(R.color.MainMenuColor)),
                    modifier = Modifier.width(180.dp).height(80.dp)){
                    Text("Make new Activity", fontSize = 15.sp, fontWeight=Bold)
                }
                Button(onClick={
                    navController.navigate(route = "${AppScreens.seeact.name}/$name")
                },
                    colors=ButtonDefaults.buttonColors
                        (containerColor = colorResource(R.color.MainMenuColor)),
                    modifier = Modifier.width(180.dp).height(80.dp), ){
                    Text("See my activities", fontSize = 15.sp, fontWeight=Bold)
                }
            }

            Text("Recent publications", fontSize = 30.sp, fontWeight=Bold, color = colorResource(R.color.MainMenuColor))

            val list = Array<String>(10){"Example publication"}
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically)
            ){
                items(list){title->
                    Button(onClick = {
                        navController.navigate(route = "${AppScreens.seepublic.name}/$name")
                    },
                        modifier=Modifier.height(300.dp).width(300.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)

                    ){
                        Text(text = title, fontSize = 25.sp)
                    }

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuPreview() {
    val navController = rememberNavController()
    MainMenu(navController)
}