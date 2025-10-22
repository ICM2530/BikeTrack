package com.example.interfaces.ViewModel

import android.R
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.interfaces.PATH_USERS
import com.example.interfaces.database
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.flow.MutableStateFlow

data class MyUser(val name: String = "", val age: String = "0", val id: String = "", val uri: String = "default", val mail: String = "mail")

class MyUserViewModel: ViewModel(){
    val myref = database.getReference(PATH_USERS)
    var _users = MutableStateFlow(listOf<MyUser>())

    val vel : ValueEventListener = myref.addValueEventListener(object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            val newList = mutableListOf<MyUser>()
            for(child in snapshot.children){
                val user = child.getValue<MyUser>()
                user?.let {
                    newList.add(user)
                }
            }
            _users.value = newList
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("FirebaseAPP",error.toString())
        }
    })
}