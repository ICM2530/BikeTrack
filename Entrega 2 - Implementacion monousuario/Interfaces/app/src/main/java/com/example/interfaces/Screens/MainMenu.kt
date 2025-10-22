package com.example.interfaces.Screens

import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.util.Log
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Create
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.interfaces.Navigation.AppScreens
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.interfaces.PATH_USERS
import com.example.interfaces.R
import com.example.interfaces.ViewModel.MyUser
import com.example.interfaces.ViewModel.MyUserViewModel
import com.example.interfaces.ViewModel.UserAuthViewModel
import com.example.interfaces.database
import com.google.firebase.auth.FirebaseAuth
import java.security.KeyStore



@Composable
fun cargaImagenes(imageURi: Uri?){
    if(imageURi != null){
        Image(painter = rememberAsyncImagePainter(imageURi), "imagen" )
    }
    else{
        Text("Cargando imagen...")
    }
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

@Composable
fun MainMenu(navController: NavHostController, name: String? = "TemplateUser", image: String? = "default", viewModel: MyUserViewModel = viewModel(), id: String? = "default", darkauto : Boolean) {
    val users by viewModel._users.collectAsState()
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    val context = LocalContext.current
    val userinDB = users.find { it.mail == currentUser?.email }
    var nameDB by remember { mutableStateOf(name)}
    var uriDB by remember { mutableStateOf("default")}

    if(userinDB != null){
        nameDB = userinDB.name
        val URI = userinDB.uri
        uriDB = URI
        Log.i("UriIs", uriDB)
    }


    var darkt by remember {mutableStateOf("Dark Mode")}

    var ScreenColor by remember { mutableStateOf(R.color.white) }
    var buttoncolors by remember { mutableStateOf(R.color.MainMenuColor) }
    var textcolors by remember { mutableStateOf(R.color.black) }
    var pubcolors by remember { mutableStateOf(R.color.Dark) }
    var AutoturnDark by remember { mutableStateOf(false) }
    var ChoseAutoDark by remember { mutableStateOf(darkauto) }
    var checked by remember{mutableStateOf(false)}


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
                    Log.i("MapApp", "Luminosidad: $lux")
                    if(ChoseAutoDark) {
                        AutoturnDark = if (lux < 2000) true else false
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(Unit) {
        sensorManager.registerListener(sensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }



    if(AutoturnDark && ChoseAutoDark){
        buttoncolors = R.color.DarkButtons
        ScreenColor = R.color.Dark
        textcolors = R.color.white
        pubcolors = R.color.PubDark
        darkt = "Light Mode"
    }
    else if(!AutoturnDark && ChoseAutoDark){
        buttoncolors = R.color.MainMenuColor
        ScreenColor = R.color.white
        textcolors = R.color.black
        pubcolors = R.color.Dark
        darkt = "DarkMode"
    }



    Scaffold(
        topBar={TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorResource(buttoncolors),
            ),
            title = {Text("Home", color = Color.White, fontWeight = Bold)},
            navigationIcon = {
                IconButton(
                    onClick = {
                        firebaseAuth.signOut()
                        navController.navigate(AppScreens.Login.name) {
                            popUpTo("${AppScreens.MainScreen.name}/$name/$image") { inclusive = true }
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Regresa a inicio", tint = Color.White
                    )
                }
            },
            actions = {

                Box(){
                    var expandeds by remember { mutableStateOf(false) }
                    IconButton(onClick = {
                        if(expandeds){
                            expandeds = false
                        }
                        else{
                            expandeds = true
                        }
                    }, colors = IconButtonColors(
                        contentColor = Color.White,
                        containerColor = colorResource(buttoncolors),
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.White
                    ) )
                    { Icon(Icons.Default.Settings, "Friends") }

                    DropdownMenu(
                        expanded = expandeds,
                        onDismissRequest = { expandeds = false}
                    ) {

                        DropdownMenuItem(
                            leadingIcon = {Icon(Icons.Default.DarkMode, "DarkMode manual change", tint = colorResource(buttoncolors))},
                            trailingIcon = {
                                Switch(
                                    checked = checked,
                                    onCheckedChange = {
                                        checked = it
                                        if(ChoseAutoDark){
                                            ChoseAutoDark = false
                                        }
                                        else{
                                            ChoseAutoDark = true
                                        }
                                    },
                                    colors = SwitchDefaults.colors(
                                        uncheckedBorderColor = colorResource(R.color.MainMenuColor),
                                        checkedBorderColor = colorResource(R.color.Dark),
                                        checkedTrackColor = colorResource(buttoncolors)
                                    )
                                ) },
                            text = {Text("Automatic NightMode?")},
                            onClick = {}

                        )
                    }

                }

                Box(){
                    IconButton(onClick = {
                        navController.navigate(route = "${AppScreens.chat.name}/$name/$image/${ChoseAutoDark}")
                    }, colors = IconButtonColors(
                        contentColor = Color.White,
                        containerColor = colorResource(buttoncolors),
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
                        containerColor = colorResource(buttoncolors),
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
                            onClick = {navController.navigate(route = "${AppScreens.seefriendtrack.name}/$name/$image/${ChoseAutoDark}")}
                        )
                    }
                }

            }
        )},
        containerColor = colorResource(ScreenColor)

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
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                if(image == "default" && uriDB == "default"){
                    Image(
                        painter = painterResource(R.drawable.userbasic),
                        contentDescription = "BasicUser",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .size(190.dp)
                            .clip(CircleShape)
                    )
                }
                else if(image == "default"){

                    val changed = uriDB.replace('L', '/').replace('A', ':').replace('M', '_')
                    val UriD = changed.toUri()
                    Log.i("UriIS", "CHANGED AND URI: $changed")
                    Image(
                        painter = rememberAsyncImagePainter(UriD),
                        contentDescription = "BasicUser",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .size(190.dp)
                            .clip(CircleShape)
                    )
                }
                else{
                    var changed by remember { mutableStateOf(image)}
                    if(image != null) {
                        changed = image.replace('L', '/').replace('A', ':').replace('M', '_')
                        val Uri = changed!!.toUri()
                        Log.i("UriIS", "CHANGED TO IMAGE: $changed")
                        Image(
                            painter = rememberAsyncImagePainter(Uri),
                            contentDescription = "BasicUser",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .size(190.dp)
                                .clip(CircleShape)
                        )
                    }

                }


                Text(text = "Hello $nameDB!", fontWeight = Bold, fontSize = 30.sp, color = colorResource(buttoncolors))

                Button(
                    onClick = {

                        navController.navigate(route = "${AppScreens.changeprofile.name}/$name/$image/${ChoseAutoDark}")
                    },
                    colors = ButtonDefaults.buttonColors
                        (containerColor = colorResource(buttoncolors)),
                    modifier = Modifier
                        .width(230.dp)
                        .height(60.dp),
                )
                {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()){
                        Icon(Icons.Outlined.Create, "choose from gallery",
                            modifier = Modifier.size(30.dp), tint = Color.White)
                        Text(text = "Edit Profile", fontWeight = Bold, fontSize = 25.sp, color = Color.White)
                    }
                }


            }





            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 5.dp,
                color = colorResource(buttoncolors)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Top,
            ){
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text("Friends", fontWeight = Bold, fontSize = 30.sp, color = colorResource(buttoncolors))
                    Text(" 131 friends", fontSize = 20.sp, color = colorResource(textcolors))
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text("Followers", fontWeight = Bold, fontSize = 30.sp, color = colorResource(buttoncolors))
                    Text(" 200 followers", fontSize = 20.sp, color = colorResource(textcolors))
                }
            }

            Text(text = "Activities", fontWeight = Bold, fontSize = 30.sp, color = colorResource(buttoncolors))
            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Top,
            ){
                Button(onClick={
                    val intent = Intent(context, MakeActivity::class.java)
                    context.startActivity(intent)
                },
                    colors=ButtonDefaults.buttonColors
                        (containerColor = colorResource(buttoncolors)),
                    modifier = Modifier
                        .width(180.dp)
                        .height(60.dp)){
                    Text("Make Activity", fontSize = 20.sp, fontWeight=Bold)
                }
                Button(
                    onClick = {
                        navController.navigate(route = "${AppScreens.seeact.name}/$name/$image/${ChoseAutoDark}")
                    },
                    colors = ButtonDefaults.buttonColors
                        (containerColor = colorResource(buttoncolors)),
                    modifier = Modifier
                        .width(180.dp)
                        .height(60.dp),
                ){
                    Text("See Activities", fontSize = 20.sp, fontWeight=Bold)
                }
            }

            Text("Recent publications", fontSize = 30.sp, fontWeight=Bold, color = colorResource(buttoncolors))

            val list = Array<String>(10){"Example publication"}
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically)
            ){
                items(list){title->
                    Button(onClick = {
                        navController.navigate(route = "${AppScreens.seepublic.name}/$name/$image/${ChoseAutoDark}")
                    },
                        modifier=Modifier
                            .height(300.dp)
                            .width(300.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(pubcolors))

                    ){
                        Text(text = title, fontSize = 25.sp)
                    }

                }
            }
        }
    }
}