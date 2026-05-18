package com.charan.readlater.data.mappers

import com.charan.readlater.Bookmark
import com.charan.readlater.data.local.model.WebMetaData
import com.charan.readlater.presentation.home.ReadLaterUiItem
import com.charan.readlater.utils.DateUtils
import kotlinx.datetime.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun WebMetaData.toBookmark(url : String,isDue : Boolean,createdAt : String = "",categoryUUID : String = "") : Bookmark {
    val uuid = Uuid.random().toString()
    return Bookmark(
        id = uuid,
        url = url,
        title = this.title,
        description = this.description,
        createdAt = createdAt.ifEmpty { Clock.System.now().toString() },
        isDue =  isDue ,
        isSynced = false,
        imageUrl = this.imageUrl,
        isDeleted = false,
        categoryUUID = categoryUUID,
        hostURL = this.hostURL
    )
}

fun Bookmark.toReadLaterUiItem(categoryName : String) : ReadLaterUiItem {
    return ReadLaterUiItem(
        uuid = this.id,
        title = this.title ?: "",
        description = this.description ?: "",
        url = this.url,
        isDue = this.isDue,
        imageUrl = this.imageUrl ?: "",
        categoryUuid = this.categoryUUID ?: "",
        hostUrl = this.hostURL ?: "",
        formattedDate = DateUtils.formatReadableDateFromIso(this.createdAt),
        createdAt = this.createdAt,
        categoryName = categoryName
    )
}

fun List<Bookmark>.toReadLaterUiItem() : List<ReadLaterUiItem> {
    return this.map {
        ReadLaterUiItem(
            uuid = it.id,
            title = it.title ?: "",
            description = it.description ?: "",
            url = it.url,
            isDue = it.isDue,
            imageUrl = it.imageUrl ?: "",
            categoryUuid = it.categoryUUID ?: "",
            hostUrl = it.hostURL ?: "",
            createdAt = it.createdAt,
        )
    }

}
