package com.charan.readlater.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.charan.readlater.presentation.home.HomeScreen
import com.charan.readlater.presentation.authentication.AuthenticationScreen

@Composable
fun NavAppHost(
    navHostController: NavHostController
) {
    NavHost(
        navController = navHostController,
        startDestination = AuthenticationScreenNav

    ) {
        composable <AuthenticationScreenNav>{
            AuthenticationScreen(
                navigateToHome = {
                    navHostController.navigate(HomeScreenNav)
                }
            )
        }

        composable <HomeScreenNav>{
            HomeScreen()
        }
    }

}