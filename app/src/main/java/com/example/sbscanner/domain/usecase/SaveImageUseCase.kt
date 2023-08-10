package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.models.Image
import com.example.sbscanner.domain.repository.ImageRepository
import com.example.sbscanner.domain.utils.getCurrentTimestamp

class SaveImageUseCase(
    private val imageRepository: ImageRepository
) {

    suspend operator fun invoke(docId: Int, image: Image): Int {
        return imageRepository.addImage(docId, image.copy(timestamp = getCurrentTimestamp()))
    }
}
