package com.example.sbscanner.domain.repository

import com.example.sbscanner.domain.models.Image
import com.example.sbscanner.domain.models.SendImageForm
import com.example.sbscanner.domain.utils.ResultWrapper
import kotlinx.coroutines.flow.Flow

interface ImageRepository {

    suspend fun getImagesByDocId(docId: Int): List<Image>

    suspend fun addImage(docId: Int, image: Image): Int

    fun getImagesFlowByDocId(docId: Int): Flow<List<Image>>

    suspend fun removeImage(imgId: Int): Boolean

    suspend fun getImage(imgId: Int): Image?

    suspend fun sendImage(form: SendImageForm): ResultWrapper<Int>
}
