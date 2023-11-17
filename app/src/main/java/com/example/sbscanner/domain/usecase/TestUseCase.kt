package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.repository.BoxRepository
import com.example.sbscanner.domain.repository.DocumentRepository
import com.example.sbscanner.domain.repository.ImageRepository
import com.example.sbscanner.domain.repository.TaskRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

sealed class TestResult {
    data class Progress(val progress: Int) : TestResult()
    object Success : TestResult()
}

class TestUseCase(
    private val taskRepository: TaskRepository,
    private val boxRepository: BoxRepository,
    private val documentRepository: DocumentRepository,
    private val imageRepository: ImageRepository
) {

    operator fun invoke(boxCount: Int, docCount: Int, imgCount: Int) = flow {
        /*
val images = imageRepository.getAllImages().ifEmpty {
    emit(TestResult.Success)
    return@flow
}
val tasks = taskRepository.getAllTasks().ifEmpty {
    emit(TestResult.Success)
    return@flow
}

val bitmap = imageRepository.getImageBitmap(images.last())
val testImage = images.last().copy(id = EMPTY_ID, bitmap = bitmap, isSending = false)
val testTask = tasks.last()

val allImgCount = imgCount * docCount * boxCount
var imgSaved = 0

List(boxCount) { b_index ->
    val box = Box(EMPTY_ID, "${testTask.barcode}_BOX_${b_index}")
    val boxId = boxRepository.addBox(testTask.id, box)
    List(docCount) { d_index ->
        val document = Document(EMPTY_ID, "${testTask.barcode}_DOC_${b_index}_${d_index}")
        val docId = documentRepository.addDocument(boxId, document)
        List(imgCount) { img ->
            imageRepository.addImage(docId, testImage)
            val progress = (++imgSaved * 100) / allImgCount
            emit(TestResult.Progress(progress))
        }
    }
}
 */
        emit(TestResult.Progress(100))
        emit(TestResult.Success)
    }.distinctUntilChanged()
}
