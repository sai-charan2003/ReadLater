package com.charan.readlater.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.charan.readlater.presentation.add_url.AddURLScreen
import com.charan.readlater.presentation.home.HomeScreen
import com.charan.readlater.presentation.authentication.AuthenticationScreen
import com.charan.readlater.presentation.settings.SettingsScreen
import com.charan.readlater.presentation.settings.account.AccountScreen

@Composable
fun NavAppHost(
    navHostController: NavHostController,
    isLoggedIn: Boolean = true,
    sharedURL : String = "",
) {
    LaunchedEffect(sharedURL){
        if(sharedURL.isNotEmpty()) {
            navHostController.navigate(AddURLScreenNav(sharedURL)) {
                popUpTo(navHostController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
    NavHost(
        navController = navHostController,
        startDestination = if(isLoggedIn) HomeScreenNav else AuthenticationScreenNav,
        enterTransition = {
            fadeIn() + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                initialOffset = { 100 },
                animationSpec = (tween(easing = LinearEasing, durationMillis = 200))
            )
        },
        exitTransition = {
            fadeOut() + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                targetOffset = { -100 },
                animationSpec = (tween(easing = LinearEasing, durationMillis = 200))
            )
        },
        popEnterTransition = {
            fadeIn() + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                initialOffset = { -100 },
                animationSpec = (tween(easing = LinearEasing, durationMillis = 200))
            )
        },
        popExitTransition = {
            fadeOut() + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                targetOffset = { 100 },
                animationSpec = (tween(easing = LinearEasing, durationMillis = 200))
            )
        },

    ) {
        composable <AuthenticationScreenNav>{
            AuthenticationScreen(
                navigateToHome = {
                    navHostController.navigate(HomeScreenNav){
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToBack = {
                    navHostController.popBackStack()
                },
                hasBackStack = navHostController.previousBackStackEntry != null
            )
        }

        composable <HomeScreenNav>{
            HomeScreen(
                navigateToSettings = {
                    navHostController.navigate(SettingsScreenNav)

                },
                navigateToLoginScreen = {
                    navHostController.navigate(AuthenticationScreenNav)
                },
                navigateToAddURLScreen = { isEdit, id ->
                    navHostController.navigate(AddURLScreenNav(
                        url = "",
                        isEdit = isEdit,
                        uuid = id
                    ))
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
                },
                navigateToSignIn = {
                    navHostController.navigate(AuthenticationScreenNav)
                }
            )
        }

        composable <AddURLScreenNav>{
            val arguments = it.toRoute<AddURLScreenNav>()
            AddURLScreen(
                onBackClick = {
                    println("On Back press")
                    navHostController.popBackStack()
                },
                sharedURL = arguments.url,
                isEdit = arguments.isEdit,
                uuid = arguments.uuid
            )
        }
    }

}