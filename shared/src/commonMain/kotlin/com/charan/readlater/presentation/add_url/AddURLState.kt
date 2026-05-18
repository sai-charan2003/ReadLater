package com.charan.readlater.presentation.add_url

import com.charan.readlater.presentation.models.BookmarkUiModel
import com.charan.readlater.presentation.models.CategoryUiState
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class AddURLState(
    val bookmarkData: BookmarkUiModel = BookmarkUiModel(),
    val isLoading : Boolean = false,
    val errorMessage : String = "",
    val categorySelectBottomSheet : Boolean = false,
    val categoryItems : List<CategoryUiState> = emptyList(),
    val newCategoryName : String = "",
)
@OptIn(ExperimentalUuidApi::class)
data class BookmarkDataUIState (
    val url : String = "",
    val isDue : Boolean = false,
    val categoryUuid : String = "",
    val id : String = Uuid.generateV4().toString()
)