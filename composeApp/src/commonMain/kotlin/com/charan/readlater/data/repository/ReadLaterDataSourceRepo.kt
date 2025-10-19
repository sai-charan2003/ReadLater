package com.charan.readlater.data.repository

import com.charan.readlater.CategoryEntity
import com.charan.readlater.ReadLaterEntity
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.flow.Flow

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


}