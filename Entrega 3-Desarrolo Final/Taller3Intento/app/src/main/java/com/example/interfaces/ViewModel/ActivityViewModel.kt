package com.example.interfaces.ViewModel
import android.R
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.interfaces.PATH_ACT
import com.example.interfaces.database
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MyAct(
    var User: String = "",
    var identification : String = "",
    var time: String = "",
    var distance: String = "",
) {
    constructor() : this(
        "",
        "",
        "",
        ""
    )
}

class MyActivityViewModel : ViewModel() {

    private val ref = database.getReference(PATH_ACT)
    val _acts = MutableStateFlow<List<MyAct>>(emptyList())
    val acts: StateFlow<List<MyAct>> = _acts

    init {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children
                    .mapNotNull { it.getValue(MyAct::class.java) }
                _acts.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseAPP", error.toString())
            }
        })
    }

    fun saveAct(ac: MyAct) {
        val actId = ac.identification.ifEmpty {
            ref.push().key ?: "unknown"
        }

        ref.child(actId).setValue(ac)
            .addOnSuccessListener {
                Log.d("FirebaseAPP", "actividad guardada correctamente")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseAPP", "Error al guardar actividad", e)
            }
    }
}
