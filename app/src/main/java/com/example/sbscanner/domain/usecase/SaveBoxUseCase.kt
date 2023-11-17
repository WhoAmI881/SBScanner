package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.models.Box
import com.example.sbscanner.domain.repository.BoxRepository
import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.domain.utils.getCurrentTimestamp

sealed class SaveBoxResult {
    data class BoxSaved(val boxId: Int) : SaveBoxResult()
    data class BoxAlreadyExists(val boxId: Int) : SaveBoxResult()
}

class SaveBoxUseCase(
    private val boxRepository: BoxRepository
) {

    suspend operator fun invoke(boxBarcode: String, taskId: Int): SaveBoxResult {
        val box = Box(barcode = boxBarcode)
        val boxId = boxRepository.getBoxId(taskId, box)
        return if (boxId == EMPTY_ID) {
            val id = boxRepository.addBox(taskId, box.copy(timestamp = getCurrentTimestamp()))
            SaveBoxResult.BoxSaved(id)
        } else {
            SaveBoxResult.BoxAlreadyExists(boxId)
        }
    }
}
