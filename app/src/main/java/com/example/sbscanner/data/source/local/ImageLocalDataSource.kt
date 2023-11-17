package com.example.sbscanner.data.source.local

import com.example.sbscanner.domain.models.Image
import kotlinx.coroutines.flow.Flow

interface ImageLocalDataSource {

    suspend fun getImagesByDocId(docId: Int): List<Image>

    suspend fun addImage(docId: Int, image: Image): Int

    suspend fun getImageAsBytes(image: Image): ByteArray?

    fun getImagesFlowByDocId(docId: Int): Flow<List<Image>>

    suspend fun removeImage(imgId: Int): Boolean

    suspend fun getImage(imgId: Int): Image?

    suspend fun setImageSendingFlag(imageId: Int)
}
