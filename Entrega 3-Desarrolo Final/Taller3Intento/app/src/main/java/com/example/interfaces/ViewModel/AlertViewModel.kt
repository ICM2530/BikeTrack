package com.example.interfaces.ViewModel
import android.R
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.interfaces.PATH_ALERTS
import com.example.interfaces.database
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MyAlert(
    var Type: String = "Imprudencia",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var identification : String = ""
) {
    constructor() : this(
        "",
        0.0,
        0.0,
        ""
    )
}

class MyAlertViewModel : ViewModel() {

    private val ref = database.getReference(PATH_ALERTS)
    val _alerts = MutableStateFlow<List<MyAlert>>(emptyList())
    val alerts: StateFlow<List<MyAlert>> = _alerts

    init {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children
                    .mapNotNull { it.getValue(MyAlert::class.java) }

                _alerts.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseAPP", error.toString())
            }
        })
    }

    fun saveAlert(alert: MyAlert) {
        val alertId = alert.identification.ifEmpty {
            ref.push().key ?: "unknown"
        }

        ref.child(alertId).setValue(alert)
            .addOnSuccessListener {
                Log.d("FirebaseAPP", "Alerta guardada correctamente")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseAPP", "Error al guardar alerta", e)
            }
    }
}
