package com.charan.readlater.presentation.authentication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.charan.readlater.data.remote.ReadLaterSupabaseClient
import com.mmk.kmpauth.google.GoogleButtonUiContainer
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import readlater.composeapp.generated.resources.Res
import readlater.composeapp.generated.resources.google_signin

@Composable
fun AuthenticationScreen(
    navigateToHome: () -> Unit,

) {
    val viewModel = koinViewModel<AuthenticationViewModel>()
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.authenticationScreenEffect.collectLatest { effect ->
            when (effect) {
                AuthenticationScreenEffect.NavigateToHome -> {
                    navigateToHome()
                }

                is AuthenticationScreenEffect.ShowError -> {

                }
            }
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome to ReadLater",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(40.dp))
            GoogleButtonUiContainer(onGoogleSignInResult = { googleUser ->
                val idToken = googleUser?.idToken
                viewModel.onEvent(AuthenticationEvent.OnGoogleSignInKey(idToken ?: ""))

            }) {
                Button(
                    onClick = {
                        println("Button clicked")
                        this.onClick()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isAuthenticating
                ) {
                    Image(
                        painter = painterResource(Res.drawable.google_signin),
                        contentDescription = "Google Sign-In",
                    )
                    Spacer(Modifier.padding(end = 10.dp))
                    Text("Sign in with Google")
                    AnimatedVisibility(visible = state.isAuthenticating) {
                        Spacer(Modifier.weight(1f))
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.onPrimary
                        )

                    }

                }

            }


            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text("  OR  ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))


            FilledTonalButton(
                onClick = {
                    viewModel.onEvent(AuthenticationEvent.OnNoAccountLogin)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue without account")
            }
        }
    }
}
