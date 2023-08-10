package com.example.sbscanner.data.repository

import android.graphics.Bitmap
import com.example.sbscanner.data.source.local.ImageLocalDataSource
import com.example.sbscanner.data.source.remote.ImageRemoteDataSource
import com.example.sbscanner.domain.models.Box
import com.example.sbscanner.domain.models.Document
import com.example.sbscanner.domain.models.Image
import com.example.sbscanner.domain.models.SendImageForm
import com.example.sbscanner.domain.repository.ImageRepository
import com.example.sbscanner.domain.utils.ResultWrapper
import com.example.sbscanner.domain.utils.SUCCESS_CODE
import com.example.sbscanner.domain.utils.isSuccessCode
import kotlinx.coroutines.flow.Flow

class ImageRepositoryImpl(
    private val imageLocalDataSource: ImageLocalDataSource,
    private val imageRemoteDataSource: ImageRemoteDataSource
) : ImageRepository {

    override suspend fun getImagesByDocId(docId: Int): List<Image> {
        return imageLocalDataSource.getImagesByDocId(docId)
    }

    override suspend fun addImage(docId: Int, image: Image): Int {
        return imageLocalDataSource.addImage(docId, image)
    }

    override suspend fun saveTemporaryBitmap(bitmap: Bitmap): String? {
        return imageLocalDataSource.saveTemporaryBitmap(bitmap)
    }

    override fun getImagesFlowByDocId(docId: Int): Flow<List<Image>> {
        return imageLocalDataSource.getImagesFlowByDocId(docId)
    }

    override suspend fun removeImage(image: Image): Boolean {
        return imageLocalDataSource.removeImage(image)
    }

    override suspend fun getImage(imgId: Int): Image? {
        return imageLocalDataSource.getImage(imgId)
    }

    override suspend fun getImageBitmap(image: Image): Bitmap? {
        return imageLocalDataSource.getImageBitmap(image)
    }

    override fun getAllImagesFlow(): Flow<List<Image>> {
        return imageLocalDataSource.getAllImagesFlow()
    }

    override suspend fun getAllImages(): List<Image> {
        return imageLocalDataSource.getAllImages()
    }

    override suspend fun sendImage(form: SendImageForm): ResultWrapper<Int> {
        val bitmap = imageLocalDataSource.getImageBitmap(form.image)
            ?: return ResultWrapper.SuccessResponse(SUCCESS_CODE)

        val result = imageRemoteDataSource.sendImage(form, bitmap)
        if (result is ResultWrapper.SuccessResponse && result.value.isSuccessCode()) {
            imageLocalDataSource.setImageSendingFlag(form.image.id)
        }
        return result
    }
}
