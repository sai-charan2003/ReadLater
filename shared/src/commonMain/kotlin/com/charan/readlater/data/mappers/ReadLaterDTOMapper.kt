package com.charan.readlater.data.mappers

import com.charan.readlater.Bookmark
import com.charan.readlater.data.remote.model.BookmarkDTO

fun List<Bookmark>.toBookmarkDTO(emailId : String) : List<BookmarkDTO> {
    return this.map {
        BookmarkDTO(
            id = it.id,
            title = it.title ?: "",
            url = it.url,
            description = it.description ?: "",
            imageUrl = it.imageUrl ?: "",
            createdAt = it.createdAt,
            isDue = it.isDue,
            isDeleted = it.isDeleted,
            email = emailId,
            categoryUuid = it.categoryId,
            hostUrl = it.hostURL ?: "",
            isMetaDataFetched = it.isMetaDataFetched
        )
    }
}
fun List<BookmarkDTO>.toBookmarkList() : List<Bookmark> {
    return this.map {
        Bookmark(
            id = it.id,
            title = it.title,
            url = it.url,
            description = it.description,
            imageUrl = it.imageUrl,
            createdAt = it.createdAt,
            isDue = it.isDue,
            isSynced = true,
            isDeleted = it.isDeleted,
            categoryUUID = it.categoryUuid,
            hostURL = it.hostUrl
        )
    }
}
