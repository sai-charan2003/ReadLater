package com.charan.readlater.presentation.home

sealed class DrawerItems(val id : String, val label : String) {
    object AllBookMarks : DrawerItems(TopDrawerItems.ALL.name, TopDrawerItems.ALL.toString())
    object ReadLater : DrawerItems(TopDrawerItems.READ_LATER.name, TopDrawerItems.READ_LATER.toString())
    data class Category(val categoryId : String, val categoryName : String) : DrawerItems(categoryId, categoryName)
}

enum class TopDrawerItems {
    ALL,
    READ_LATER;

    override fun toString() = when(this) {
        ALL -> "All Bookmarks"
        READ_LATER -> "Read Later"
    }
}

