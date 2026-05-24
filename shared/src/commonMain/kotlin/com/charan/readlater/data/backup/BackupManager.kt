package com.charan.readlater.data.backup

import com.charan.readlater.data.local.model.ImportData
import com.charan.readlater.data.mappers.toBookmark
import com.charan.readlater.data.repository.BookmarkRepository
import com.charan.readlater.utils.ProcessState
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.csv.Csv
import org.koin.core.annotation.Singleton

@Singleton
class BackupManager(
    private val bookmarkRepository: BookmarkRepository
) {

        @OptIn(ExperimentalSerializationApi::class)
        suspend fun importFromFile(uri: String): Flow<ProcessState<Boolean>> = flow {
            emit(ProcessState.Loading(progress = 0f))

            try {
                val file = PlatformFile(uri)
                val content = file.readString()

                val csv = Csv {
                    hasHeaderRecord = true
                    ignoreEmptyLines = true
                    ignoreUnknownColumns = true
                }

                val importData = csv.decodeFromString(
                    ListSerializer(ImportData.serializer()),
                    content
                )

                if (importData.isEmpty()) {
                    emit(ProcessState.Success(true))
                    return@flow
                }

                val total = importData.size
                var completed = 0

                importData.forEach { data ->
                    try {
                        val bookmark = data.toBookmark()
                        bookmarkRepository.addBookmark(bookmark)
                    } catch (e: Exception) {
                        println("Import error: ${e.message}")
                    }

                    completed++

                    emit(
                        ProcessState.Loading(
                            progress = completed / total.toFloat(),
                            total = total.toLong(),
                            current = completed.toLong()
                        )
                    )
                }

                emit(ProcessState.Success(true))

            } catch (e: Exception) {
                emit(ProcessState.Error(e.message ?: "Import failed"))
            }
        }
}
