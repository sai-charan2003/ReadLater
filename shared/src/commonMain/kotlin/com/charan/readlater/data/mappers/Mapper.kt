package com.charan.readlater.data.mappers

import com.charan.readlater.Bookmark
import com.charan.readlater.Category
import com.charan.readlater.data.local.model.ImportData
import com.charan.readlater.data.remote.model.BookmarkDTO
import com.charan.readlater.data.remote.model.CategoryDTO
import com.charan.readlater.utils.generateUuid

fun ImportData.toBookmark() : Bookmark {
    return Bookmark(
        id = generateUuid(),
        url = this.url ?: "",
        title = this.title ?: "",
        createdAt = this.created ?: "",
        isDue = false,
        isSynced = false,
        isDeleted = false,
        categoryId = null,
        hostURL = null,
        description = null,
        imageUrl = null,
        isMetaDataFetched = false,

        )
}

fun List<ImportData>.toBookmarkList() : List<Bookmark> {
    return this.map {
        it.toBookmark()
    }
}

fun Bookmark.toBookmarkDTO(emailId : String) : BookmarkDTO {
    return BookmarkDTO(
        id = this.id,
        title = this.title ?: "",
        url = this.url,
        description = this.description ?: "",
        imageUrl = this.imageUrl ?: "",
        createdAt = this.createdAt,
        isDue = this.isDue,
        isDeleted = this.isDeleted,
        email = emailId,
        categoryUuid = this.categoryId,
        hostUrl = this.hostURL ?: "",
        isMetaDataFetched = this.isMetaDataFetched
    )
}


fun List<Bookmark>.toBookmarkDTO(emailId : String) : List<BookmarkDTO> {
    return this.map {
        it.toBookmarkDTO(emailId)
    }
}

fun BookmarkDTO.toBookmark(): Bookmark {
    return Bookmark(
        id = this.id,
        title = this.title,
        url = this.url,
        description = this.description,
        imageUrl = this.imageUrl,
        createdAt = this.createdAt,
        isDue = this.isDue,
        isSynced = true,
        isDeleted = this.isDeleted,
        categoryId = this.categoryUuid,
        hostURL = this.hostUrl,
        isMetaDataFetched = this.isMetaDataFetched
    )
}

fun List<BookmarkDTO>.toBookmark() : List<Bookmark> {
    return this.map {
        it.toBookmark()
    }
}

fun CategoryDTO.toCategory() : Category {
    return Category(
        id = this.id,
        name = this.name,
        createdAt = this.createdAt,
        isDeleted = this.isDeleted,
        isSynced = true
    )
}

fun List<CategoryDTO>.toCategory() : List<Category> {
    return this.map {
        it.toCategory()
    }
}

fun Category.toCategoryDTO(emailId: String) : CategoryDTO {
    return CategoryDTO(
        id = this.id,
        name = this.name,
        createdAt = this.createdAt,
        isDeleted = this.isDeleted,
        email = emailId
    )
}

fun List<Category>.toCategoryDTO(emailId: String) : List<CategoryDTO> {
    return this.map {
        it.toCategoryDTO(emailId)
    }
}