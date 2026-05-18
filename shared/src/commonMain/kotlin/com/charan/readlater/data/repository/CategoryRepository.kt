package com.charan.readlater.data.repository

import com.charan.readlater.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    suspend fun addCategory(category : Category) : Category

    suspend fun deleteCategory(categoryId : String) : Boolean

    suspend fun getAllActiveCategories() : Flow<List<Category>>
}