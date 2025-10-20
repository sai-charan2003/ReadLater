package com.charan.readlater.data.mappers

import com.charan.readlater.CategoryEntity
import com.charan.readlater.data.remote.model.CategoryDTO
import com.charan.readlater.presentation.home.CategoryItem

fun CategoryEntity.toCategoryUI() : List<CategoryItem>{
    return listOf(
        CategoryItem(
            uuid = this.uuid,
            name = this.name,
        )
    )
}

fun List<CategoryEntity>.toCategoryUIList() : List<CategoryItem>{
    return this.map {
        CategoryItem(
            uuid = it.uuid,
            name = it.name,
        )
    }
}

fun List<CategoryEntity>.toCategoryDTO(email : String) : List<CategoryDTO>{
    return this.map {
        CategoryDTO(
            id = 0,
            name = it.name,
            created_at = it.createdAt,
            isDeleted = it.isDeleted,
            uuid = it.uuid,
            email = email
        )
    }
}