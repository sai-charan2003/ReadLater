package com.charan.readlater.presentation.add_url

import com.charan.readlater.presentation.home.CategoryItem

data class AddURLState(
    val bookmarkData: BookmarkDataUIState = BookmarkDataUIState(),
    val isLoading : Boolean = false,
    val errorMessage : String = "",
    val selectedCategory : String = "",
    val categorySelectBottomSheet : Boolean = false,
    val categoryItems : List<CategoryItem> = emptyList(),
    val newCategoryName : String = "",
    val editUUID : String = ""
)

data class BookmarkDataUIState(
    val url : String = "",
    val isDue : Boolean = false,
    val categoryUUID : String = "",
)