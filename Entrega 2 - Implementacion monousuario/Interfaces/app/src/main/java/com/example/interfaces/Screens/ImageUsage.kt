package com.example.interfaces.Screens

import android.Manifest
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.interfaces.Navigation.AppScreens
import com.example.interfaces.PATH_USERS
import com.example.interfaces.R
import com.example.interfaces.ViewModel.MyUser
import com.example.interfaces.ViewModel.MyUserViewModel
import com.example.interfaces.database
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.File

@Composable
fun imageloader(imageURi: Uri?, UriU: String, textcolors: Int){
    if(imageURi != null){
        Image(painter = rememberAsyncImagePainter(imageURi), "imagen" ,
            contentScale = ContentScale.FillBounds, modifier = Modifier.size(500.dp))
    }
    else{
        Text("Loading Image...", color = colorResource(textcolors))
    }
}

fun saveUser(username: String, age: String, uri: String, id : String, mail : String){
    val ageInt = age.toString()
    ageInt.let {
        val myUser= MyUser(username, age, id, uri, mail)
        var ref = database.getReference(PATH_USERS)
        val key = ref.push().key

        ref = database.getReference(PATH_USERS+key)
        ref.setValue(myUser)

    }
}
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Images(navController: NavController, name: String? = "TemplateUser", image:String? = "default", viewModel: MyUserViewModel = viewModel(), darkauto : Boolean){

    val users by viewModel._users.collectAsState()
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    val statuspermiso = rememberPermissionState(Manifest.permission.CAMERA)
    var textodescriptor by remember{ mutableStateOf("") }
    var nombreU by remember{ mutableStateOf("$name") }
    var edadU by remember{ mutableStateOf("18") }
    var UriU by remember{ mutableStateOf("Default") }
    val database = FirebaseDatabase.getInstance()
    var dir by remember{ mutableStateOf("") }
    val context = LocalContext.current
    var AutoturnDark by remember { mutableStateOf(false) }

    var ScreenColor by remember { mutableStateOf(R.color.white) }
    var buttoncolors by remember { mutableStateOf(R.color.MainMenuColor) }
    var textcolors by remember { mutableStateOf(R.color.black) }
    var pubcolors by remember { mutableStateOf(R.color.Dark) }



    val sensorManager = remember {
        context.getSystemService(SENSOR_SERVICE) as SensorManager
    }
    val lightSensor = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }




    val sensorListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
                    val lux = event.values[0]
                    if(darkauto) {
                        AutoturnDark = if (lux < 2000) true else false
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }



    if(darkauto && AutoturnDark){
        buttoncolors = R.color.DarkButtons
        ScreenColor = R.color.Dark
        textcolors = R.color.white
        pubcolors = R.color.PubDark
    }
    else{
        buttoncolors = R.color.MainMenuColor
        ScreenColor = R.color.white
        textcolors = R.color.black
        pubcolors = R.color.Dark
    }




    DisposableEffect(Unit) {
        sensorManager.registerListener(sensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }





    val data = users.find{it.mail == currentUser?.email }

    if(data != null){
        nombreU = data.name
        edadU = data.age
        UriU = data.uri
    }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    //CAMARA Y GALERIA

    val gallery = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()){
            it ->
        imageUri = it
    }

    //USO DE CAMARA


    val camaraUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider",
        File(context.filesDir, "cameraPic.jpg")
    )
    val camara = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()) { it ->
        if(it){
            imageUri=camaraUri
        }
    }
    //----------------------------------------------------------------------------------------------

    var ImagenUsuario by remember{mutableStateOf("")}
    if(statuspermiso.status.isGranted){
        if(image != null && image != "default") {
            var imageUri by remember { mutableStateOf<Uri?>(image.toUri()) }
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = colorResource(ScreenColor)
        )

        {paddingValues ->
            var revised by remember{mutableStateOf("")}
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically),
                modifier = Modifier.fillMaxSize().padding(paddingValues))
            {

                Box(
                    modifier = Modifier.size(500.dp),
                    contentAlignment = Alignment.Center
                ){
                    if(UriU != null && UriU != "Default" && imageUri == null){
                        var changed by remember{mutableStateOf(UriU)}
                        changed = changed.replace('L','/').replace('A', ':').replace('M', '_')
                        imageloader(changed.toUri(), UriU, textcolors)
                    }
                    else{
                        imageloader(imageUri, UriU, textcolors)
                    }


                }


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally),
                    modifier = Modifier.fillMaxWidth()
                ){
                    Button(
                        onClick = { gallery.launch("image/*")},
                        modifier = Modifier.width(180.dp).height(80.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(buttoncolors))
                    ){
                        Text("Galeria")
                    }

                    Button(
                        onClick = {
                            camara.launch(camaraUri)},
                        modifier = Modifier.width(180.dp).height(80.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(buttoncolors))

                    ){
                        Text("Camara")
                    }
                }

                OutlinedTextField(
                    value = nombreU,
                    onValueChange = {
                        nombreU = it
                    },
                    label = { Text("name") },
                    modifier = Modifier.width(400.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorResource(textcolors),
                        unfocusedTextColor = Color.LightGray
                    )
                )

                OutlinedTextField(
                    value = edadU,
                    onValueChange = {
                        edadU = it
                    },
                    label = { Text("age") },
                    modifier = Modifier.width(400.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorResource(textcolors),
                        unfocusedTextColor = Color.LightGray
                    )
                )


                Button(
                    onClick = {
                        if(imageUri == null && UriU != null){
                            revised = UriU.replace('/', 'L').replace(':', 'A').replace('_', 'M')
                        }
                        else {
                            revised = imageUri.toString().replace('/', 'L').replace(':', 'A').replace('_', 'M')
                        }
                        val id = currentUser?.uid.toString()
                        if(data == null) {
                            saveUser(nombreU, edadU, revised, id, currentUser?.email.toString())
                        }
                        else{

                        }
                        Log.i("reviseUri", revised)
                        navController.navigate(route = "${AppScreens.MainScreen.name}/$name/${revised}/$id/${darkauto}")},
                    modifier = Modifier.width(180.dp).height(80.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(buttoncolors))
                ){
                    Text("Aceptar")
                }

            }

        }

    }

    else{
        if(statuspermiso.status.shouldShowRationale){
            textodescriptor="El permiso es necesario para seleccionar la imagen de perfil"
        }
        else{
            textodescriptor="Se requiere el permiso de camara"
        }

        Scaffold(
            modifier = Modifier.fillMaxSize()
        ){paddingValues ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(9.dp, Alignment.CenterVertically),
                modifier = Modifier.padding(paddingValues).fillMaxSize()
            ){
                Button(
                    onClick ={
                        statuspermiso.launchPermissionRequest()
                    },
                    modifier = Modifier.width(180.dp).height(80.dp)
                ){
                    Text("Aceptar Solicitud")
                }
                Text(textodescriptor)
            }
        }
    }
}