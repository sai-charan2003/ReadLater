package com.charan.readlater.presentation.add_url

import com.charan.readlater.presentation.home.CategoryItem

data class AddURLState(
    val url : String = "",
    val isDue : Boolean = false,
    val isLoading : Boolean = false,
    val errorMessage : String = "",
    val selectedCategory : String = "",
    val selectedCategoryUUID : String = "",
    val categorySelectBottomSheet : Boolean = false,
    val categoryItems : List<CategoryItem> = emptyList(),
    val newCategoryName : String = ""
)