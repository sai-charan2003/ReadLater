package com.charan.readlater.data.remote.model
import kotlinx.serialization.Serializable

@Serializable
data class ReadLaterDTO(
    val id : Long = 0,
    val title : String = "",
    val url : String = "",
    val image_url : String? = null,
    val is_due : Boolean = false,
    val is_deleted : Boolean = false,
    val created_at : String = "",
    val uuid : String = "",
    val description : String? = null,
    val email : String = "",
    val hostURL : String = "",
    val category_uuid : String? = null,

    )