package com.example.interfaces.Services

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.example.interfaces.ViewModel.Chat
import com.example.interfaces.ViewModel.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.File

object FirebaseUtil {
    private val database = FirebaseDatabase.getInstance()
    private val chatsRef = database.getReference("chats")
    private val messagesRef = database.getReference("messages")
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Obtener chats del usuario actual (usando EMAIL en lugar de UID)
    fun getUserChats(onResult: (List<Chat>) -> Unit) {
        val currentUserEmail = auth.currentUser?.email ?: return

        chatsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chats = mutableListOf<Chat>()
                for (chatSnapshot in snapshot.children) {
                    val chat = chatSnapshot.getValue(Chat::class.java)
                    // Verificar si el EMAIL del usuario está en la lista de miembros
                    if (chat != null && chat.members.contains(currentUserEmail)) {
                        chats.add(chat)
                    }
                }
                onResult(chats.sortedByDescending { it.lastMessageTime })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseUtil", "Error getting chats: ${error.message}")
            }
        })
    }

    // Crear un nuevo chat (usando EMAILS como members)
    fun createChat(
        name: String,
        members: List<String>, // Ahora son EMAILS
        image: String = "",
        onSuccess: (String) -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val chatId = chatsRef.push().key ?: return
        val currentUserEmail = auth.currentUser?.email ?: return

        val chat = Chat(
            id = chatId,
            name = name,
            image = image,
            members = members, // Lista de EMAILS
            createdBy = currentUserEmail, // EMAIL del creador
            lastMessage = "Chat creado",
            lastMessageTime = System.currentTimeMillis()
        )

        chatsRef.child(chatId).setValue(chat)
            .addOnSuccessListener {
                Log.d("FirebaseUtil", "Chat creado: $chatId con miembros: $members")
                onSuccess(chatId)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseUtil", "Error creando chat", e)
                onFailure(e)
            }
    }

    // Obtener mensajes de un chat
    fun getMessages(chatId: String, onMessageReceived: (Message?) -> Unit) {
        messagesRef.child(chatId).orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (messageSnapshot in snapshot.children) {
                        val message = messageSnapshot.getValue(Message::class.java)
                        onMessageReceived(message)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseUtil", "Error getting messages: ${error.message}")
                    onMessageReceived(null)
                }
            })
    }

    // Enviar mensaje
    fun sendMessage(
        context: Context,
        chatId: String,
        message: Message,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val currentUser = auth.currentUser ?: return
        val messageId = messagesRef.child(chatId).push().key ?: return

        val fullMessage = message.copy(
            id = messageId,
            chatId = chatId,
            senderId = currentUser.email ?: currentUser.uid, // Usar email preferentemente
            senderName = currentUser.displayName ?: currentUser.email ?: "Usuario",
            timestamp = System.currentTimeMillis()
        )

        // Si hay adjunto, subirlo primero
        if (message.attachment != null && message.attachment.isNotEmpty()) {
            uploadAttachment(Uri.parse(message.attachment), chatId, messageId) { downloadUrl ->
                val messageWithAttachment = fullMessage.copy(attachment = downloadUrl)
                saveMessage(chatId, messageId, messageWithAttachment, onSuccess, onFailure)
            }
        } else {
            saveMessage(chatId, messageId, fullMessage, onSuccess, onFailure)
        }
    }

    private fun saveMessage(
        chatId: String,
        messageId: String,
        message: Message,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        messagesRef.child(chatId).child(messageId).setValue(message)
            .addOnSuccessListener {
                // Actualizar último mensaje del chat
                chatsRef.child(chatId).updateChildren(
                    mapOf(
                        "lastMessage" to message.text.ifEmpty { "Archivo adjunto" },
                        "lastMessageTime" to message.timestamp
                    )
                )
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseUtil", "Error enviando mensaje", e)
                onFailure(e)
            }
    }

    // Subir archivo adjunto
    private fun uploadAttachment(
        uri: Uri,
        chatId: String,
        messageId: String,
        onSuccess: (String) -> Unit
    ) {
        val storageRef = storage.reference
            .child("chat_attachments")
            .child(chatId)
            .child(messageId)

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    onSuccess(downloadUri.toString())
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseUtil", "Error subiendo adjunto", e)
                onSuccess("") // Enviar sin adjunto si falla
            }
    }

    // Descargar archivo adjunto
    fun downloadAttachment(
        context: Context,
        attachmentUrl: String,
        onComplete: (Boolean) -> Unit
    ) {
        try {
            val storageRef = storage.getReferenceFromUrl(attachmentUrl)
            val fileName = storageRef.name
            val downloadDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            )
            val localFile = File(downloadDir, fileName)

            storageRef.getFile(localFile)
                .addOnSuccessListener {
                    Log.d("FirebaseUtil", "Archivo descargado: ${localFile.absolutePath}")
                    onComplete(true)
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseUtil", "Error descargando archivo", e)
                    onComplete(false)
                }
        } catch (e: Exception) {
            Log.e("FirebaseUtil", "Error en downloadAttachment", e)
            onComplete(false)
            }
        }
}