package com.charan.readlater.data.mappers

import com.charan.readlater.ReadLaterEntity
import com.charan.readlater.data.remote.model.ReadLaterDTO

fun List<ReadLaterEntity>.toReadLaterDTO(emailId : String) : List<ReadLaterDTO> {
    return this.map {
        ReadLaterDTO(
            id = it.id,
            title = it.title ?: "",
            url = it.url,
            description = it.description ?: "",
            image_url = it.image_url?: "",
            created_at = it.created_at,
            is_due = it.is_due,
            is_deleted = it.isDeleted,
            email = emailId,
            uuid = it.uuid,
            category_uuid = it.category_uuid,
            hostURL = it.host_url ?: ""

        )
    }
}
fun List<ReadLaterDTO>.toReadLaterEntity() : List<ReadLaterEntity> {
    return this.map {
        ReadLaterEntity(
            id = it.id,
            title = it.title,
            url = it.url,
            description = it.description,
            image_url = it.image_url,
            created_at = it.created_at,
            is_due = it.is_due,
            isSynced = true,
            uuid = it.uuid,
            isDeleted = it.is_deleted,
            category_uuid = it.category_uuid,
            host_url = it.hostURL
        )
    }
}