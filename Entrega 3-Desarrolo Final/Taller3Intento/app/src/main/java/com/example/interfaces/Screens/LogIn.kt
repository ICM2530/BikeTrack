package com.example.interfaces.Screens

import android.Manifest
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import com.example.interfaces.Services.Punto5.Companion.sendUserAvailableNotification
import com.example.interfaces.ViewModel.MyUser
import com.example.interfaces.ViewModel.UserAuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: UserAuthViewModel = viewModel()
){

    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()
    val user by viewModel.user.collectAsState()
    val pasa = false
    val database = FirebaseDatabase.getInstance()
    val usersRef = database.getReference("users")

    val previousState = remember { mutableStateMapOf<String, Boolean>() }
    LaunchedEffect (Unit) {
        firebaseAuth.currentUser?.let {
            navController.navigate(AppScreens.punto2.name) {
                popUpTo(AppScreens.Login.name) { inclusive = true }
            }
        }

        val listener = object : ValueEventListener {
            @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = mutableListOf<MyUser>()

                for (userSnapshot in snapshot.children) {
                    val email = userSnapshot.child("mail").getValue(String::class.java) ?: continue
                    val isAvailable = userSnapshot.child("available").getValue(Boolean::class.java) ?: false
                    val lastState = previousState[email]

                    if (lastState == false && isAvailable) {
                        val userName = userSnapshot.child("name").getValue(String::class.java) ?: ""
                        val lastName = userSnapshot.child("lastName").getValue(String::class.java) ?: ""
                        val mail = email
                        val latitude = userSnapshot.child("latitude").getValue(Double::class.java) ?: 0.0
                        val longitude = userSnapshot.child("longitude").getValue(Double::class.java) ?: 0.0

                        sendUserAvailableNotification(
                            context = context,
                            name = userName,
                            lastName = lastName,
                            mail = mail,
                            latitude = latitude,
                            longitude = longitude
                        )
                    }

                    previousState[email] = isAvailable

                    val userName = userSnapshot.child("name").getValue(String::class.java)
                    val lastName = userSnapshot.child("lastName").getValue(String::class.java)
                    val uri = userSnapshot.child("uri").getValue(String::class.java)
                    val latitude = userSnapshot.child("latitude").getValue(Double::class.java)
                    val longitude = userSnapshot.child("longitude").getValue(Double::class.java)
                    val identification = userSnapshot.child("identification").getValue(String::class.java)
                    val pass = userSnapshot.child("pass").getValue(String::class.java)

                    if (email != firebaseAuth.currentUser?.email && isAvailable) {
                        users.add(
                            MyUser(
                                name = userName ?: "",
                                lastName = lastName ?: "",
                                mail = email,
                                pass = pass ?: "",
                                uri = uri ?: "",
                                latitude = latitude ?: 0.0,
                                longitude = longitude ?: 0.0,
                                identification = identification ?: "",
                                available = true
                            )
                        )
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AvailableUsers", "Error: ${error.message}")
            }
        }
        usersRef.addValueEventListener(listener)
        }


    LaunchedEffect (Unit) {

        firebaseAuth.currentUser?.let {
            navController.navigate(AppScreens.punto2.name) {
                popUpTo(AppScreens.Login.name) { inclusive = true }
            }
        }
    }


    Scaffold(){paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,

            ){
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 480.dp)
                    .shadow(8.dp, RoundedCornerShape(26.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.white)
                ),
                shape = RoundedCornerShape(26.dp)
            ){
                Column(
                    modifier =Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    Card(colors = CardColors(
                        contentColor = Color.White,
                        containerColor = colorResource(R.color.MainMenuColor),
                        disabledContainerColor = colorResource(R.color.MainMenuColor),
                        disabledContentColor = Color.White
                    )){
                        Image(painterResource(R.drawable.logobk),
                            contentDescription = "TemplateMap",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.width(400.dp).height(200.dp))
                    }

                    Text("Login", color = colorResource(R.color.MainMenuColor), fontSize = 30.sp, fontWeight = FontWeight.Black)

                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(Modifier
                            .size(52.dp)
                            .background(Color.White, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        )
                        {
                            Icon(Icons.Default.Person, "UserIconSmall",
                                tint = colorResource(R.color.MainMenuColor),
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        OutlinedTextField(
                            value = user.email,
                            onValueChange = {
                                viewModel.updateEmailClass(it)
                                viewModel.updateEmailError("")
                            },
                            label = { Text("Mail") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            supportingText = {
                                if (user.emailError.isNotEmpty()) {
                                    Text(user.emailError, color = Color.Red)
                                }
                            },
                            isError = user.emailError.isNotEmpty(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = colorResource(R.color.MainMenuColor),
                                focusedBorderColor = colorResource(R.color.MainMenuColor),
                                focusedLabelColor = colorResource(R.color.MainMenuColor),
                                unfocusedLabelColor = colorResource(R.color.MainMenuColor),
                                cursorColor = colorResource(R.color.MainMenuColor),
                                focusedTextColor = colorResource(R.color.MainMenuColor),
                                unfocusedTextColor = colorResource(R.color.MainMenuColor)
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ){
                        Box(Modifier
                            .size(52.dp)
                            .background(Color.White, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        )
                        {
                            Icon(Icons.Default.Lock, "UserIconSmall",
                                tint = colorResource(R.color.MainMenuColor),
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        OutlinedTextField(
                            value = user.password,
                            onValueChange = {
                                viewModel.updatePassClass(it)
                                viewModel.updatePassError("")
                            },
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            supportingText = {
                                if (user.passError.isNotEmpty()) {
                                    Text(user.passError, color = Color.Red)
                                }
                            },
                            isError = user.passError.isNotEmpty(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = colorResource(R.color.MainMenuColor),
                                focusedBorderColor = colorResource(R.color.MainMenuColor),
                                focusedLabelColor = colorResource(R.color.MainMenuColor),
                                unfocusedLabelColor = colorResource(R.color.MainMenuColor),
                                cursorColor = colorResource(R.color.MainMenuColor),
                                focusedTextColor = colorResource(R.color.MainMenuColor),
                                unfocusedTextColor = colorResource(R.color.MainMenuColor)
                            )
                        )
                    }

                    Button(
                        onClick = {
                            navController.navigate(route = "${AppScreens.Register.name}/{name}")
                        },
                    ){
                        Text("Dont have an account yet?", color = Color.White, fontSize=15.sp)
                    }


                    Button(
                        onClick = {
                            login(
                                email = user.email, password = user.password,
                                viewModel = viewModel, firebaseAuth = firebaseAuth,
                                navController = navController, context = context)
                        },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        colors=ButtonDefaults.buttonColors(containerColor = Color.White),
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
                    navController.navigate("${AppScreens.punto2.name}") {
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