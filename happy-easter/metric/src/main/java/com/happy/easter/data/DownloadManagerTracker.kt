package com.happy.easter.data

import android.app.DownloadManager
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import androidx.room.util.useCursor
import com.happy.easter.HappyEasterMetricQueue
import java.util.concurrent.Executors

private val URI = Uri.parse("content://downloads/my_downloads")
private val projection = arrayOf(
    DownloadManager.COLUMN_ID,
    DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR,
    DownloadManager.COLUMN_STATUS,
    DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP,
    DownloadManager.COLUMN_REASON
)

internal class DownloadManagerTracker(
    private val context: Context,
    private val queue: HappyEasterMetricQueue
) {
    private val executorService = Executors.newSingleThreadExecutor()
    private val entities = mutableMapOf<Long, HappyEasterDownloadEntity>()
    private val dao = HappyEasterDatabaseProvider.getDatabase(context)
        .downloadDao()

    init {
        executorService.submit(::loadEntities)
        context.contentResolver.registerContentObserver(
            URI,
            true,
            DownloadChangeObserver()
        )
    }

    fun onDownloadEnqueued(
        downloadId: Long,
        url: String,
        feature: String
    ) {
        executorService.submit {
            val startTime = System.currentTimeMillis()
            val entity = HappyEasterDownloadEntity(
                id = downloadId,
                startTime = startTime,
                url = url,
                feature = feature
            )
            dao.insert(entity)
            entities[downloadId] = entity
        }
    }

    private fun loadEntities() {
        entities.putAll(dao.getAll().associateBy { it.id })
        val downloads = mutableMapOf<Long, Download>()
        if (entities.isNotEmpty()) {
            context.contentResolver
                .query(
                    URI,
                    projection,
                    "${DownloadManager.COLUMN_ID} IN (${entities.keys.joinToString(",")})",
                    null,
                    null
                )
                ?.useCursor { parseDownloads(it, downloads) }
            updateDownloads(downloads)
        }
    }

    private fun parseDownloads(
        cursor: Cursor,
        map: MutableMap<Long, Download>
    ) = with(cursor) {
        if (moveToFirst()) {
            do {
                val id = getLong(getColumnIndexOrThrow(DownloadManager.COLUMN_ID))
                map[id] = parseDownload()
            } while (moveToNext())
        }
    }

    private fun Cursor.parseDownload(): Download {
        val bytes = getLong(getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
        val status = getInt(getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
        val lastModified =
            getLong(getColumnIndexOrThrow(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP))
        val reason = getInt(getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))
        return Download(bytes, status, reason, lastModified)
    }

    private fun updateDownloads(downloads: Map<Long, Download>) {
        val removedEntities = mutableListOf<HappyEasterDownloadEntity>()
        val updatedEntities = mutableListOf<HappyEasterDownloadEntity>()
        for (entity in entities.values) {
            val download = downloads[entity.id]
            val updatedEntity = entity.copy(
                responseBytes = download?.bytes ?: entity.responseBytes,
                endTime = download?.lastTimeModified ?: entity.endTime,
                code = download?.code() ?: -1
            )
            if (
                download == null ||
                download.isSuccessful() ||
                download.isFailed()
            ) {
                removedEntities.add(updatedEntity)
            } else {
                updatedEntities.add(updatedEntity)
                entities[entity.id] = updatedEntity
            }
        }
        if (updatedEntities.isNotEmpty()) {
            dao.update(updatedEntities)
        }
        if (removedEntities.isNotEmpty()) {
            removeEntities(removedEntities)
        }
    }

    private fun removeEntity(entityId: Long) {
        entities[entityId]?.let(::listOf)
            ?.let(::removeEntities)
    }

    private fun removeEntities(
        removedEntities: List<HappyEasterDownloadEntity>
    ) {
        for (entity in removedEntities) {
            entities.remove(entity.id)
        }
        dao.delete(removedEntities.map { it.id })
        queue.push(removedEntities)
    }

    inner class DownloadChangeObserver : ContentObserver(null) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange)
            val changedId = uri?.lastPathSegment
                ?.toLongOrNull()
                ?.takeIf { it in entities }
                ?: return
            executorService.submit {
                context.contentResolver
                    .query(URI, projection, "${DownloadManager.COLUMN_ID} = $changedId", null, null)
                    ?.useCursor {
                        if (it.moveToFirst()) {
                            val id = it.getLong(it.getColumnIndexOrThrow(DownloadManager.COLUMN_ID))
                            updateDownloads(mapOf(id to it.parseDownload()))
                        } else {
                            removeEntity(changedId)
                        }
                    }
            }
        }
    }

    data class Download(
        val bytes: Long,
        val status: Int,
        val reason: Int,
        val lastTimeModified: Long
    ) {
        fun isSuccessful() =
            status and DownloadManager.STATUS_SUCCESSFUL == DownloadManager.STATUS_SUCCESSFUL

        fun isFailed() = status and DownloadManager.STATUS_FAILED == DownloadManager.STATUS_FAILED

        fun code() = when {
            isSuccessful() -> 0
            isFailed() -> reason
            else -> -1
        }
    }
}