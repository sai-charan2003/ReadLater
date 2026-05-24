package com.charan.readlater.data.remote.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookmarkDTO(
    val id : String = "",
    val title : String = "",
    val url : String = "",
    @SerialName("image_url")
    val imageUrl : String? = null,
    @SerialName("is_due")
    val isDue : Boolean = false,
    @SerialName("is_delete")
    val isDeleted : Boolean = false,
    @SerialName("created_at")
    val createdAt : String = "",
    val description : String? = null,
    val email : String = "",
    @SerialName("host_url")
    val hostUrl : String? = null,
    @SerialName("category_uuid")
    val categoryUuid : String? = null,
    @SerialName("is_metadata_fetched")
    val isMetaDataFetched : Boolean = false
)