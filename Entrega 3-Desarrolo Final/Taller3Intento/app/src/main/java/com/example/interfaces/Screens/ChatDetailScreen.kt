package com.example.interfaces.Screens

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.interfaces.R
import com.example.interfaces.Services.FirebaseUtil
import com.example.interfaces.ViewModel.Chat
import com.example.interfaces.ViewModel.ChatState
import com.example.interfaces.ViewModel.Message
import com.example.interfaces.database
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    navController: NavHostController,
    chatId: String,
    modifier: Modifier = Modifier
) {
    var messageText by remember { mutableStateOf("") }
    var attachmentUri by remember { mutableStateOf<Uri?>(null) }
    var chatState by remember { mutableStateOf(ChatState(id = chatId)) }
    val context = LocalContext.current
    val messages = remember { mutableStateListOf<Message>() }

    // IMPORTANTE: Usar EMAIL para comparar
    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
    val listState = rememberLazyListState()

    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        attachmentUri = uri
    }

    val requestStorage = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Toast.makeText(context, "Permiso concedido", Toast.LENGTH_SHORT).show()
        }
    }

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
                        MainColor = R.color.textGT
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

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LaunchedEffect(chatId) {
        database.getReference("chats").child(chatId).get()
            .addOnSuccessListener { snapshot ->
                val chat = snapshot.getValue(Chat::class.java)
                chat?.let {
                    chatState = ChatState(
                        id = it.id,
                        name = it.name,
                        image = it.image,
                        members = it.members
                    )
                }
            }
    }

    LaunchedEffect(chatId) {
        FirebaseUtil.getMessages(chatId) { message ->
            if (message == null) return@getMessages
            val index = messages.indexOfFirst { it.id == message.id }
            if (index == -1) {
                messages.add(message)
            } else {
                messages[index] = message
            }
        }
    }

    Scaffold(
        containerColor = colorResource(MainColor),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (chatState.image.isNotEmpty()) {
                            AsyncImage(
                                model = chatState.image,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        Column {
                            Text(
                                chatState.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${chatState.members.size} miembros",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.MainMenuColor),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colorResource(MainColor))
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth().background(colorResource(MainColor)),
                state = listState,
                contentPadding = PaddingValues(vertical = 8.dp),

            ) {
                items(messages) { message ->
                    val isMyMessage = message.senderId == currentUserEmail

                    MessageItem(
                        message = message,
                        isCurrentUser = isMyMessage,
                        onDownloadClicked = { attachment ->
                            requestStorage.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            FirebaseUtil.downloadAttachment(context, attachment) { success ->
                                Toast.makeText(
                                    context,
                                    if (success) "Descarga completa" else "Error",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }
            }

            if (attachmentUri != null) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(R.color.MainMenuColor).copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AttachFile,
                            null,
                            tint = colorResource(R.color.MainMenuColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Archivo adjunto",
                            modifier = Modifier.weight(1f),
                            color = colorResource(R.color.MainMenuColor)
                        )
                        IconButton(onClick = { attachmentUri = null }) {
                            Icon(Icons.Default.Close, "Eliminar", tint = Color.Gray)
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    IconButton(
                        onClick = { filePicker.launch("/") },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.AttachFile,
                            contentDescription = "Adjuntar",
                            tint = colorResource(R.color.MainMenuColor)
                        )
                    }

                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        placeholder = { Text("Escribe un mensaje...") },
                        maxLines = 4,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF0F0F0),
                            unfocusedContainerColor = Color(0xFFF0F0F0),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )

                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank() || attachmentUri != null) {
                                FirebaseUtil.sendMessage(
                                    context,
                                    chatId,
                                    Message(
                                        text = messageText,
                                        attachment = attachmentUri?.toString()
                                    )
                                )
                                messageText = ""
                                attachmentUri = null
                            }
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Enviar",
                            tint = colorResource(R.color.MainMenuColor)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageItem(
    message: Message,
    isCurrentUser: Boolean,
    onDownloadClicked: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
        ) {
            if (!isCurrentUser) {
                if (message.senderImage.isNotEmpty()) {
                    AsyncImage(
                        model = message.senderImage,
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(
                modifier = Modifier.widthIn(max = 280.dp),
                horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
            ) {
                if (!isCurrentUser && message.senderName.isNotEmpty()) {
                    Text(
                        text = message.senderName,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(R.color.MainMenuColor),
                        modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCurrentUser)
                            colorResource(R.color.MainMenuColor)  // Verde/Azul para ti
                        else
                            Color.White
                    ),
                    shape = RoundedCornerShape(
                        topStart = if (isCurrentUser) 16.dp else 4.dp,
                        topEnd = if (isCurrentUser) 4.dp else 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        if (message.text.isNotEmpty()) {
                            Text(
                                text = message.text,
                                color = if (isCurrentUser) Color.White else Color.Black,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        if (message.attachment != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onDownloadClicked(message.attachment!!) }
                                    .padding(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Download,
                                    contentDescription = null,
                                    tint = if (isCurrentUser) Color.White else colorResource(R.color.MainMenuColor),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Descargar",
                                    color = if (isCurrentUser) Color.White else colorResource(R.color.MainMenuColor),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault())
                        .format(Date(message.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}