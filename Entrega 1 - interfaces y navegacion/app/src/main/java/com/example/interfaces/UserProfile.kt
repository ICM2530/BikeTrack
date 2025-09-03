package com.example.interfaces


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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun UserMenu(navController: NavHostController, name: String? = "UserSearched") {
    Scaffold(
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
            title = {Text("User#1431 profile", color = Color.White, fontWeight = Bold)},
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
                    IconButton(onClick = {}, colors = IconButtonColors(
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
            Text(text = "User #1431", fontWeight = Bold, fontSize = 30.sp, color = colorResource(R.color.MainMenuColor))
            HorizontalDivider(thickness = 5.dp, color = colorResource(R.color.MainMenuColor))
            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Top,
            ){
                /*Image(
                    painter = painterResource(R.drawable.userbasic),
                    contentDescription = "friends logo",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.size(60.dp)
                )*/
                Icon(
                    Icons.Outlined.AccountBox,
                    contentDescription = "Share",
                    modifier = Modifier.size(50.dp),
                    tint = colorResource(R.color.MainMenuColor)
                )
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
                    Text(" 1000 followers", fontSize = 20.sp)
                }
            }
            HorizontalDivider(thickness = 5.dp, color = colorResource(R.color.MainMenuColor))
            Text(text = "Friends in common", fontWeight = Bold, fontSize = 30.sp, color = colorResource(R.color.MainMenuColor))
            Text(text = "User 1431 has 2 friends in common with you!", fontSize = 10.sp, color = colorResource(R.color.MainMenuColor))
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
                    Text(text = "User 4567")
                }
                Column {
                    Image(
                        painter = painterResource(R.drawable.userbasic),
                        contentDescription = "friends logo",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.size(60.dp)
                    )
                    Text(text = "User 2350")
                }

            }
            Text(text = "Activities", fontWeight = Bold, fontSize = 30.sp, color = colorResource(R.color.MainMenuColor))
            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Top,
            ){
                Button(onClick={
                    navController.navigate(route = "${AppScreens.seeact.name}/$name")
                },
                    colors=ButtonDefaults.buttonColors
                        (containerColor = colorResource(R.color.MainMenuColor)),
                    modifier = Modifier.width(250.dp).height(80.dp)){
                    Text("See user activities", fontSize = 23.sp, fontWeight=Bold, textAlign = TextAlign.Center)
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
fun UserPreview() {
    val navController = rememberNavController()
    UserMenu(navController)
}