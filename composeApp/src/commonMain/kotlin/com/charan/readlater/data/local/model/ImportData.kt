package com.charan.readlater.data.local.model

import kotlinx.serialization.Serializable
@Serializable
data class ImportData(
    val url : String? = null,
    val title : String? = null,
    val created : String? = null,
    val time_added : String? = null
)
