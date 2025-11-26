package com.example.interfaces.ViewModel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import com.example.interfaces.Services.Punto5.Companion.sendUserAvailableNotification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.Manifest
import kotlinx.coroutines.flow.StateFlow

class AvailableUsersViewModel : ViewModel() {

    private val usersRef = FirebaseDatabase.getInstance().getReference("users")

    private val _availableUsers = MutableStateFlow<List<MyUser>>(emptyList())
    val availableUsers: StateFlow<List<MyUser>> = _availableUsers

    lateinit var monitor: UserStateMonViewModel

    fun attachMonitor(m: UserStateMonViewModel) {
        monitor = m
    }

    init {
        startListening()
    }

    private fun startListening() {

        usersRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val list = snapshot.children.mapNotNull { snap ->

                    snap.getValue(MyUser::class.java)?.copy(
                        // ensure null safety
                        mail = snap.child("mail").getValue(String::class.java) ?: "",
                        available = snap.child("available").getValue(Boolean::class.java) ?: false,
                        latitude = snap.child("latitude").getValue(Double::class.java) ?: 0.0,
                        longitude = snap.child("longitude").getValue(Double::class.java) ?: 0.0
                    )
                }


                if (::monitor.isInitialized) {
                    monitor.processUsers(list)
                }

                // Update UI list (only available users)
                _availableUsers.value = list.filter { it.available }
            }


            override fun onCancelled(error: DatabaseError) {
                Log.e("AvailableUsers", "Error: ${error.message}")
            }
        })
    }
}