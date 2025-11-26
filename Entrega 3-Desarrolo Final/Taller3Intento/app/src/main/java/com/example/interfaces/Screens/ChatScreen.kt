package com.example.interfaces.Screens


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.interfaces.Navigation.AppScreens
import com.example.interfaces.R
import com.example.interfaces.Services.FirebaseUtil
import com.example.interfaces.ViewModel.Chat
import com.example.interfaces.ViewModel.MyUser
import com.example.interfaces.database
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MyBotBar3(navController: NavHostController) {
    BottomAppBar(
        modifier = Modifier.height(80.dp).shadow(
            elevation = 10.dp,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ),
        containerColor = Color.White,
        tonalElevation = 6.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = { navController.navigate(AppScreens.profile.name) },
                modifier = Modifier.padding(horizontal = 20.dp).size(50.dp)
            ) {
                Icon(Icons.Outlined.AccountCircle, contentDescription = "Account")
            }
            IconButton(
                onClick = { navController.navigate(AppScreens.punto2.name) },
                modifier = Modifier.padding(horizontal = 20.dp).size(50.dp)
            ) {
                Icon(Icons.Outlined.Home, contentDescription = "Activity")
            }
            IconButton(
                onClick = {},
                modifier = Modifier.padding(horizontal = 20.dp).size(50.dp)
            ) {
                Icon(
                    Icons.Filled.Chat,
                    contentDescription = "Chat",
                    tint = colorResource(R.color.MainMenuColor)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var chatList by remember { mutableStateOf<List<Chat>>(emptyList()) }
    var showCreateDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    //--COLORES
    var MainColor by remember { mutableStateOf(R.color.white) }
    var MainTextcolor by remember { mutableStateOf(R.color.black) }

    //--------_
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
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
                    }else{
                        MainColor = R.color.MainMenuColor
                        MainTextcolor = R.color.white
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

    LaunchedEffect(Unit) {
        FirebaseUtil.getUserChats { chatList = it }
    }

    Scaffold(
        containerColor = colorResource(MainColor),
        topBar = {
            TopAppBar(
                title = { Text("Chats", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.MainMenuColor),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = { MyBotBar3(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = colorResource(R.color.MainMenuColor)
            ) {
                Icon(Icons.Default.Add, "Crear chat", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (chatList.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.ChatBubbleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No tienes chats aún",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Presiona + para crear uno",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(chatList) { chat ->
                        ChatItem(
                            chat = chat,
                            onClick = {
                                navController.navigate("${AppScreens.ChatDetail.name}/${chat.id}")
                            }
                        )
                    }
                }
            }
        }

        if (showCreateDialog) {
            CreateChatDialog(
                onDismiss = { showCreateDialog = false },
                onChatCreated = { chatId ->
                    showCreateDialog = false
                    navController.navigate("${AppScreens.ChatDetail.name}/$chatId")
                }
            )
        }
    }
}

@Composable
fun ChatItem(chat: Chat, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (chat.image.isNotEmpty()) {
                AsyncImage(
                    model = chat.image,
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .padding(8.dp),
                    tint = colorResource(R.color.MainMenuColor)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chat.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = chat.lastMessage.ifEmpty { "Sin mensajes" },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1
                )
                Text(
                    text = "${chat.members.size} miembros",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            if (chat.lastMessageTime > 0) {
                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault())
                        .format(Date(chat.lastMessageTime)),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateChatDialog(
    onDismiss: () -> Unit,
    onChatCreated: (String) -> Unit
) {
    var chatName by remember { mutableStateOf("") }
    var selectedMembers by remember { mutableStateOf<Set<String>>(emptySet()) }
    var availableUsers by remember { mutableStateOf<List<MyUser>>(emptyList()) }
    var currentUserEmail by remember { mutableStateOf("") }

    // Obtener el email del usuario actual
    LaunchedEffect(Unit) {
        currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""

        database.getReference("users").get().addOnSuccessListener { snapshot ->
            val users = mutableListOf<MyUser>()
            for (userSnapshot in snapshot.children) {
                val user = userSnapshot.getValue(MyUser::class.java)
                // Filtrar para no mostrar al usuario actual en la lista
                if (user != null && user.mail != currentUserEmail) {
                    users.add(user)
                }
            }
            availableUsers = users
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear Chat Grupal") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = chatName,
                    onValueChange = { chatName = it },
                    label = { Text("Nombre del chat") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Seleccionar miembros:", fontWeight = FontWeight.Bold)

                LazyColumn(modifier = Modifier.height(200.dp)) {
                    items(availableUsers) { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedMembers = if (user.mail in selectedMembers) {
                                        selectedMembers - user.mail
                                    } else {
                                        selectedMembers + user.mail
                                    }
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = user.mail in selectedMembers,
                                onCheckedChange = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${user.name} ${user.lastName} (${user.mail})")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (chatName.isNotBlank() && selectedMembers.isNotEmpty()) {
                        // Agregar el email del usuario actual a la lista de miembros
                        val allMembers = selectedMembers + currentUserEmail

                        FirebaseUtil.createChat(
                            name = chatName,
                            members = allMembers.toList(),
                            onSuccess = onChatCreated,
                            onFailure = { /* Handle error */ }
                        )
                    }
                },
                enabled = chatName.isNotBlank() && selectedMembers.isNotEmpty()
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
            }
        )
}