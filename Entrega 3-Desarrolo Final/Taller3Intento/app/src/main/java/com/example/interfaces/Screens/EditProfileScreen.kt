package com.example.interfaces.Screens

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.interfaces.R
import com.example.interfaces.ViewModel.MyUser
import com.example.interfaces.ViewModel.MyUserViewModel
import com.example.interfaces.ViewModel.UserAuthViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.auth.FirebaseAuth
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EditProfileScreen(
    navController: NavHostController,
    userId: String?,
    viewModel: UserAuthViewModel = viewModel(),
    viewModelA: MyUserViewModel = viewModel()
) {
    val contexto = LocalContext.current
    val statuspermiso = rememberPermissionState(Manifest.permission.CAMERA)

    val users = viewModelA.users.collectAsState()
    val currentUser = users.value.firstOrNull { it.identification == userId ?: "" }

    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var mail by remember { mutableStateOf("") }
    var pswd by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    //--COLORES
    var MainColor by remember { mutableStateOf(R.color.white) }
    var MainTextcolor by remember { mutableStateOf(R.color.black) }
    var MainBackColor by remember { mutableStateOf(R.color.white) }
    var MainInColor by remember { mutableStateOf(R.color.MainMenuColor) }

    //--------_
    val sensorManager = remember {
        contexto.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val sensorLuz = remember{sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)}
    val sensorListener = remember {
        object : SensorEventListener{
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
                    val luz = event.values[0]

                    //Se define que estilo de mapa se usara
                    //ActualStyle = if() MapLight else MapDark
                    if(luz >= 2000){
                        MainColor = R.color.white
                        MainTextcolor = R.color.black
                        MainBackColor = R.color.white
                        MainInColor = R.color.MainMenuColor
                    }else{
                        MainColor = R.color.MainMenuColor
                        MainTextcolor = R.color.white
                        MainBackColor = R.color.Dark
                        MainInColor = R.color.white
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(Unit) {
        sensorManager.registerListener(
            sensorListener,
            sensorLuz,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            name = user.name
            lastName = user.lastName
            mail = user.mail
            pswd = user.pass
            if (user.uri != "default" && user.uri.isNotEmpty()) {
                imageUri = Uri.parse(user.uri)
            }
        }
    }

    // Launcher para galería
    val gallery = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    // Launcher para cámara
    val camaraUri = FileProvider.getUriForFile(
        contexto,
        "${contexto.packageName}.fileprovider",
        File(contexto.filesDir, "cameraPic.jpg")
    )
    val camara = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri = camaraUri
        }
    }

    LaunchedEffect(Unit) {
        statuspermiso.launchPermissionRequest()
    }

    if (statuspermiso.status.isGranted) {
        Scaffold (containerColor = colorResource(MainBackColor)){ paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 900.dp)
                        .widthIn(min = 370.dp)
                        .shadow(8.dp, RoundedCornerShape(26.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(MainColor)
                    ),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                    ) {
                        Text(
                            "Edit Profile",
                            color = colorResource(MainInColor),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black
                        )

                        // Campo Email (solo lectura)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                Modifier
                                    .size(52.dp)
                                    .background(colorResource(MainColor), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    "UserIconSmall",
                                    tint = colorResource(MainInColor),
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            OutlinedTextField(
                                value = mail,
                                onValueChange = { },
                                label = { Text("Mail (Read only)") },
                                enabled = false,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledBorderColor = colorResource(MainInColor).copy(alpha = 0.5f),
                                    disabledLabelColor = colorResource(MainInColor).copy(alpha = 0.5f),
                                    disabledTextColor = colorResource(MainInColor).copy(alpha = 0.7f)
                                )
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                Modifier
                                    .size(52.dp)
                                    .background(colorResource(MainColor), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    "UserIconSmall",
                                    tint = colorResource(MainInColor),
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Name") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = colorResource(MainInColor),
                                    focusedBorderColor = colorResource(MainInColor),
                                    focusedLabelColor = colorResource(MainInColor),
                                    unfocusedLabelColor = colorResource(MainInColor),
                                    cursorColor = colorResource(MainInColor),
                                    focusedTextColor = colorResource(MainInColor),
                                    unfocusedTextColor = colorResource(MainInColor)
                                )
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                Modifier
                                    .size(52.dp)
                                    .background(colorResource(MainColor), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    "UserIconSmall",
                                    tint = colorResource(MainInColor),
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            OutlinedTextField(
                                value = lastName,
                                onValueChange = { lastName = it },
                                label = { Text("Last Name") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = colorResource(MainInColor),
                                    focusedBorderColor = colorResource(MainInColor),
                                    focusedLabelColor = colorResource(MainInColor),
                                    unfocusedLabelColor = colorResource(MainInColor),
                                    cursorColor = colorResource(MainInColor),
                                    focusedTextColor = colorResource(MainInColor),
                                    unfocusedTextColor = colorResource(MainInColor)
                                )
                            )
                        }


                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                Modifier
                                    .size(52.dp)
                                    .background(colorResource(MainColor), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Lock,
                                    "UserIconSmall",
                                    tint = colorResource(MainInColor),
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            OutlinedTextField(
                                value = pswd,
                                onValueChange = { pswd = it },
                                label = { Text("Password") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                visualTransformation = PasswordVisualTransformation(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = colorResource(MainInColor),
                                    focusedBorderColor = colorResource(MainInColor),
                                    focusedLabelColor = colorResource(MainInColor),
                                    unfocusedLabelColor = colorResource(MainInColor),
                                    cursorColor = colorResource(MainInColor),
                                    focusedTextColor = colorResource(MainInColor),
                                    unfocusedTextColor = colorResource(MainInColor)
                                )
                            )
                        }

                        // Sección de imagen
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    12.dp,
                                    Alignment.CenterHorizontally
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = { gallery.launch("image/*") },
                                    colors = ButtonColors(
                                        containerColor = colorResource(MainInColor),
                                        contentColor = colorResource(MainColor),
                                        disabledContainerColor = colorResource(MainInColor),
                                        disabledContentColor = colorResource(MainColor))
                                ) {
                                    Text("Galeria", color = colorResource(MainColor))
                                }

                                Button(
                                    onClick = { camara.launch(camaraUri) },
                                    colors = ButtonColors(
                                        containerColor = colorResource(MainInColor),
                                        contentColor = colorResource(MainColor),
                                        disabledContainerColor = colorResource(MainInColor),
                                        disabledContentColor = colorResource(MainColor)
                                    )
                                ) {
                                    Text("Camara", color = colorResource(MainColor))
                                }
                            }

                            Box(
                                modifier = Modifier.size(150.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (imageUri != null) {
                                    Image(
                                        painter = rememberAsyncImagePainter(imageUri),
                                        "Profile Image",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(150.dp)
                                    )
                                } else {
                                    Text("No Image", color = Color.Gray)
                                }
                            }
                        }

                        Button(
                            onClick = {
                                Log.d("EditProfile", "Button clicked")
                                Log.d("EditProfile", "Current user: $currentUser")
                                Log.d("EditProfile", "Name: $name, LastName: $lastName, Pass: $pswd")

                                if (currentUser == null) {
                                    Toast.makeText(
                                        contexto,
                                        "Error: Usuario no encontrado",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }

                                if (name.isEmpty() || lastName.isEmpty()) {
                                    Toast.makeText(
                                        contexto,
                                        "Nombre y apellido son requeridos",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }

                                if (pswd.length < 6) {
                                    Toast.makeText(
                                        contexto,
                                        "Contraseña debe tener al menos 6 caracteres",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }


                                Toast.makeText(
                                    contexto,
                                    "Actualizando perfil...",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Crear usuario actualizado (sin la URI todavía)
                                val updatedUser = currentUser!!.copy(
                                    name = name,
                                    lastName = lastName,
                                    pass = pswd
                                )

                                // Determinar qué URI usar
                                val uriToUpload = if (imageUri != null &&
                                    imageUri.toString().startsWith("content://")) {
                                    // Nueva imagen local seleccionada
                                    imageUri
                                } else if (imageUri != null &&
                                    imageUri.toString().startsWith("https://")) {
                                    // URI de Firebase ya existente
                                    null // No subir de nuevo
                                } else {
                                    // Sin imagen
                                    null
                                }

                                Log.d("EditProfile", "URI to upload: $uriToUpload")


                                if (uriToUpload != null &&
                                    currentUser!!.uri != "default" &&
                                    currentUser!!.uri.startsWith("https://")) {
                                    viewModelA.deleteImage(currentUser!!.uri)
                                }

                                viewModelA.uploadImageAndSaveUser(
                                    uriToUpload,
                                    updatedUser
                                ) { success, downloadUrl ->
                                    if (success) {
                                        Toast.makeText(
                                            contexto,
                                            "Perfil actualizado exitosamente",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        Log.d("EditProfile", "Profile updated successfully. Image URL: $downloadUrl")
                                        navController.popBackStack()
                                    } else {
                                        Toast.makeText(
                                            contexto,
                                            "Error al actualizar la imagen",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            },
                            modifier = Modifier
                                .width(300.dp)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(MainColor)
                            )
                        ) {
                            Text(
                                "Save Changes",
                                color = colorResource(MainInColor),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Button(
                            onClick = {
                                navController.popBackStack()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(MainInColor)
                            )
                        ) {
                            Text(
                                "Cancel",
                                color = colorResource(MainColor),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    } else {
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
                    verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically),
                    modifier = Modifier.padding(paddingValues)
                ) {
                    Text("El permiso solicitado no fue aceptado, intente de nuevo mas tarde...")
                    Button(onClick = {
                        navController.popBackStack()
                    }) {
                        Text("Volver")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfilePreview() {
    val navController = rememberNavController()
    EditProfileScreen(navController, "123456")
}

