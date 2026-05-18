package com.charan.readlater.data.mappers

import com.charan.readlater.Category
import com.charan.readlater.data.remote.model.CategoryDTO
import com.charan.readlater.presentation.home.CategoryItem
import com.charan.readlater.presentation.models.CategoryUiState


fun Category.toCategoryUI() : List<CategoryItem>{
    return listOf(
        CategoryItem(
            uuid = this.id,
            name = this.name,
        )
    )
}

fun List<Category>.toCategoryUIList() : List<CategoryUiState>{
    return this.map {
        CategoryUiState(
            id = it.id,
            name = it.name
        )
    }
}

fun List<Category>.toCategoryDTO(email : String) : List<CategoryDTO>{
    return this.map {
        CategoryDTO(
            id = it.id,
            name = it.name,
            createdAt = it.createdAt,
            isDeleted = it.isDeleted,
            email = email
        )
    }
}

fun List<CategoryDTO>.toCategoryList() : List<Category>{
    return this.map {
        Category(
            id = it.id,
            name = it.name,
            createdAt = it.createdAt,
            isDeleted = it.isDeleted,
            isSynced = true
        )
    }
}
