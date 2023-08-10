package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.models.SendImageForm
import com.example.sbscanner.domain.repository.BoxRepository
import com.example.sbscanner.domain.repository.ImageRepository
import com.example.sbscanner.domain.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

sealed class SendTaskResult {

    data class StartUploadTask(val imgMaxCount: Int) : SendTaskResult()

    data class SentImage(val imgId: Int, val imgCount: Int) : SendTaskResult()

    object ServerError : SendTaskResult()

    object LoseConnection : SendTaskResult()

    object Success : SendTaskResult()
}

class SendTaskUseCase(
    private val boxRepository: BoxRepository,
    private val imageRepository: ImageRepository
) {

    suspend operator fun invoke(sessionId: Int, taskId: Int) = flow {
        val boxes = boxRepository.getFullBoxesByTaskId(taskId).getSendBoxes().ifEmpty {
            emit(SendTaskResult.Success)
            return@flow
        }

        val imgCount = boxes.flatMap { it.documents }
            .flatMap { it.images.filter { img -> !img.isSending } }.size
        var imgSent = 0

        emit(SendTaskResult.StartUploadTask(imgCount))

        boxes.forEach { fbox ->
            val lastImgInBox = fbox.documents.flatMap { it.images }.last()
            val boxCreate = fbox.box.timestamp.toDate()
            fbox.documents.forEach { fdoc ->
                val docCreate = fdoc.document.timestamp.toDate()
                fdoc.images.filter { it.isSending.not() }.forEach { img ->
                    val form = SendImageForm(
                        sessionId = sessionId,
                        image = img,
                        imgCreate = img.timestamp.toDate(),
                        box = fbox.box,
                        boxCreate = boxCreate,
                        document = fdoc.document,
                        docCreate = docCreate,
                        isLastImgInBox = img.id == lastImgInBox.id
                    )

                    var result = imageRepository.sendImage(form)
                    while (result is ResultWrapper.NetworkError) {
                        emit(SendTaskResult.LoseConnection)
                        delay(1000)
                        result = imageRepository.sendImage(form)
                    }

                    when (result) {
                        is ResultWrapper.SuccessResponse -> if (result.value.isSuccessCode()) {
                            emit(SendTaskResult.SentImage(img.id, ++imgSent))
                        } else {
                            emit(SendTaskResult.ServerError)
                            return@flow
                        }
                        else -> {
                            emit(SendTaskResult.ServerError)
                            return@flow
                        }
                    }
                }
            }
        }
        emit(SendTaskResult.Success)
    }
}
