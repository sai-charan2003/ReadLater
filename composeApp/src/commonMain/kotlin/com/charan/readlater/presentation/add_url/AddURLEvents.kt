package com.charan.readlater.presentation.add_url

import com.charan.readlater.presentation.home.CategoryItem

sealed interface AddURLEvents {
    data class OnURLChange(val url : String) : AddURLEvents
    data class OnSaveURLClick(val isEdit : Boolean) : AddURLEvents
    data class OnDueButtonClick(val isDue : Boolean) : AddURLEvents

    data object OnCategorySheetOpen : AddURLEvents

    data object OnCategorySheetDismiss : AddURLEvents

    data class OnCategorySelect(val category: CategoryItem) : AddURLEvents

    data object OnCreateCategoryClick : AddURLEvents

    data class OnNewCategoryNameChange(val name : String) : AddURLEvents

    data class LoadDataForEdit(val uuid : String) : AddURLEvents
}