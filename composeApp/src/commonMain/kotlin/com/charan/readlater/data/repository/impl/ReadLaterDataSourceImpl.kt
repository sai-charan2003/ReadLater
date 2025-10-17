package com.charan.readlater.data.repository.impl


import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.charan.readlater.ReadLaterDatabase
import com.charan.readlater.ReadLaterEntity
import com.charan.readlater.data.repository.ReadLaterDataSourceRepo
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ReadLaterDataSourceImpl(
    private val db : ReadLaterDatabase
) : ReadLaterDataSourceRepo {
    val queries = db.readLaterEntityQueries
    override fun getAllItems(): Flow<List<ReadLaterEntity>> {
        return queries.getAllReadLaterItems().asFlow().mapToList(Dispatchers.IO)

    }

    override suspend fun insertItem(item: ReadLaterEntity) {
        queries.insertReadLaterItem(
            title = item.title,
            url = item.url,
            description = item.description,
            created_at = item.created_at,
            is_due = item.is_due,
            image_url = item.image_url,
            isSynced = item.isSynced,
            uuid = item.uuid,
            isDeleted = item.isDeleted
        )

    }

    override suspend fun insertItems(items: List<ReadLaterEntity>) {
        db.transaction {
            items.forEach { item ->
                queries.insertReadLaterItem(
                    title = item.title,
                    url = item.url,
                    description = item.description,
                    created_at = item.created_at,
                    is_due = item.is_due,
                    image_url = item.image_url,
                    isSynced = item.isSynced,
                    uuid = item.uuid,
                    isDeleted = item.isDeleted
                )
            }
        }

    }

    override suspend fun updateItem(item: ReadLaterEntity) {

    }

    override suspend fun getUnSyncedItems(): List<ReadLaterEntity> {
        return queries.getAllPendingSyncItems().asFlow().mapToList(Dispatchers.IO).first()
    }

    override suspend fun clearAllData() {
        queries.delteAllReadLaterItems()
    }

    override suspend fun getAllActiveItems(): Flow<List<ReadLaterEntity> >{
        return queries.getActiveReadLaterItems().asFlow().mapToList(Dispatchers.IO)
    }

    override suspend fun deleteItem(id: String) {
        queries.deleteBookmarkById(id.toLong()  )

    }

    override suspend fun updateDueStatus(id: Long, isDue: Boolean) {
        queries.updateDueStatus(is_due = isDue, id = id)
    }

    override suspend fun searchBookmarks(text: String): Flow<List<ReadLaterEntity>> {
        return queries.searchFromReadLater(text).asFlow().mapToList(Dispatchers.IO)
    }
}