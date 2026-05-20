package com.charan.readlater.data.sync

import com.charan.readlater.data.remote.model.MetaDataDTO
import com.charan.readlater.data.repository.BookmarkRepository
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MetadataFetcher(
    private val bookmarkRepository: BookmarkRepository,
    private val supabaseRepo: SupabaseRepo
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    fun triggerMetaDataFetch() = scope.launch{
        bookmarkRepository.getPendingMetaDataFetchBookmarks().collectLatest { bookmarks ->
            bookmarks.forEach { bookmark->
                supabaseRepo.fetchMetaDataForUrl(bookmark.url).apply {
                    when(this){
                        is ProcessState.Error -> {

                        }
                        is ProcessState.Loading -> TODO()
                        ProcessState.NotDetermined -> {

                        }
                        is ProcessState.Success<*> -> {
                            val bookmarkMetaDataDTO = this.data as? MetaDataDTO
                            val updatedBookmarkData = bookmark.copy(
                                title =  bookmarkMetaDataDTO?.title ?: "",
                                description = bookmarkMetaDataDTO?.description ?: "",
                                imageUrl = bookmarkMetaDataDTO?.imageUrl ?: "",
                                hostURL = bookmarkMetaDataDTO?.hostUrl ?: "",
                                isMetaDataFetched = true
                            )

                            bookmarkRepository.addBookmark(updatedBookmarkData)
                        }
                    }
                }
            }

        }

    }


}