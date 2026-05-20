package com.charan.readlater.data.repository

import com.charan.readlater.Category
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    suspend fun fetchCategories() : ProcessState<Boolean>

    suspend fun addCategory(category : Category) : Category

    suspend fun deleteCategory(categoryId : String) : Boolean

    suspend fun getAllActiveCategories() : Flow<List<Category>>

    suspend fun getAllUnSyncedCategories() : List<Category>

     suspend fun updateCategorySyncStatus(categoryId : String, isSynced : Boolean)
}