package com.charan.readlater.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDTO(
    val id : Long = 0,
    val name : String = "",
    val created_at : String = "",
    val is_deleted : Boolean = false,
    val uuid : String = "",
    val email : String = ""
)
