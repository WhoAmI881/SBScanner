package com.example.sbscanner.data.repository

import com.example.sbscanner.data.source.local.ImageLocalDataSource
import com.example.sbscanner.data.source.remote.ImageRemoteDataSource
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

    override fun getImagesFlowByDocId(docId: Int): Flow<List<Image>> {
        return imageLocalDataSource.getImagesFlowByDocId(docId)
    }

    override suspend fun removeImage(imgId: Int): Boolean {
        return imageLocalDataSource.removeImage(imgId)
    }

    override suspend fun getImage(imgId: Int): Image? {
        return imageLocalDataSource.getImage(imgId)
    }

    override suspend fun sendImage(form: SendImageForm): ResultWrapper<Int> {
        val bytes = imageLocalDataSource.getImageAsBytes(form.image)
            ?: return ResultWrapper.SuccessResponse(SUCCESS_CODE)
        val result = imageRemoteDataSource.sendImage(form, bytes)
        if (result is ResultWrapper.SuccessResponse && result.value.isSuccessCode()) {
            imageLocalDataSource.setImageSendingFlag(form.image.id)
        }
        return result
    }
}
