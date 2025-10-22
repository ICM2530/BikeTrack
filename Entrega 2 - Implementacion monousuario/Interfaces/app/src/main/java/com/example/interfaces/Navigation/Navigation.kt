package com.example.interfaces.Navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.interfaces.R
import com.example.interfaces.Screens.ActivitiesScreen
import com.example.interfaces.Screens.Chat
import com.example.interfaces.Screens.CommentsScreen
import com.example.interfaces.Screens.Images
import com.example.interfaces.Screens.LoginScreen
import com.example.interfaces.Screens.MainMenu

import com.example.interfaces.Screens.PostDetailScreen
import com.example.interfaces.Screens.RegisScreen
import com.example.interfaces.Screens.SeeFriendTrack
import com.example.interfaces.Screens.UserMenu


enum class AppScreens{
    MainScreen,
    Login,
    Register,
    makeact,
    seeact,
    chat,
    seepublic,
    seecomment,
    seeprofile,
    seefriendtrack,
    changeprofile
}

@Composable
fun Navigation(){
    val navController = rememberNavController()
    var ColorPantalla by remember { mutableStateOf(R.color.MainMenuColor) }
    NavHost(navController = navController, startDestination = AppScreens.Login.name)  {
        composable(route = AppScreens.Login.name){
            LoginScreen(navController)
        }
        composable(route = "${AppScreens.Register.name}/{name}"){ backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            RegisScreen(navController, name)
        }

        composable(route = "${AppScreens.MainScreen.name}/{name}/{image}/{id}/{dAuto}",
            arguments = listOf(
                navArgument("dAuto") { type = NavType.BoolType }
            )
        )
        { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            val im = backStackEntry.arguments?.getString("image")
            val id = backStackEntry.arguments?.getString("id")
            val dAu = backStackEntry.arguments?.getBoolean("dAuto")

            MainMenu(navController, name, im, id = id, darkauto =  dAu == true)
        }

        composable(route = "${AppScreens.seeact.name}/{name}/{image}/{dAuto}",
            arguments = listOf(
                navArgument("dAuto") { type = NavType.BoolType }
            )){ backStackEntry ->

            val name = backStackEntry.arguments?.getString("name")
            val im = backStackEntry.arguments?.getString("image")
            val dAu = backStackEntry.arguments?.getBoolean("dAuto")

            ActivitiesScreen(navController, name, im, dAu == true)
        }

        composable(route = "${AppScreens.seepublic.name}/{name}/{image}/{dAuto}",
            arguments = listOf(
                navArgument("dAuto") { type = NavType.BoolType }
            )){ backStackEntry ->

            val name = backStackEntry.arguments?.getString("name")
            val im = backStackEntry.arguments?.getString("image")
            val dAu = backStackEntry.arguments?.getBoolean("dAuto")

            PostDetailScreen(navController, name, im, dAu == true)
        }
        composable(route = "${AppScreens.seecomment.name}/{name}/{image}/{dAuto}",
            arguments = listOf(
                navArgument("dAuto") { type = NavType.BoolType }
            )){ backStackEntry ->

            val name = backStackEntry.arguments?.getString("name")
            val im = backStackEntry.arguments?.getString("image")
            val dAu = backStackEntry.arguments?.getBoolean("dAuto")

            CommentsScreen(navController, name, im, dAu == true)
        }
        composable(route = "${AppScreens.chat.name}/{name}/{image}/{dAuto}",
            arguments = listOf(
                navArgument("dAuto") { type = NavType.BoolType }
            )){ backStackEntry ->

            val name = backStackEntry.arguments?.getString("name")
            val im = backStackEntry.arguments?.getString("image")
            val dAu = backStackEntry.arguments?.getBoolean("dAuto")

            Chat(navController, name, im, dAu == true)
        }
        composable(route = "${AppScreens.seeprofile.name}/{name}/{image}/{dAuto}",
            arguments = listOf(
                navArgument("dAuto") { type = NavType.BoolType }
            )){ backStackEntry ->

            val name = backStackEntry.arguments?.getString("name")
            val im = backStackEntry.arguments?.getString("image")
            val dAu = backStackEntry.arguments?.getBoolean("dAuto")

            UserMenu(navController, name, im, dAu == true)
        }
        composable(route = "${AppScreens.seefriendtrack.name}/{name}/{image}/{dAuto}",
            arguments = listOf(
                navArgument("dAuto") { type = NavType.BoolType }
            )){ backStackEntry ->

            val name = backStackEntry.arguments?.getString("name")
            val im = backStackEntry.arguments?.getString("image")
            val dAu = backStackEntry.arguments?.getBoolean("dAuto")

            SeeFriendTrack(navController, name, im, dAu == true)
        }
        composable(route = "${AppScreens.changeprofile.name}/{name}/{imageName}/{dAuto}",
            arguments = listOf(
                navArgument("dAuto") { type = NavType.BoolType }
            )
            ){ backStackEntry ->

            val name = backStackEntry.arguments?.getString("name")
            val im = backStackEntry.arguments?.getString("imagename")
            val dAu = backStackEntry.arguments?.getBoolean("dAuto")

            Images(navController, name, im, darkauto = dAu == true)
        }

    }
}