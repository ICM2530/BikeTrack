package com.example.interfaces.Screens


import android.Manifest
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.interfaces.Navigation.AppScreens
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.example.interfaces.R
import com.example.interfaces.Services.Punto5.Companion.sendUserAvailableNotification
import com.example.interfaces.ViewModel.MyUser
import com.example.interfaces.ViewModel.MyUserViewModel
import com.example.interfaces.ViewModel.UserAuthViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File


@Composable
fun imageloader(imageURi: Uri?, UriU: String){
    if(imageURi != null){
        Image(painter = rememberAsyncImagePainter(imageURi), "imagen" ,
            contentScale = ContentScale.FillBounds, modifier = Modifier.size(500.dp))
    }
    else{
        Text("Loading Image...")
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RegisScreen(navController: NavHostController, name: String? = "User", viewModel: UserAuthViewModel = viewModel(), viewModelA: MyUserViewModel = viewModel()) {


    var Name by remember { mutableStateOf("") }
    var pswd by remember { mutableStateOf("") }
    var UsN by remember { mutableStateOf("") }
    var LastName by remember { mutableStateOf("") }
    val statuspermiso = rememberPermissionState(Manifest.permission.CAMERA)
    val contexto = LocalContext.current
    var UriU by remember{ mutableStateOf("Default") }



    val mAuth = remember { FirebaseAuth.getInstance() }
    val context = LocalContext.current
    val pasa = false
    val database = FirebaseDatabase.getInstance()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    //CAMARA Y GALERIA

    val gallery = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()){
            it ->
        imageUri = it
    }

    //USO DE CAMARA


    val camaraUri = FileProvider.getUriForFile(contexto, "${contexto.packageName}.fileprovider",
        File(contexto.filesDir, "cameraPic.jpg")
    )
    val camara = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()) { it ->
        if(it){
            imageUri=camaraUri
        }
    }
    //----------------------------------------------------------------------------------------------


    LaunchedEffect(Unit) {
        statuspermiso.launchPermissionRequest()
    }

    if(statuspermiso.status.isGranted){
        Scaffold()

        {paddingValues ->
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 1000.dp)
                        .widthIn(min= 370.dp)
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
                        Text("Register", color = colorResource(R.color.MainMenuColor), fontSize = 30.sp, fontWeight = FontWeight.Black)

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
                                value = Name,
                                onValueChange = {Name = it
                                    viewModel.updateEmailError("")},
                                label = {Text("Mail")},
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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
                                value = UsN,
                                onValueChange = {UsN = it
                                    viewModel.updateEmailError("")},
                                label = {Text("Name")},
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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
                                value = LastName,
                                onValueChange = {LastName = it
                                    viewModel.updateEmailError("")},
                                label = {Text("Last Name")},
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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


                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(Modifier
                                .size(52.dp)
                                .background(Color.White, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            )
                            {
                                Icon(Icons.Default.Lock, "UserIconSmall",
                                    tint = colorResource(R.color.MainMenuColor),
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            OutlinedTextField(
                                value = pswd,
                                onValueChange = {pswd = it
                                    viewModel.updateEmailError("") },
                                label = {Text("Password")},
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                visualTransformation = PasswordVisualTransformation(),
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
                                if (validateFormReg(viewModel, Name, pswd, UsN, LastName)) {
                                    mAuth.createUserWithEmailAndPassword(Name, pswd)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                val newUser = MyUser(
                                                    name = UsN,
                                                    lastName = LastName,
                                                    mail = Name,
                                                    pass = pswd,
                                                    uri = "default", // Se actualizará después de subir la imagen
                                                    latitude = 4.628196206265264,
                                                    longitude = -74.06545308630227,
                                                    identification = (100000..999999).random().toString(),
                                                    available = false
                                                )

                                                // Subir imagen y guardar usuario
                                                viewModelA.uploadImageAndSaveUser(imageUri, newUser) { success, imageUrl ->
                                                    if (success) {
                                                        Toast.makeText(
                                                            context,
                                                            "Usuario registrado correctamente",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        navController.navigate(route = "${AppScreens.punto2.name}")
                                                        Log.i("Moving to", "menu")
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            "Error al subir imagen, pero usuario creado",
                                                            Toast.LENGTH_SHORT
                                                        ).show()

                                                        navController.navigate(route = "${AppScreens.punto2.name}")
                                                    }
                                                }
                                            } else {
                                                Toast.makeText(context, "Sign up failed", Toast.LENGTH_SHORT).show()
                                                Log.i("It got here", "Meaning the Sign up broke")
                                            }
                                        }
                                }
                            },
                            modifier = Modifier
                                .width(300.dp)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.white))
                        ) {
                            Text(
                                "Register and begin",
                                color = colorResource(R.color.MainMenuColor),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Button(
                            onClick = {
                                navController.navigate(AppScreens.Login.name)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors=ButtonDefaults.buttonColors(containerColor = colorResource(R.color.MainMenuColor))
                        ){
                            Text("Back to login", color = colorResource(R.color.white), fontSize = 15.sp, fontWeight = FontWeight.Black)
                        }

                    }
                }
            }
        }
    }else{
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically)
                    ,modifier = Modifier.padding(paddingValues)){
                    Text("El permiso solicitado no fue aceptado, intente de nuevo mas tarde...")
                    Button(onClick = {
                        navController.navigate(AppScreens.Login.name)
                    }){
                        Text("Volver")
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

fun validateFormReg(model: UserAuthViewModel,email:String, password:String, userName: String, userLastName: String):Boolean{
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
    if(userName.isEmpty() || userLastName.isEmpty()) {model.updatePassError("Nombre de usuario incompleto")
        return false
    }else{model.updatePassError("")}

    return true
}

private fun validEmailAdd(email: String): Boolean {
    val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
    return email.matches(regex.toRegex())
}