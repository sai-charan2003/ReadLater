package com.charan.readlater.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDTO(
    val id : String = "",
    val name : String = "",
    @SerialName("createdAt")
    val createdAt : String = "",
    @SerialName("isDeleted")
    val isDeleted : Boolean = false,
    val email : String = ""
)
