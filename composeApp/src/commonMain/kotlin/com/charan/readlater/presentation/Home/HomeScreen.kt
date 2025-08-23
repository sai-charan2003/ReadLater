package com.charan.readlater.presentation.Home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(

) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Read Later") }
            )
        }

    ) {

        LazyColumn {

        }
    }

}