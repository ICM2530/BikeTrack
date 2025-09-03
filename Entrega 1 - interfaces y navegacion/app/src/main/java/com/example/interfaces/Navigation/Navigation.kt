package com.example.interfaces.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.interfaces.ActivitiesScreen
import com.example.interfaces.Chat
import com.example.interfaces.CommentsScreen
import com.example.interfaces.LoginScreen
import com.example.interfaces.MainMenu
import com.example.interfaces.MakeActivity
import com.example.interfaces.PostDetailScreen
import com.example.interfaces.RegisScreen
import com.example.interfaces.SeeFriendTrack
import com.example.interfaces.TrackView
import com.example.interfaces.UserMenu


enum class AppScreens{
    MainScreen,
    Login,
    Register,
    makeact,
    seeact,
    seetrack,
    chat,
    seepublic,
    seecomment,
    seeprofile,
    seefriendtrack
}

@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.Login.name)  {
        composable(route = AppScreens.Login.name){
            LoginScreen(navController)
        }
        composable(route = "${AppScreens.Register.name}/{name}"){ backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            RegisScreen(navController, name)
        }

        composable(route = "${AppScreens.MainScreen.name}/{name}"){ backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            MainMenu(navController, name)
        }
        composable(route = "${AppScreens.makeact.name}/{name}"){ backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            MakeActivity(navController, name)
        }
        composable(route = "${AppScreens.seeact.name}/{name}"){ backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            ActivitiesScreen(navController, name)
        }
        composable(route = "${AppScreens.seetrack.name}/{name}/{acname}"){ backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            val aname = backStackEntry.arguments?.getString("acname")
            TrackView(navController, name, aname)
        }
        composable(route = "${AppScreens.seepublic.name}/{name}"){ backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            PostDetailScreen(navController, name)
        }
        composable(route = "${AppScreens.seecomment.name}/{name}"){ backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            CommentsScreen(navController, name)
        }
        composable(route = "${AppScreens.chat.name}/{name}"){ backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            Chat(navController, name)
        }
        composable(route = "${AppScreens.seeprofile.name}/{name}"){ backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            UserMenu(navController, name)
        }
        composable(route = "${AppScreens.seefriendtrack.name}/{name}"){ backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            SeeFriendTrack(navController, name)
        }

    }
}