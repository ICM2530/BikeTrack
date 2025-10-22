package com.example.interfaces.Screens


import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.example.interfaces.R
import com.example.interfaces.ViewModel.UserAuthViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun RegisScreen(navController: NavHostController, name: String? = "User", viewModel: UserAuthViewModel = viewModel()) {


    var Name by remember { mutableStateOf("") }
    var pswd by remember { mutableStateOf("") }
    val mAuth = remember { FirebaseAuth.getInstance() }
    val context = LocalContext.current
    val pasa = false
    Scaffold()

    {paddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            Card(
                modifier = Modifier
                    .width(370.dp)
                    .height(440.dp),
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
                    Text("Register", color = Color.White, fontSize = 28.sp, fontWeight = Bold)

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
                            onValueChange = {Name = it
                                viewModel.updateEmailError("")},
                            label = {Text("Mail")},
                            modifier = Modifier
                                .padding(all = 5.dp)
                                .height(50.dp)
                                .width(280.dp),
                            shape = RoundedCornerShape(20.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
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
                            onValueChange = {pswd = it
                                viewModel.updateEmailError("") },
                            label = {Text("Password")},
                            modifier = Modifier
                                .padding(all = 5.dp)
                                .height(50.dp)
                                .width(280.dp),
                            shape = RoundedCornerShape(20.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            visualTransformation = PasswordVisualTransformation()
                        )
                    }

                    Button(
                        onClick = {
                            if (validateFormReg(viewModel, Name, pswd)) {
                                mAuth.createUserWithEmailAndPassword(Name, pswd)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            navController.navigate(route = "${AppScreens.MainScreen.name}/$Name/default/default/${pasa}")
                                            Log.i("Moving to", "menu")
                                        } else {
                                            Toast.makeText(context, "Sign up failed",Toast.LENGTH_SHORT).show()
                                            Log.i("It got here", "Meaning the Sign up broke")
                                        }
                                    }
                            }

                        },
                        modifier = Modifier
                            .width(300.dp)
                            .height(50.dp),
                        colors=ButtonDefaults.buttonColors(containerColor = colorResource(R.color.white)),
                        shape = RoundedCornerShape(15.dp)
                    ){
                        Text("Register and begin", color = colorResource(R.color.MainMenuColor), fontSize = 20.sp, fontWeight = Bold)
                    }

                    Button(
                        onClick = {
                            navController.navigate(AppScreens.Login.name)
                        },
                        modifier = Modifier
                            .width(300.dp)
                            .height(50.dp),
                        colors=ButtonDefaults.buttonColors(containerColor = colorResource(R.color.MainMenuColor)),
                        shape = RoundedCornerShape(15.dp)
                    ){
                        Text("Back to login", color = colorResource(R.color.white), fontSize = 15.sp, fontWeight = Bold)
                    }

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisPreview() {
    val navController = rememberNavController()
    RegisScreen(navController)
}

fun validateFormReg(model: UserAuthViewModel,email:String, password:String):Boolean{
    if (email.isEmpty()){ model.updateEmailError("Correo vacio")
        return false
    }else{model.updateEmailError("")}
    if(!validEmailAdd(email)){model.updateEmailError("Dirección de correo invalida")
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

private fun validEmailAdd(email: String): Boolean {
    val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
    return email.matches(regex.toRegex())
}