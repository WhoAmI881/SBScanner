package com.example.sbscanner.domain.repository

import android.graphics.Bitmap
import com.example.sbscanner.domain.models.Box
import com.example.sbscanner.domain.models.Document
import com.example.sbscanner.domain.models.Image
import com.example.sbscanner.domain.models.SendImageForm
import com.example.sbscanner.domain.utils.ResultWrapper
import kotlinx.coroutines.flow.Flow

interface ImageRepository {

    suspend fun getImagesByDocId(docId: Int): List<Image>

    suspend fun addImage(docId: Int, image: Image): Int

    suspend fun saveTemporaryBitmap(bitmap: Bitmap): String?

    fun getImagesFlowByDocId(docId: Int): Flow<List<Image>>

    suspend fun removeImage(image: Image): Boolean

    suspend fun getImage(imgId: Int): Image?

    suspend fun getImageBitmap(image: Image): Bitmap?

    fun getAllImagesFlow(): Flow<List<Image>>

    suspend fun getAllImages(): List<Image>

    suspend fun sendImage(form: SendImageForm): ResultWrapper<Int>
}
