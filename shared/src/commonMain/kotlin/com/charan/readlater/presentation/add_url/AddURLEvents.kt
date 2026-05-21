package com.charan.readlater.presentation.add_url

import com.charan.readlater.presentation.models.CategoryUiModel

sealed interface AddURLEvents {
    data class OnURLChange(val url : String) : AddURLEvents
    data class OnSaveURLClick(val isEdit : Boolean) : AddURLEvents
    data class OnDueButtonClick(val isDue : Boolean) : AddURLEvents

    data object OnCategorySheetToggle : AddURLEvents



    data class OnCategorySelect(val category: CategoryUiModel) : AddURLEvents

    data object OnCreateCategoryClick : AddURLEvents

    data class OnNewCategoryNameChange(val name : String) : AddURLEvents
}