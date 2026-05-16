package com.charan.readlater.presentation.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.ImportExport
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.charan.readlater.presentation.settings.components.ProgressAlertDialog
import com.charan.readlater.presentation.settings.components.SettingsItem
import com.charan.readlater.presentation.settings.components.SettingsSubHeading
import com.charan.readlater.ui.theme.inversePrimaryDarkHighContrast
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    onPop : () -> Unit,
    onAccountScreenOpen : () -> Unit
) {
    val viewModel = koinViewModel<SettingsScreenViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val filePickerLauncher = rememberFilePickerLauncher(
        type = FileKitType.File(extension = "csv")
    ) { file->
        if(file != null){
            viewModel.onEvent(SettingsScreenEvents.OnFilePickerResult(file.toString()))
        }


    }
        if(state.showProgressDialog) {
            ProgressAlertDialog(
                title = "Importing Data",
                progress = state.importProgress.progress,
                total = state.importProgress.totalItems,
                current = state.importProgress.importedItems
            )
        }

    LaunchedEffect(Unit){
        viewModel.effect.collectLatest {
            when(it){
                SettingsScreenEffeect.NavigateBack -> {
                    onPop()
                }
                SettingsScreenEffeect.NavigateToAccountScreen -> {
                    onAccountScreenOpen()

                }
                is SettingsScreenEffeect.ShowError -> {

                }

                SettingsScreenEffeect.NavigateToLoginScreen -> {

                }

                SettingsScreenEffeect.OpenFilePicker -> {
                    filePickerLauncher.launch()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text("Settings")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.onEvent(SettingsScreenEvents.OnBackPressed)

                    }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack,null)

                    }


                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(it)
        ) {
            item {
                SettingsSubHeading(
                    title = "Account"
                )
                SettingsItem(
                    text = "Account Settings",
                    icon = Icons.Outlined.AccountCircle,
                    isClickable = true,
                    onClick = {
                        viewModel.onEvent(SettingsScreenEvents.OnAccountScreenClick)

                    }
                )


            }

            item {
                SettingsSubHeading(
                    title = "Data"
                )

                SettingsItem(
                    text = "Import Data",
                    icon = Icons.Rounded.ImportExport,
                    isClickable = true,
                    onClick = {

                        viewModel.onEvent(SettingsScreenEvents.OnImportClick)

                    }
                )
            }
        }
    }

}