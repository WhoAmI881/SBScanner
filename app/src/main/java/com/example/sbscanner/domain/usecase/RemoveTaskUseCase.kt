package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.repository.BoxRepository
import com.example.sbscanner.domain.repository.DocumentRepository
import com.example.sbscanner.domain.repository.ImageRepository
import com.example.sbscanner.domain.repository.TaskRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

sealed class RemoveTaskResult {

    data class Start(val imgCount: Int) : RemoveTaskResult()
    data class ErrorRemoveImage(val imgId: Int) : RemoveTaskResult()
    data class RemovedImage(val imgId: Int, val progress: Int) : RemoveTaskResult()
    object Success : RemoveTaskResult()
}

class RemoveTaskUseCase(
    private val taskRepository: TaskRepository,
    private val boxRepository: BoxRepository,
    private val documentRepository: DocumentRepository,
    private val imageRepository: ImageRepository,
) {

    operator fun invoke(taskId: Int) = flow {
        val boxes = boxRepository.getFullBoxesByTaskId(taskId).ifEmpty {
            taskRepository.removeTask(taskId)
            emit(RemoveTaskResult.Success)
            return@flow
        }

        val imgCount = boxes.flatMap { it.documents }.sumOf { it.images.size }
        var imgRemoved = 0
        emit(RemoveTaskResult.Start(imgCount))

        boxes.forEach { box ->
            box.documents.forEach { doc ->
                doc.images.forEach { img ->
                    if (imageRepository.removeImage(img).not()) {
                        emit(RemoveTaskResult.ErrorRemoveImage(img.id))
                        return@flow
                    }
                    val progress = (++imgRemoved * 100) / imgCount
                    emit(RemoveTaskResult.RemovedImage(img.id, progress))
                }
                documentRepository.removeDocument(doc.document.id)
            }
            boxRepository.removeBox(box.box.id)
        }
        taskRepository.removeTask(taskId)
        emit(RemoveTaskResult.Success)
    }.distinctUntilChanged()
}
