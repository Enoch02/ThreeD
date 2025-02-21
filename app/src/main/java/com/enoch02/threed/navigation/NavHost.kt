package com.enoch02.threed.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.enoch02.threed.ui.screen.custom.CustomModelScreen
import com.enoch02.threed.ui.screen.demo.RenderScreen
import com.enoch02.threed.ui.screen.home.HomeScreen

@Composable
fun TDNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Destination.Home.route,
        builder = {
            composable(Destination.Home.route) {
                HomeScreen(navController = navController)
            }

            composable(Destination.DemoScene.route) {
                RenderScreen(navController = navController)
            }

            composable(Destination.LoadModel.route) {
                CustomModelScreen(navController = navController)
            }
        }
    )
}