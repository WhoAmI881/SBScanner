package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.repository.BoxRepository
import com.example.sbscanner.domain.repository.DocumentRepository
import com.example.sbscanner.domain.repository.ImageRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

sealed class RemoveBoxResult {

    data class Start(val imgCount: Int) : RemoveBoxResult()
    data class ErrorRemoveImage(val imgId: Int) : RemoveBoxResult()
    data class RemovedImage(val imgId: Int, val progress: Int) : RemoveBoxResult()
    object Success : RemoveBoxResult()
}

class RemoveBoxUseCase(
    private val boxRepository: BoxRepository,
    private val documentRepository: DocumentRepository,
    private val imageRepository: ImageRepository,
) {

    operator fun invoke(boxId: Int) = flow {
        val documents = documentRepository.getFullDocumentsByBoxId(boxId).ifEmpty {
            boxRepository.removeBox(boxId)
            emit(RemoveBoxResult.Success)
            return@flow
        }

        val imgCount = documents.sumOf { it.images.size }
        var imgRemoved = 0
        emit(RemoveBoxResult.Start(imgCount))

        documents.forEach { doc ->
            doc.images.forEach { img ->
                if (imageRepository.removeImage(img.id).not()) {
                    emit(RemoveBoxResult.ErrorRemoveImage(img.id))
                    return@flow
                }
                val progress = (++imgRemoved * 100) / imgCount
                emit(RemoveBoxResult.RemovedImage(img.id, progress))
            }
            documentRepository.removeDocument(doc.document.id)
        }
        boxRepository.removeBox(boxId)
        emit(RemoveBoxResult.Success)
    }.distinctUntilChanged()
}
