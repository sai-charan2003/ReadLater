package com.charan.readlater.presentation.settings.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.charan.readlater.presentation.settings.SettingsScreenEffeect
import com.charan.readlater.presentation.settings.SettingsScreenEvents
import com.charan.readlater.presentation.settings.SettingsScreenViewModel
import com.charan.readlater.presentation.settings.components.LogoutAlertDialog
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AccountScreen(
    onPop: () -> Unit,
    navigateToSignIn : () -> Unit
) {
    val viewModel = koinViewModel<SettingsScreenViewModel>()
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit){
        viewModel.effect.collect { effect->
            when(effect){
                SettingsScreenEffeect.NavigateToLoginScreen -> {
                    navigateToSignIn()
                }
                else -> Unit

            }
        }
    }
    if(state.showLogoutDialog){
        LogoutAlertDialog(
            onConfirmClick = {
                viewModel.onEvent(SettingsScreenEvents.OnConfirmSignOutClick)
            },
            onDismiss = {
                viewModel.onEvent(SettingsScreenEvents.OnSignOutClick)
            }
        )
    }

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text("Account") },
                navigationIcon = {
                    IconButton(onClick = { onPop() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        if (state.isLoggedIn) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = state.userDetails.imageURL,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = state.userDetails.userName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )

                Text(
                    text = state.userDetails.userEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )


                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.onEvent(SettingsScreenEvents.OnSignOutClick) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text("Sign Out")
                }
            }

        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Sign in to sync your data across devices",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.onEvent(SettingsScreenEvents.OnSignInClick) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Sign In")
                }
            }
        }
    }
}
