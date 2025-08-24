package com.charan.readlater.data.repository.impl

import com.charan.readlater.data.local.model.WebMetaData
import com.charan.readlater.data.repository.WebScrapperRepo
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequest
import com.fleeksoft.ksoup.nodes.Document

class WebScrapperRepoImpl : WebScrapperRepo {
    override suspend fun getWebMetaData(url: String) : WebMetaData {
        val doc: Document = Ksoup.parseGetRequest(url = url)
        val title = doc.title()
        val imageUrl = doc.select("meta[property=og:image]").attr("content")
        val description = doc.select("meta[name=description]").attr("content")
        return WebMetaData(
            title = title,
            description = description,
            imageUrl = imageUrl
        )

    }

}