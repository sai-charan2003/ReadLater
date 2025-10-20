package com.charan.readlater.data.mappers

import com.charan.readlater.ReadLaterEntity
import com.charan.readlater.data.local.model.WebMetaData
import com.charan.readlater.presentation.home.ReadLaterUiItem
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
fun WebMetaData.toReadLaterItem(url : String,isDue : Boolean,createdAt : String = "",categoryUUID : String = "") : ReadLaterEntity {
    return ReadLaterEntity(
        id = 0,
        url = url,
        title = this.title,
        description = this.description,
        created_at = createdAt.ifEmpty { Clock.System.now().toString() },
        is_due =  isDue ,
        isSynced = false,
        image_url = this.imageUrl,
        uuid = Uuid.random().toString(),
        isDeleted = false,
        category_uuid = categoryUUID
    )
}

fun List<ReadLaterEntity>.toReadLaterUiItem() : List<ReadLaterUiItem> {
    return this.map {
        ReadLaterUiItem(
            id = it.id.toString(),
            title = it.title ?: "",
            description = it.description ?: "",
            url = it.url,
            isDue = it.is_due as Boolean,
            imageUrl = it.image_url ?: "",
            categoryUUID = it.category_uuid ?: ""


        )
    }

}