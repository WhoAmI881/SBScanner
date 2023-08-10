package com.example.sbscanner.data.local.source

import android.graphics.Bitmap
import com.example.sbscanner.data.local.db.dao.ImagesDao
import com.example.sbscanner.data.local.db.entities.image.toDomain
import com.example.sbscanner.data.local.db.entities.image.toLocal
import com.example.sbscanner.data.local.files.FileManager
import com.example.sbscanner.data.source.local.ImageLocalDataSource
import com.example.sbscanner.domain.models.Image
import com.example.sbscanner.domain.utils.EMPTY_ID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ImageLocalDataSourceImpl(
    private val imagesDao: ImagesDao,
    private val fileManager: FileManager
) : ImageLocalDataSource {

    override suspend fun getImagesByDocId(docId: Int): List<Image> {
        return imagesDao.getImagesByDocId(docId).map { it.toDomain() }
    }

    override suspend fun addImage(docId: Int, image: Image): Int {
        image.bitmap?.let {
            val path = fileManager.saveBitmap(it).ifBlank { return EMPTY_ID }
            return imagesDao.insertImage(image.copy(path = path).toLocal(docId)).toInt()
        }
        val temp = fileManager.getTempFileFromInternalStorage(image.path) ?: return EMPTY_ID
        val path = fileManager.saveTempFileInInternalStorage(temp) ?: return EMPTY_ID
        return imagesDao.insertImage(image.copy(path = path).toLocal(docId)).toInt()
    }

    override suspend fun saveTemporaryBitmap(bitmap: Bitmap): String? {
        return fileManager.saveBitmapAsTempFileInInternalStorage(bitmap)
    }

    override fun getImagesFlowByDocId(docId: Int): Flow<List<Image>> {
        return imagesDao.getImagesFlowByDocId(docId).map {
            it.map { img -> img.toDomain() }
        }
    }

    override suspend fun removeImage(image: Image): Boolean {
        return if (fileManager.deleteFile(image.path)) {
            imagesDao.deleteImage(image.id)
            true
        } else {
            true
        }
    }

    override suspend fun getImage(imgId: Int): Image? {
        return imagesDao.getImageById(imgId)?.toDomain()
    }

    override suspend fun getImageBitmap(image: Image): Bitmap? {
        val bitmap = fileManager.getBitmap(image.path)
        return if (bitmap == null) {
            imagesDao.deleteImage(image.id)
            null
        } else {
            bitmap
        }
    }

    override suspend fun setImageSendingFlag(imageId: Int) {
        imagesDao.updateIsSendingImage(imageId, true)
    }

    override suspend fun getLastImageNotSending(): Image? {
        val images = imagesDao.getImagesBySendingFlag(false).ifEmpty {
            return null
        }
        return images.last().toDomain()
    }

    override fun getAllImagesFlow(): Flow<List<Image>> {
        return imagesDao.getAllImagesFlow().map { items -> items.map { it.toDomain() } }
    }

    override suspend fun getAllImages(): List<Image> {
        return imagesDao.getAllImages().map { it.toDomain() }
    }
}
