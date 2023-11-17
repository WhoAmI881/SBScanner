package com.example.sbscanner.data.local.source

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
        val temp = fileManager.getFileFromInternalStorage(image.path) ?: return EMPTY_ID
        val path = fileManager.transferFileInInternalStorage(temp) ?: return EMPTY_ID
        return imagesDao.insertImage(image.copy(path = path).toLocal(docId)).toInt()
    }

    override suspend fun getImageAsBytes(image: Image): ByteArray? {
        return fileManager.getBytesFromFile(image.path)
    }

    override fun getImagesFlowByDocId(docId: Int): Flow<List<Image>> {
        return imagesDao.getImagesFlowByDocId(docId).map {
            it.map { img -> img.toDomain() }
        }
    }

    override suspend fun removeImage(imgId: Int): Boolean {
        val image = imagesDao.getImageById(imgId)
        image?.let {
            fileManager.deleteFile(it.path)
            imagesDao.deleteImage(image.id)
        }
        return true
    }

    override suspend fun getImage(imgId: Int): Image? {
        return imagesDao.getImageById(imgId)?.toDomain()
    }

    override suspend fun setImageSendingFlag(imageId: Int) {
        imagesDao.updateIsSendingImage(imageId, true)
    }
}
