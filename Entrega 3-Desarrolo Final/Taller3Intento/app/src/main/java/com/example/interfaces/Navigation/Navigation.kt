package com.example.interfaces.Navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.interfaces.R
import com.example.interfaces.Screens.LoginScreen
import com.example.interfaces.Screens.Punto2Screen
import com.example.interfaces.Screens.RegisScreen
import com.example.interfaces.Screens.TrackUserScreen
import com.example.interfaces.ViewModel.MyUserViewModel
import com.example.interfaces.Screens.AvailableUsersListScreen
import com.example.interfaces.Screens.ChatDetailScreen
import com.example.interfaces.Screens.ChatsScreen
import com.example.interfaces.Screens.EditProfileScreen
import com.example.interfaces.Screens.ProfileScreen
import com.example.interfaces.punto4.SaveUserScreen



enum class AppScreens{
        MainScreen,
        Login,
        Register,
        seefriendtrack,
        punto2,
        AvailableUsers,
        TrackUser,
        saveUser,
        profile,
        editProfile,
        Chats,
        ChatDetail
    }

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    var ColorPantalla by remember { mutableStateOf(R.color.MainMenuColor) }
    NavHost(navController = navController, startDestination = AppScreens.Login.name)  {
        composable(route = AppScreens.Login.name){
            LoginScreen(navController)
        }
        composable(route = AppScreens.punto2.name){
            Punto2Screen(navController)
        }
        composable(route = AppScreens.AvailableUsers.name){
            AvailableUsersListScreen(navController)
        }
        composable(route = AppScreens.profile.name){
            ProfileScreen(navController)
        }

        composable(
            route = "${AppScreens.editProfile.name}/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            EditProfileScreen(navController, userId)
        }



        //--------------
        composable(route = "${AppScreens.Register.name}/{name}"){ backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            RegisScreen(navController, name)
        }


        composable(route = AppScreens.saveUser.name) {
            SaveUserScreen()
        }


        composable(route = "${AppScreens.seefriendtrack.name}/{name}/{image}/{dAuto}",
            arguments = listOf(
                navArgument("dAuto") { type = NavType.BoolType }
            )){ backStackEntry ->

            val name = backStackEntry.arguments?.getString("name")
            val im = backStackEntry.arguments?.getString("image")
            val dAu = backStackEntry.arguments?.getBoolean("dAuto")
        }
        composable(route = AppScreens.Chats.name) {
            ChatsScreen(navController = navController)
        }

        composable(
            route = "${AppScreens.ChatDetail.name}/{chatId}",
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            ChatDetailScreen(
                navController = navController,
                chatId = chatId
            )
        }


    }
}