package com.charan.readlater.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.charan.readlater.presentation.home.HomeScreen
import com.charan.readlater.presentation.authentication.AuthenticationScreen
import com.charan.readlater.presentation.settings.SettingsScreen
import com.charan.readlater.presentation.settings.account.AccountScreen

@Composable
fun NavAppHost(
    navHostController: NavHostController,
    isLoggedIn: Boolean = true
) {
    NavHost(
        navController = navHostController,
        startDestination = if(isLoggedIn) HomeScreenNav else AuthenticationScreenNav

    ) {
        composable <AuthenticationScreenNav>{
            AuthenticationScreen(
                navigateToHome = {
                    navHostController.navigate(HomeScreenNav)
                }
            )
        }

        composable <HomeScreenNav>{
            HomeScreen(
                navigateToSettings = {
                    navHostController.navigate(SettingsScreenNav)

                }
            )
        }

        composable <SettingsScreenNav>{
            SettingsScreen(
                onPop = {
                    navHostController.popBackStack()
                },
                onAccountScreenOpen = {
                    navHostController.navigate(AccountScreenNav)
                }
            )
        }

        composable <AccountScreenNav>{
            AccountScreen(
                onPop = {
                    navHostController.popBackStack()
                }
            )
        }
    }

}