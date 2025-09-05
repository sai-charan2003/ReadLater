package com.charan.readlater.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable

fun BookmarkItem(
    title : String,
    description : String,
    imageUrl : String,
    onClick : () -> Unit,
    isDue : Boolean = false
) {

    ListItem(
        leadingContent = {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.width(120.dp)
                    .height(71.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

        },
        headlineContent = {
            Column {
                Text(title)
            }
        },
        modifier = Modifier.clickable(){
            onClick()
        },
        supportingContent = {
            if(isDue){
                DueBadge()
            }
        },




    )

}