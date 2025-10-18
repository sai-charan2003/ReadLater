package com.charan.readlater.presentation.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charan.readlater.presentation.home.DrawerItems

@Composable

fun DrawerContent(
    items : List<DrawerItems>,
    selectedIndex : Int,
    onItemClick : (DrawerItems, Int) ->Unit
) {
    LazyColumn(
        modifier = Modifier
    ) {
        items(items.size){
            val item = items[it]
            Box(modifier = Modifier.padding(horizontal = 10.dp)) {
                NavigationDrawerItem(
                    label = { Text(item.label) },
                    selected = it == selectedIndex,
                    onClick = {
                        onItemClick(item, it)
                    },
                    modifier = Modifier.padding(2.dp)
                )
            }
            if(item == DrawerItems.ReadLater){
                HorizontalDivider(modifier = Modifier.padding(top = 5.dp))
            }
        }
    }

}
