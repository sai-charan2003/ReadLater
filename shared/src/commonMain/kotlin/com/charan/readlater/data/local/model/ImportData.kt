package com.charan.readlater.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class ImportData(
    val url : String? = null,
    val title : String? = null,
    val created : String? = null,
    @SerialName("time_added")
    val timeAdded : String? = null
)
