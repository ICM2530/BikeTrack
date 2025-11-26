package com.example.interfaces.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class UserStateMonViewModel : ViewModel() {
    private val lastAvailability = mutableMapOf<String, Boolean>()
    private val _events = MutableSharedFlow<MyUser>()
    val events = _events.asSharedFlow()

    fun processUsers(users: List<MyUser>) {
        users.forEach { user ->
            val last = lastAvailability[user.mail] ?: false

            if (!last && user.available) {
                viewModelScope.launch {
                    _events.emit(user)
                }
            }

            lastAvailability[user.mail] = user.available
        }
    }
}