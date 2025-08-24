package com.charan.readlater.data.repository

import com.charan.readlater.data.local.model.WebMetaData

interface WebScrapperRepo {

    suspend fun getWebMetaData(url : String) : WebMetaData
}