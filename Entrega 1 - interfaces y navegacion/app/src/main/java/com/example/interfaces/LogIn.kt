package com.example.interfaces

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.interfaces.Navigation.AppScreens

@Composable
fun LoginScreen(navController: NavHostController) {
    val nameloginCheck = "User1"
    val paswdLogin = "HelloWorld1"

    var Name by remember { mutableStateOf("") }
    var pswd by remember { mutableStateOf("") }

    var ERROR by remember { mutableStateOf("") }
    Scaffold()

    {paddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ){
            Card(
                modifier = Modifier.width(370.dp).height(500.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor= colorResource(R.color.MainMenuColor))
            ){
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier =Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                        .padding(paddingValues)
                ) {
                    Text("Login", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)

                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                    ) {
                        Box(Modifier
                            .size(50.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                        )
                        {
                            Icon(Icons.Default.Person, "UserIconSmall",
                                tint = Color.Black,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        TextField(
                            value = Name,
                            onValueChange = {Name = it},
                            label = {Text("UserName")},
                            modifier = Modifier.padding(all = 5.dp).height(50.dp).width(280.dp),
                            shape = RoundedCornerShape(20.dp)
                        )

                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                    ){
                        Box(Modifier
                            .size(48.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                        )
                        {
                            Icon(Icons.Default.Lock, "UserIconSmall",
                                tint = Color.Black,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        TextField(
                            value = pswd,
                            onValueChange = {pswd = it},
                            label = {Text("Password")},
                            modifier = Modifier.padding(all = 5.dp).height(50.dp).width(280.dp),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        var rcolor = Color.White
                        var clicked = false

                        Text("Remember me", color = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Button (onClick = {
                            if(!clicked){
                                rcolor = Color.Black
                            }
                        }, modifier = Modifier
                            .size(15.dp),
                            colors=ButtonDefaults.buttonColors
                            (containerColor = rcolor),
                            shape = RoundedCornerShape(4.dp)
                        )
                        {
                            Text(" ")
                        }
                    }
                    Button(
                        onClick = {
                            navController.navigate(route = "${AppScreens.Register.name}/{name}")
                        },
                        colors=ButtonDefaults.buttonColors(containerColor = colorResource(R.color.MainMenuColor))
                    ){
                        Text("Dont have an account yet?", color = Color.White)
                    }


                    Button(
                        onClick = {
                            if(pswd == paswdLogin && Name == nameloginCheck){
                                Log.i("Moving to", "menu")
                                navController.navigate(route = "${AppScreens.MainScreen.name}/$Name")
                            }
                            else{
                                ERROR = "Username or password are incorrect, please try again"
                            }
                        },
                        modifier = Modifier.width(300.dp).height(60.dp),
                        colors=ButtonDefaults.buttonColors(containerColor = colorResource(R.color.white)),
                        shape = RoundedCornerShape(15.dp)
                    ){
                        Text("Log In", color = colorResource(R.color.MainMenuColor), fontSize = 20.sp, fontWeight = Bold)
                    }

                }
            }
            Text(ERROR , fontSize = 20.sp, fontWeight = Bold,color = Color.Red)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LogInPreview() {
    val navController = rememberNavController()
    LoginScreen(navController)
}