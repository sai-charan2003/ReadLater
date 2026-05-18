package com.charan.readlater.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class MetaDataDTO(
    val url : String? = null,
    val title : String? = null,
    val description : String? = null,
    val imageUrl : String? = null,
    val hostUrl : String? = null
)
