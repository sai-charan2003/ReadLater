package com.charan.readlater.data.repository.impl


import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.charan.readlater.ReadLaterDatabase
import com.charan.readlater.ReadLaterEntity
import com.charan.readlater.data.repository.ReadLaterDataSourceRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

class ReadLaterDataSourceImpl(
    private val db : ReadLaterDatabase
) : ReadLaterDataSourceRepo {
    val queries = db.readLaterEntityQueries
    override fun getAllItems(): Flow<List<ReadLaterEntity>> {
        return queries.getAllReadLaterItems().asFlow().mapToList(Dispatchers.IO)

    }

    override suspend fun insertItem(item: ReadLaterEntity) {
        queries.insertReadLaterItem(
            id = item.id,
            title = item.title,
            url = item.url,
            description = item.description,
            created_at = item.created_at,
            is_due = item.is_due,
        )

    }

    override suspend fun updateItem(item: ReadLaterEntity) {

    }

}