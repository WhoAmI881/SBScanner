package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.models.Box
import com.example.sbscanner.domain.repository.BoxRepository
import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.domain.utils.getCurrentTimestamp

class SaveBoxUseCase(
    private val boxRepository: BoxRepository
) {

    suspend operator fun invoke(taskId: Int, box: Box): Int {
        val boxId = boxRepository.getBoxId(taskId, box)
        return if (boxId == EMPTY_ID) {
            boxRepository.addBox(taskId, box.copy(timestamp = getCurrentTimestamp()))
        } else {
            boxId
        }
    }
}
