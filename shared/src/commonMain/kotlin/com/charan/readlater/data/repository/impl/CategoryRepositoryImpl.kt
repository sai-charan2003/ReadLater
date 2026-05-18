package com.charan.readlater.data.repository.impl

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.charan.readlater.Category
import com.charan.readlater.CategoryQueries
import com.charan.readlater.data.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

class CategoryRepositoryImpl(
    private val categoryQueries: CategoryQueries
) : CategoryRepository {
    override suspend fun addCategory(category: Category): Category {
        categoryQueries.insertCategory(
            id = category.id,
            name = category.name,
            createdAt = category.createdAt,
            isSynced = category.isSynced,
            isDeleted = category.isDeleted
        )
        return category

    }

    override suspend fun deleteCategory(categoryId: String): Boolean {
        categoryQueries.deleteCategoryById(categoryId)
        return true
    }

    override suspend fun getAllActiveCategories(): Flow<List<Category>> {
        return categoryQueries.getAllActiveCategories().asFlow().mapToList(Dispatchers.IO)
    }
}