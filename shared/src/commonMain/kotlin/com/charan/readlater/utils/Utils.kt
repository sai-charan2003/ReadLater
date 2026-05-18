package com.charan.readlater.utils

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun generateUuid() : String {
    return Uuid.generateV4().toString()
}