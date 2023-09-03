package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.models.FullBox
import com.example.sbscanner.domain.repository.BoxRepository

class GetFullBoxUseCase(
    private val boxRepository: BoxRepository
) {

    suspend operator fun invoke(boxId: Int): FullBox? {
        return boxRepository.getFullBox(boxId)
    }
}
