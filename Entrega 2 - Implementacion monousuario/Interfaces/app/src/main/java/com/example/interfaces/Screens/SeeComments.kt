package com.example.interfaces.Screens

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.interfaces.Navigation.AppScreens
import com.example.interfaces.R

/**
 * Data class para representar un comentario
 */
data class CommentData(
    val userName: String,
    val message: String,
    val timeAgo: String
)
/**
 * INTERFACE 4 - COMMENTS SCREEN
 * Pantalla de comentarios con lista scrolleable
 * Incluye: header, lista de comentarios, botón "see more answers"
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsScreen(
    navController: NavHostController,
    name: String? = "Usual",
    im: String?,
    dAu: Boolean?
) {
    // Lista de comentarios simulados
    val comments = remember {
        listOf(
            CommentData("UserName", "Message Description", "2 h"),
            CommentData("UserName", "Message Description", "1 h"),
            CommentData("UserName", "Message Description", "2 h"),
            CommentData("UserName", "Message Description", "2 h")
        )
    }
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
        pubcolors = R.color.PubDark
    }
    else{
        buttoncolors = buttoncolors
        ScreenColor = R.color.white
        textcolors = R.color.black
        pubcolors = R.color.BackLight
    }




    DisposableEffect(Unit) {
        sensorManager.registerListener(sensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }
    Scaffold(
        containerColor = colorResource(ScreenColor),
        // Barra superior con título y botón de regreso
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Comments",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(route = "${AppScreens.seepublic.name}/$name/$im/$dAu")
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(buttoncolors)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colorResource(ScreenColor))
        ) {
            // Texto "View previous comments"
            Text(
                "View previous comments...",
                color = colorResource(buttoncolors),
                modifier = Modifier.padding(16.dp), fontWeight = Bold
            )

            // Lista de comentarios
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {items(comments){comment ->
                CommentItem(comment = comment, textcolors, pubcolors)

            }
                item {
                    SeeMoreAnswersButton(buttoncolors, textcolors)
                }
            }
        }
    }
}



/**
 * Item individual de comentario
 * Incluye: foto de perfil, nombre, mensaje, tiempo, botones like/reply
 */
@Composable
fun CommentItem(comment: CommentData, textColors : Int, pubcolors:Int) {
    Column {
        // Card del comentario
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colorResource(pubcolors)),
            shape = RoundedCornerShape(16.dp) // Bordes redondeados
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Foto de perfil (simulada con icono)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Nombre y mensaje del comentario
                Column(modifier = Modifier.weight(1f)) {
                    val randomval = (1..500).random()
                    Text(
                        comment.userName + randomval,
                        fontWeight = Bold,
                        fontSize = 14.sp,
                        color = colorResource(textColors)
                    )
                    Text(
                        comment.message,
                        color = colorResource(pubcolors),
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Fila con tiempo y botones de acción
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tiempo del comentario
            Text(
                comment.timeAgo,
                color = Color.Gray,
                fontSize = 12.sp
            )

            // Botones de like y reply
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Botón Like
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { }
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Like",
                        tint = Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("1", fontSize = 12.sp, color = colorResource(textColors))
                }

                // Botón Reply
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { }
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Reply",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("1", fontSize = 12.sp, color = colorResource(textColors))

                }

            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
}


/**
 * Botón "See more answers" con línea divisoria
 */
@Composable
fun SeeMoreAnswersButton(buttoncolors : Int, textColors : Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { },
        verticalAlignment = Alignment.CenterVertically
    ) {

        HorizontalDivider(
            modifier = Modifier.width(60.dp),
            thickness = 2.dp,
            color = colorResource(buttoncolors)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            "See more answers",
            color = colorResource(textColors),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            Icons.Default.KeyboardArrowDown,
            "Expand",
            tint = Color.Black
        )
    }
}

