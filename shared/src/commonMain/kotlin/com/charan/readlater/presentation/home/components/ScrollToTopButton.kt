package com.charan.readlater.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScrollToTop(
    showScrollToTop:Boolean,
    onClick : () -> Unit
) {
    Box(modifier= Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = showScrollToTop,
            enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically),
            exit = scaleOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically),
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            FilledTonalIconButton(
                onClick = {
                },
                modifier = Modifier
                    .padding(bottom = 10.dp, end = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowUpward,
                    contentDescription = null
                )

            }
        }
    }
}