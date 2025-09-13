package com.charan.readlater.data.repository

import com.charan.readlater.ReadLaterEntity
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


}