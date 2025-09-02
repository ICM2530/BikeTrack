package com.example.interfaces

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun CommentsScreen(navController: NavHostController, name: String? = "Usual") {
    // Lista de comentarios simulados
    val comments = remember {
        listOf(
            CommentData("UserName", "Message Description", "2 h"),
            CommentData("UserName", "Message Description", "1 h"),
            CommentData("UserName", "Message Description", "2 h"),
            CommentData("UserName", "Message Description", "2 h")
        )
    }

    Scaffold(
        // Barra superior con título y botón de regreso
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Comments",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(route = "${AppScreens.seepublic.name}/$name")
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.MainMenuColor)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // Texto "View previous comments"
            Text(
                "View previous comments...",
                color = colorResource(R.color.MainMenuColor),
                modifier = Modifier.padding(16.dp), fontWeight = Bold
            )

            // Lista de comentarios
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(comments) { comment ->
                    CommentItem(comment = comment)
                }

                item {
                    SeeMoreAnswersButton()
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
fun CommentItem(comment: CommentData) {
    Column {
        // Card del comentario
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)),
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
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        comment.message,
                        color = Color.Gray,
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
                    Text("1", fontSize = 12.sp)
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
                    Text("1", fontSize = 12.sp)

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
fun SeeMoreAnswersButton() {
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
            color = colorResource(R.color.MainMenuColor)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            "See more answers",
            color = Color.Black,
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
@Preview(showBackground = true)
@Composable
fun CommentsPreview() {
    val navController = rememberNavController()
    CommentsScreen(navController)
}