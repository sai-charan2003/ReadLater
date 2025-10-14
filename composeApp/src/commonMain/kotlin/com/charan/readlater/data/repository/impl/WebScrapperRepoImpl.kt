package com.charan.readlater.data.repository.impl

import com.charan.readlater.data.local.model.WebMetaData
import com.charan.readlater.data.repository.WebScrapperRepo
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequest
import com.fleeksoft.ksoup.nodes.Document
import io.ktor.http.Url

class WebScrapperRepoImpl : WebScrapperRepo {
    override suspend fun getWebMetaData(url: String) : WebMetaData {
        try {
            val doc: Document = Ksoup.parseGetRequest(url = url)
            val title = doc.title()
            val imageUrl = doc.getMetaImage(url)
            val description = doc.select("meta[name=description]").attr("content")
            return WebMetaData(
                title = title,
                description = description,
                imageUrl = imageUrl
            )
        } catch (e: Exception){
            return WebMetaData(
                title = url
            )
        }

    }

    private fun Document.getMetaImage(url : String) : String{
        val selectors = listOf(
            "meta[property=og:image]",
            "meta[property=og:image:url]",
            "meta[property=og:image:secure_url]",
            "meta[name=twitter:image]",
            "meta[name=twitter:image:src]",
            "meta[name=image]",
            "meta[itemprop=image]",
            "meta[property=og:logo]",
            "link[rel=image_src]",
            "link[rel=apple-touch-icon]",
            "link[rel=icon]",
            "link[rel=shortcut icon]"
        )

        for (selector in selectors) {
            val element = this.selectFirst(selector)
            val content = element?.attr("content") ?: element?.attr("href")
            if (!content.isNullOrEmpty()) {
                return content
            }
        }
        val host = Url(url).host
        return "https://${host}/favicon.ico"
    }

}