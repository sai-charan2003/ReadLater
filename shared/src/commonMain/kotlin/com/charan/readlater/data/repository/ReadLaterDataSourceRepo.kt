package com.charan.readlater.data.repository

import com.charan.readlater.CategoryEntity
import com.charan.readlater.ReadLaterEntity
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.flow.Flow


/**
 * This is the repository interface for managing Read Later database operations.
 * It defines various methods for CRUD operations on ReadLaterEntity and CategoryEntity.
 */
interface ReadLaterDataSourceRepo {

    fun getAllItems() : Flow<List<ReadLaterEntity>>

    suspend fun insertItem(item : ReadLaterEntity)

    suspend fun insertItems(items : List<ReadLaterEntity>)

    suspend fun updateItem(item : ReadLaterEntity)

    suspend fun getUnSyncedItems() : List<ReadLaterEntity>

    suspend fun clearAllData()

    suspend fun getAllActiveItems() : Flow<List<ReadLaterEntity>>

    suspend fun deleteItem(id: String)

    suspend fun updateDueStatus(id: Long, isDue: Boolean)

    suspend fun searchBookmarks(text : String) : Flow<List<ReadLaterEntity>>

    suspend fun getDueItems() : Flow<List<ReadLaterEntity>>

    suspend fun createCategory(categoryEntity: CategoryEntity) : Boolean

    suspend fun getAllCategories() : Flow<List<CategoryEntity>>

    suspend fun getBookmarkItemsByCategoryUUID(categoryUUID : String) : Flow<List<ReadLaterEntity>>

    suspend fun getUnSyncedCategories() : List<CategoryEntity>

    suspend fun updateSyncStatusForBookmark(id : Long, uuid : String) : Boolean

    suspend fun updateSyncStatusForCategory(id : Long, uuid : String) : Boolean

    suspend fun insertCategories(categories: List<CategoryEntity>) : Boolean

    suspend fun getBookmarkById(id : String) : ReadLaterEntity?

    suspend fun getCategoryByUUID(uuid : String) : CategoryEntity?

    suspend fun getBookmarkByUUID(uuid : String) : ReadLaterEntity?

    suspend fun deleteBookmarkByUUID(uuid : String) : Boolean

    suspend fun updateDueStatusByUUID(uuid: String, isDue: Boolean) : Boolean

    suspend fun getUnsyncedItemsCount() : Flow<Long>

    suspend fun deleteCategoryByUUID(uuid : String) : Boolean

    suspend fun updateCategory(categoryName: String, uuid : String) : Boolean




}