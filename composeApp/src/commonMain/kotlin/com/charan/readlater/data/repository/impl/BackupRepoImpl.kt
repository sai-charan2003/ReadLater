package com.charan.readlater.data.repository.impl


import com.charan.readlater.data.local.model.ImportData
import com.charan.readlater.data.repository.BackupRepo
import com.charan.readlater.data.repository.BookmarkManagerRepo
import com.charan.readlater.utils.ProcessState
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.fromBookmarkData
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.readString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.csv.Csv


class BackupRepoImpl(
    private val bookmarkManagerRepo: BookmarkManagerRepo
) : BackupRepo {


    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun importFromFile(uri: String): Flow<ProcessState<Boolean>> = flow{
        emit(ProcessState.Loading(progress = 0f))
        try {
            val file = PlatformFile(uri)
            val content = file.readString()
            val csv = Csv {
                hasHeaderRecord = true
                ignoreEmptyLines = true
                ignoreUnknownColumns = true
            }
            val importData = csv.decodeFromString(ListSerializer(ImportData.serializer()),content)
            bookmarkManagerRepo.addImportBookmark(importData).collect {
                emit(it)
            }
        } catch (e: Exception){
            emit(ProcessState.Error(e.message.toString()))
        }

    }
}