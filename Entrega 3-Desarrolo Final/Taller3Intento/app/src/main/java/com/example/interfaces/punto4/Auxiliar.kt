package com.example.interfaces.punto4

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.interfaces.ViewModel.MyUser
import com.example.interfaces.ViewModel.MyUserViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.database

@Composable
fun SaveUserScreen(
    viewModel: MyUserViewModel = viewModel()
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Button(
                onClick = {
                    val newUser = MyUser(
                        name = "Admin",
                        lastName = "Lulu",
                        mail = "1@1.com",
                        pass = "12345",
                        uri = "default",
                        latitude = 4.628196206265264,
                        longitude = -74.06545308630227,
                        identification = "111111",
                        available = true
                    )
                    viewModel.saveUser(newUser)
                    val database = Firebase.database
                    val myRef = database.getReference("message")

                    myRef.setValue("Hello, World!")
                }
            ) {
                Text("Guardar usuario")
            }
        }
    }
}