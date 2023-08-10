package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.models.Box
import com.example.sbscanner.domain.repository.BoxRepository

class GetBoxUseCase(
    private val boxRepository: BoxRepository
) {

    suspend operator fun invoke(boxId: Int): Box? {
        return boxRepository.getBox(boxId)
    }
}
