package com.charan.readlater.presentation.models

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class BookmarkUiModel(
    val id : String = "",
    val url : String = "",
    val categoryName : String = "",
    val categoryId : String = "",
    val isDue : Boolean = false,
    val title : String = "",
    val description : String = "",
    val imageUrl : String = "",
    val createdAt : String = "",
    val hostUrl : String = ""
)
