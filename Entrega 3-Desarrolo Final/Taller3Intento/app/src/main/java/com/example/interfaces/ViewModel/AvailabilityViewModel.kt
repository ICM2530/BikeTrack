package com.example.interfaces.ViewModel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class availabilityViewModel : ViewModel() {

    private val usersRef = FirebaseDatabase.getInstance().getReference("users")
    private val auth = FirebaseAuth.getInstance()

    private val _availability = MutableStateFlow<Boolean?>(null)
    val availability: StateFlow<Boolean?> = _availability

    init {
        loadAvailability()
    }

    private fun loadAvailability() {
        val email = auth.currentUser?.email ?: return

        usersRef.get().addOnSuccessListener { snapshot ->
            for (child in snapshot.children) {
                if (child.child("mail").getValue(String::class.java).equals(email, ignoreCase = true)) {
                    //it.mail.equals(currentUser?.email, ignoreCase = true)

                    val isAvailable = child.child("available")
                        .getValue(Boolean::class.java) ?: false

                    _availability.value = isAvailable
                    return@addOnSuccessListener
                }
            }
        }
    }

    fun toggleAvailability() {
        val current = _availability.value ?: false
        updateAvailability(!current)
    }

    fun updateAvailability(isAvailable: Boolean) {
        val email = auth.currentUser?.email ?: return

        usersRef.get().addOnSuccessListener { snapshot ->
            for (child in snapshot.children) {
                if (child.child("mail").getValue(String::class.java).equals(email, ignoreCase = true)) {

                    val userId = child.key ?: return@addOnSuccessListener

                    usersRef.child(userId).child("available")
                        .setValue(isAvailable)
                        .addOnSuccessListener {


                            _availability.value = isAvailable
                        }

                    return@addOnSuccessListener
                }
            }
        }
    }
}