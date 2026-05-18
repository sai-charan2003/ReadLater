package com.charan.readlater.presentation.models

data class CategoryUiModel(
    val id : String = "",
    val name : String = "",
    val itemCount : Int = 0,
    val isSelected : Boolean = false
)