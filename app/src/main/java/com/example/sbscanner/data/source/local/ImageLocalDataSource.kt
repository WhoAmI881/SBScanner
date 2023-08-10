package com.example.sbscanner.data.source.local

import android.graphics.Bitmap
import com.example.sbscanner.domain.models.Image
import kotlinx.coroutines.flow.Flow

interface ImageLocalDataSource {

    suspend fun getImagesByDocId(docId: Int): List<Image>

    suspend fun addImage(docId: Int, image: Image): Int

    suspend fun saveTemporaryBitmap(bitmap: Bitmap): String?

    fun getImagesFlowByDocId(docId: Int): Flow<List<Image>>

    suspend fun removeImage(image: Image): Boolean

    suspend fun getImage(imgId: Int): Image?

    suspend fun getImageBitmap(image: Image): Bitmap?

    suspend fun setImageSendingFlag(imageId: Int)

    suspend fun getLastImageNotSending(): Image?

    fun getAllImagesFlow(): Flow<List<Image>>

    suspend fun getAllImages(): List<Image>
}
