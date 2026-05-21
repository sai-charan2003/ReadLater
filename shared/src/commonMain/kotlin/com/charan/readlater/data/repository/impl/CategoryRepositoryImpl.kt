package com.charan.readlater.data.repository.impl

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.charan.readlater.Category
import com.charan.readlater.CategoryQueries
import com.charan.readlater.data.mappers.toCategory
import com.charan.readlater.data.remote.SupabaseRemoteDataSource
import com.charan.readlater.data.remote.model.CategoryDTO
import com.charan.readlater.data.repository.CategoryRepository
import com.charan.readlater.data.sync.SyncManager
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val categoryQueries: CategoryQueries,
    private val supabaseRemoteDataSource: SupabaseRemoteDataSource,
    private val syncManager: SyncManager
) : CategoryRepository {

    override suspend fun fetchCategories(): ProcessState<Boolean> {
        return when(val result = supabaseRemoteDataSource.getAllCategories()){
            is ProcessState.Error -> {
                ProcessState.Error(result.exception)
            }
            is ProcessState.Loading -> {
                ProcessState.Loading()
            }

            ProcessState.NotDetermined -> {
                ProcessState.NotDetermined
            }
            is ProcessState.Success<List<CategoryDTO>> -> {
                val categories = result.data.toCategory()
                categories.forEach { category ->
                    insertCategory(category, triggerSync = false)
                }
                ProcessState.Success(true)
            }

        }
    }

    override suspend fun addCategory(category: Category): Category {
        insertCategory(category, triggerSync = true)
        return category
    }

    private suspend fun insertCategory(category: Category, triggerSync: Boolean) {
        categoryQueries.insertCategory(
            id = category.id,
            name = category.name,
            createdAt = category.createdAt,
            isSynced = category.isSynced,
            isDeleted = category.isDeleted
        )
        if (triggerSync) {
            syncManager.syncNow()
        }
    }

    override suspend fun deleteCategory(categoryId: String): Boolean {
        categoryQueries.deleteCategoryById(categoryId)
        syncManager.syncNow()
        return true
    }

    override suspend fun getCategoryById(categoryId: String): Category? {
        return categoryQueries.getCategoryById(categoryId).executeAsOneOrNull()
    }

    override suspend fun updateCategory(categoryId: String, categoryName: String): Boolean {
        categoryQueries.updateCategoryById(
            name = categoryName,
            id = categoryId
        )
        syncManager.syncNow()
        return true
    }

    override suspend fun getAllActiveCategories(): Flow<List<Category>> {
        return categoryQueries.getAllActiveCategories().asFlow().mapToList(Dispatchers.IO)
    }

    override suspend fun getAllUnSyncedCategories(): List<Category>{
        return categoryQueries.getUnSyncedCategories().executeAsList()
    }

    override suspend fun updateCategorySyncStatus(
        categoryId: String,
        isSynced: Boolean
    ) {
        categoryQueries.updateSyncStatusCategory(isSynced, categoryId)
    }

    override suspend fun clearAllCategories() {
        categoryQueries.deleteAllCategories()
    }
}
