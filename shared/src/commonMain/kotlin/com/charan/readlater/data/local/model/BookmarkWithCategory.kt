package com.charan.readlater.data.local.model

import com.charan.readlater.Bookmark
import com.charan.readlater.Category

data class BookmarkWithCategory(
    val bookmark : Bookmark? = null,
    val category : Category? = null
)
