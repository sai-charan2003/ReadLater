package com.charan.readlater.presentation.mapper

import com.charan.readlater.Bookmark
import com.charan.readlater.Category
import com.charan.readlater.data.local.model.BookmarkWithCategory
import com.charan.readlater.presentation.models.BookmarkUiModel
import com.charan.readlater.presentation.models.CategoryUiModel

fun BookmarkWithCategory.toBookmarkUiModel() : BookmarkUiModel {
    return BookmarkUiModel(
        url = this.bookmark?.url ?: "",
        isDue = this.bookmark?.isDue ?: false,
        categoryId = this.category?.id ?: "",
        categoryName = this.category?.name ?: "",
        id = this.bookmark?.id ?: "",
        hostUrl = this.bookmark?.hostURL ?: "",
        description = this.bookmark?.description ?: "",
        title = this.bookmark?.title ?: "",
        imageUrl = this.bookmark?.imageUrl ?: "",
        createdAt = this.bookmark?.createdAt ?: "",

    )
}

fun BookmarkUiModel.toBookmark() : Bookmark {
    return Bookmark(
        id = this.id,
        url = this.url,
        title = this.title,
        description = null,
        createdAt = this.createdAt,
        isDue = this.isDue,
        isSynced = false,
        imageUrl = this.imageUrl,
        isDeleted = false,
        categoryId = this.categoryId,
        hostURL = this.hostUrl
    )
}

fun Category.toCategoryUiModel(isSelected : Boolean = false) : CategoryUiModel {
    return CategoryUiModel(
        id = this.id,
        name = this.name,
        isSelected = isSelected
    )
}
