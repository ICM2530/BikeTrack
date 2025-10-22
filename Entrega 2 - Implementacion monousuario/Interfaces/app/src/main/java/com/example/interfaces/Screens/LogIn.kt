package com.example.interfaces.Screens

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.interfaces.Navigation.AppScreens
import com.example.interfaces.R
import com.example.interfaces.ViewModel.UserAuthViewModel
import com.google.firebase.auth.FirebaseAuth



@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: UserAuthViewModel = viewModel()
){

    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()
    val user by viewModel.user.collectAsState()
    val pasa = false

    LaunchedEffect (Unit) {
        firebaseAuth.currentUser?.let {
            navController.navigate("${AppScreens.MainScreen.name}/defaultUser/default/default/${pasa}") {
                popUpTo(AppScreens.Login.name) { inclusive = true }
            }
        }
    }

    Scaffold(){paddingValues ->
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
                    Text("Login", color = Color.White, fontSize = 28.sp, fontWeight = Bold)

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
                            value = user.email,
                            onValueChange = {
                                viewModel.updateEmailClass(it)
                                viewModel.updateEmailError("")
                            },
                            label = { Text("Mail") },
                            modifier = Modifier.width(300.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            supportingText = {
                                if (user.emailError.isNotEmpty()) {
                                    Text(user.emailError, color = Color.Red)
                                }
                            },
                            isError = user.emailError.isNotEmpty()
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
                            value = user.password,
                            onValueChange = {
                                viewModel.updatePassClass(it)
                                viewModel.updatePassError("")
                            },
                            label = { Text("Password") },
                            modifier = Modifier.width(300.dp),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            supportingText = {
                                if (user.passError.isNotEmpty()) {
                                    Text(user.passError, color = Color.Red)
                                }
                            },
                            isError = user.passError.isNotEmpty()
                        )
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
                            login(
                                email = user.email, password = user.password,
                                viewModel = viewModel, firebaseAuth = firebaseAuth,
                                navController = navController, context = context)
                        },
                        modifier = Modifier.width(300.dp).height(60.dp),
                        colors=ButtonDefaults.buttonColors(containerColor = colorResource(R.color.white)),
                        shape = RoundedCornerShape(15.dp)
                    ){
                        Text("Log In", color = colorResource(R.color.MainMenuColor), fontSize = 20.sp, fontWeight = Bold)
                    }
                }

            }
        }
    }
}

private fun login(
    email: String,
    password: String,
    viewModel: UserAuthViewModel,
    firebaseAuth: FirebaseAuth,
    navController: NavHostController,
    context: Context
) {
    val pasa = false
    if (validateForm(viewModel, email, password)) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = null
                    navController.navigate("${AppScreens.MainScreen.name}/defaultUser/default/default/${pasa}") {
                        popUpTo(AppScreens.Login.name) { inclusive = true }
                    }
                } else {
                    val errorMessage = task.exception?.message ?: "Error desconocido"
                    Toast.makeText(
                        context,
                        "Error de login: $errorMessage",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}
fun validateForm(model: UserAuthViewModel,email:String, password:String):Boolean{
    if (email.isEmpty()){ model.updateEmailError("Correo vacio")
        return false
    }else{model.updateEmailError("")}
    if(!validEmailAddress(email)){model.updateEmailError("Dirección de correo invalida")
        return false
    }else{model.updateEmailError("")}
    if(password.isEmpty()) {model.updatePassError("Contraseña vacia")
        return false
    }else{model.updatePassError("")}
    if(password.length < 6) {model.updatePassError("Contraseña es demasiado corta")
        return false
    }else{model.updatePassError("")}
    return true
}

private fun validEmailAddress(email: String): Boolean {
    val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
    return email.matches(regex.toRegex())
}



@Composable
@Preview
fun see2(){
    LoginScreen(
        rememberNavController()
    )
}