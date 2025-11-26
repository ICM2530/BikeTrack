package com.example.interfaces.ViewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.interfaces.PATH_USERS
import com.example.interfaces.database
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

data class MyUser(
    var name: String = "",
    var lastName: String = "",
    var mail: String = "",
    var pass: String = "",
    var uri: String = "default",
    var age: String = "0",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var identification: String = "",
    var available: Boolean = false
) {
    constructor() : this("",
        "",
        "",
        "",
        "default",
        "0",
        0.0,
        0.0,
        "",
        false)
}

class MyUserViewModel : ViewModel() {

    private val ref = database.getReference(PATH_USERS)
    private val storageRef = FirebaseStorage.getInstance().reference
    private val _users = MutableStateFlow<List<MyUser>>(emptyList())
    val users: StateFlow<List<MyUser>> = _users

    init {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children
                    .mapNotNull { it.getValue(MyUser::class.java) }

                _users.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseAPP", error.toString())
            }
        })
    }

    fun uploadImageAndSaveUser(imageUri: Uri?, user: MyUser, onComplete: (Boolean, String?) -> Unit) {
        if (imageUri == null || imageUri.toString() == "default") {
            // Si no hay imagen, guardar usuario directamente
            saveUser(user)
            onComplete(true, "default")
            return
        }

        // Crea una Uri ranndom con la que se conocera en la bd owo
        val imageName = "profile_images/${user.identification}_${UUID.randomUUID()}.jpg"
        val imageRef = storageRef.child(imageName)

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                // Obtener URL de descarga
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()

                    // Actualizar usuario con la URL de la imagen
                    val updatedUser = user.copy(uri = imageUrl)
                    saveUser(updatedUser)

                    onComplete(true, imageUrl)

                    Log.d("FirebaseAPP", "Imagen subida correctamente: $imageUrl")
                }.addOnFailureListener { e ->
                    onComplete(false, null)
                    Log.e("FirebaseAPP", "Error al obtener URL de descarga", e)
                }
            }
            .addOnFailureListener { e ->
                onComplete(false, null)
                Log.e("FirebaseAPP", "Error al subir imagen", e)
            }
    }

    fun saveUser(user: MyUser) {
        val userId = user.identification.ifEmpty {
            ref.push().key ?: "unknown"
        }

        ref.child(userId).setValue(user)
            .addOnSuccessListener {
                Log.d("FirebaseAPP", "Usuario guardado correctamente")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseAPP", "Error al guardar usuario", e)
            }
    }

    fun deleteImage(imageUrl: String) {
        if (imageUrl == "default" || imageUrl.isEmpty()) return

        try {
            val imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
            imageRef.delete()
                .addOnSuccessListener {
                    Log.d("FirebaseAPP", "Imagen eliminada correctamente")
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseAPP", "Error al eliminar imagen", e)
                }
        } catch (e: Exception) {
            Log.e("FirebaseAPP", "Error al procesar URL de imagen", e)
        }
    }
}