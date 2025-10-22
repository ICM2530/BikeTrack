package com.example.interfaces.Screens

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

//----

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.interfaces.Navigation.AppScreens
import com.example.interfaces.R


data class comment(
    val User: String,
    val Comment: String,
    val likes: Int,
    val responses: List<comment>
)

data class publication(
    val Owner: String,
    val ImageUri: String,
    val Desc: String,
    val Likes: Int,
    val Comments: List<comment>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    navController: NavHostController,
    name: String? = "Publications",
    im: String?,
    dAu: Boolean?
) {
    val context = LocalContext.current
    var ScreenColor by remember { mutableStateOf(R.color.white) }
    var buttoncolors by remember { mutableStateOf(R.color.MainMenuColor) }
    var textcolors by remember { mutableStateOf(R.color.black) }
    var pubcolors by remember { mutableStateOf(R.color.Dark) }
    var AutoturnDark by remember { mutableStateOf(false) }


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
                    if(dAu == true) {
                        AutoturnDark = if (lux < 2000) true else false
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }



    if(dAu == true && AutoturnDark){
        buttoncolors = R.color.DarkButtons
        ScreenColor = R.color.Dark
        textcolors = R.color.white
        pubcolors = R.color.BackLight
    }
    else{
        buttoncolors = buttoncolors
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


    Scaffold(
        containerColor = colorResource(ScreenColor),
        // Barra superior con iconos de notificación
        topBar = { PostDetailTopBar(navController,name, im, dAu == true, buttoncolors, ScreenColor, textcolors, pubcolors) } // postDetailTopBar es una función que define la barra superior
    ) { paddingValues ->

        val list = Array<String>(200){i -> "${i + 1}"}
        LazyColumn (
            verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        )
        {items(list) { item ->
                val randomnum = (1..200).random()
                Column(
                    modifier = Modifier

                        .padding(paddingValues)

                ) {
                    // Header con información del usuario
                    UserHeader(randomnum.toString(), navController, name, im, dAu == true, buttoncolors, ScreenColor, textcolors, pubcolors)

                    // Card principal con la imagen del post
                    PostImageCard(pubcolors)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botones de acción (like, comment, share, bookmark)
                    ActionButtons(navController, name, im, dAu == true, textcolors)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Texto "View previous comments"
                    Text(
                        "View previous comments...",
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Input simulado de comentario
                    CommentInput()
                }
            }
        }

    }
}

/**
 * Barra superior del post detail con iconos de notificación
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailTopBar(navController: NavHostController, name: String? = "Publications", im: String?, dAu: Boolean, buttoncolors: Int, screencolors: Int, textcolors: Int, pubcolors: Int) {
    var expanded1 by remember {mutableStateOf(false)}

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(buttoncolors),
        ),
        navigationIcon = {
            IconButton(onClick = {
                navController.navigate(route = "${AppScreens.MainScreen.name}/defaultUser/default/default/${dAu}")
            }, colors = IconButtonColors(
                contentColor = Color.White,
                containerColor = colorResource(buttoncolors),
                disabledContainerColor = Color.White,
                disabledContentColor = Color.White
            )
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Returning to main menu")
            }
        },
        title = {Text("Publications", color = Color.White, fontWeight = Bold)},
        actions = {

            Box(){
                IconButton(onClick = {
                    navController.navigate(route = "${AppScreens.chat.name}/$name/$im/${dAu}")
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
                    if(expanded1){
                        expanded1 = false
                    }
                    else{
                        expanded1 = true
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
                    expanded = expanded1,
                    onDismissRequest = { expanded1 = false}
                ) {
                    DropdownMenuItem(
                        text = {Text("Friend User4456 has sent a tracker to you")},
                        onClick = {navController.navigate(route = "${AppScreens.seefriendtrack.name}/$name/$im/${dAu}")}
                    )
                }
            }

        }
    )
}

/**
 * Header con información del usuario (foto, nombre, botón follow)
 */
@Composable
fun UserHeader(
    item: String,
    navController: NavHostController,
    name: String? = "Publications",
    im: String?
    , dAu: Boolean,
    buttoncolors: Int, screencolors: Int, textcolors: Int, pubcolors: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Foto de perfil circular (simulada con icono)
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.Black)
        ) {
            IconButton(onClick = {
                navController.navigate(route = "${AppScreens.seeprofile.name}/$name/$im/${dAu}")
            }, colors = IconButtonColors(
                contentColor = Color.White,
                containerColor = Color.Black,
                disabledContainerColor = Color.White,
                disabledContentColor = Color.White
            ) )
            { Icon(
                Icons.Default.Person,
                contentDescription = "Profile",
                tint = Color.White,
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.Center)
            )}


        }

        Spacer(modifier = Modifier.width(16.dp))

        // Nombre del usuario
        Text(
            text = "UserName" + item,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            color = colorResource(textcolors)
        )

        // Botón Follow
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(buttoncolors)
            )
        ) {
            Text("Follow", color = Color.White)
        }

        // Botón de más opciones
        IconButton(onClick = { }) {
            Icon(Icons.Default.MoreVert, "More options",
                tint = colorResource(textcolors))
        }
    }
}

/**
 * Card principal con la imagen del post y dots de la pagina
 */
@Composable
fun PostImageCard(pubcolors: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(pubcolors)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Imagen principal (simulada con un box de color)
            Card(
                modifier = Modifier.size(200.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF8D6E63)) // Color marron para simular imagen
                ) {
                    Text(
                        " Cycling",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // dots de paginación (3 puntos, el del medio activo)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                // El punto del medio (índice 1) es negro, los demás grises
                                if (index == 1) Color.Black else Color.Gray
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Fila de botones de acción (like, comment, share)
 */
@Composable
fun ActionButtons(navController: NavHostController, name: String? = "Publications", im: String?, dAu: Boolean, textcolors: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón de Like (corazón rojo con número 1)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { }
        ) {
            Icon(
                Icons.Default.Favorite, // esto es un corazón
                contentDescription = "Like",
                tint = Color.Red,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("1", color = colorResource(textcolors))
        }

        Spacer(modifier = Modifier.width(24.dp))

        // Botón de Comment (burbuja negra con número 1)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = {
                navController.navigate(route = "${AppScreens.seecomment.name}/$name/$im/${dAu}")
            }, colors = IconButtonColors(
                contentColor = Color.White,
                containerColor = Color.White,
                disabledContainerColor = Color.White,
                disabledContentColor = Color.White
            )
            ) {
                Icon(
                    Icons.Filled.Comment,
                    contentDescription = "Message",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))
            Text("1", color = colorResource(textcolors))
        }

        Spacer(modifier = Modifier.width(24.dp))

        // Botón de Share/Filter
        Icon(
            Icons.Default.Share,
            contentDescription = "Share",
            modifier = Modifier.size(24.dp),
            tint = colorResource(textcolors)
        )
        // Espacio flexible para empujar el guardado a la derecha
        Spacer(modifier = Modifier.weight(1f))

        // Botón de Guardado (bookmark)
        Icon(
            Icons.Filled.Save,
            contentDescription = "Save",
            modifier = Modifier.size(24.dp),
            tint = colorResource(textcolors)
        )


    }
}

/**
 * Input de comentario simulado en la parte inferior
 */
@Composable
fun CommentInput() {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "UserName",
                fontWeight = Bold
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "Message Description",
                color = Color.Gray,
                modifier = Modifier.weight(1f)
            )
        }
    }
}



