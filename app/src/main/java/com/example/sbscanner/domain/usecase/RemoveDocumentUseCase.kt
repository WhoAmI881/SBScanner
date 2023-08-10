package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.repository.DocumentRepository
import com.example.sbscanner.domain.repository.ImageRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

sealed class RemoveDocResult {

    data class Start(val imgCount: Int) : RemoveDocResult()
    data class ErrorRemoveImage(val imgId: Int) : RemoveDocResult()
    data class RemovedImage(val imgId: Int, val progress: Int) : RemoveDocResult()
    object Success : RemoveDocResult()
}

class RemoveDocumentUseCase(
    private val documentRepository: DocumentRepository,
    private val imageRepository: ImageRepository
) {

    operator fun invoke(docId: Int) = flow {
        val images = imageRepository.getImagesByDocId(docId).ifEmpty {
            documentRepository.removeDocument(docId)
            emit(RemoveDocResult.Success)
            return@flow
        }

        val imgCount = images.size
        var imgRemoved = 0
        emit(RemoveDocResult.Start(imgCount))

        images.onEach {
            if (imageRepository.removeImage(it).not()) {
                emit(RemoveDocResult.ErrorRemoveImage(it.id))
                return@flow
            }
            val progress = (++imgRemoved * 100) / imgCount
            emit(RemoveDocResult.RemovedImage(it.id, progress))
        }
        documentRepository.removeDocument(docId)
        emit(RemoveDocResult.Success)
    }.distinctUntilChanged()
}
