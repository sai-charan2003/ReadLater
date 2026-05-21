package com.charan.readlater.presentation.mapper

import com.charan.readlater.Bookmark
import com.charan.readlater.Category
import com.charan.readlater.data.local.model.BookmarkWithCategory
import com.charan.readlater.presentation.home.CategoryItem
import com.charan.readlater.presentation.models.BookmarkUiModel
import com.charan.readlater.presentation.models.CategoryUiModel
import com.charan.readlater.utils.generateUuid
import com.charan.readlater.utils.getCurrentIsoDate

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

fun List<BookmarkWithCategory>.toBookmarkUiModelList() : List<BookmarkUiModel> {
    return this.map { it.toBookmarkUiModel() }
}

fun BookmarkUiModel.toBookmark() : Bookmark {
    return Bookmark(
        id = if (this.id.isBlank()) generateUuid() else this.id,
        url = this.url,
        title = this.title.ifBlank { null },
        description = this.description.ifBlank { null },
        createdAt = this.createdAt.ifBlank { getCurrentIsoDate() },
        isDue = this.isDue,
        isSynced = false,
        imageUrl = this.imageUrl.ifBlank { null },
        isDeleted = false,
        categoryId = this.categoryId.ifBlank { null },
        hostURL = this.hostUrl.ifBlank { null },
        isMetaDataFetched = false
    )
}

fun Category.toCategoryUiModel(isSelected : Boolean = false) : CategoryUiModel {
    return CategoryUiModel(
        id = this.id,
        name = this.name,
        isSelected = isSelected
    )
}

fun List<Category>.toCategoryUiModelList(): List<CategoryUiModel> {
    return this.map { it.toCategoryUiModel() }
}

fun List<Category>.toCategoryItemList(bookmarks: List<BookmarkUiModel>): List<CategoryItem> {
    return this.map { category ->
        CategoryItem(
            uuid = category.id,
            name = category.name,
            itemCount = bookmarks.count { it.categoryId == category.id },
            isSelected = false
        )
    }
}


fun Bookmark.toBookmarkUiModel(categoryName : String) : BookmarkUiModel {
    return BookmarkUiModel(
        id = this.id,
        url = this.url,
        title = this.title ?: "",
        description = this.description ?: "",
        createdAt = this.createdAt,
        imageUrl = this.imageUrl ?: "",
        isDue = this.isDue,
        categoryId = this.categoryId ?: "",
        categoryName = categoryName,
        hostUrl = this.hostURL ?: ""
    )
}
