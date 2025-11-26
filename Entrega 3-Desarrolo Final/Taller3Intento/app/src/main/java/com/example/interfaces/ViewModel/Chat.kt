package com.example.interfaces.ViewModel

data class Chat(
    val id: String = "",
    val name: String = "",
    val image: String = "",
    val members: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastMessageTime: Long = 0L,
    val createdBy: String = ""
) {
    constructor() : this("", "", "", emptyList(), "", 0L, "")
}

data class Message(
    val id: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderImage: String = "",
    val text: String = "",
    val attachment: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "", "", "", null, 0L)
}

data class ChatState(
    val id: String = "",
    val name: String = "",
    val image: String = "",
    val members: List<String> = emptyList()
)