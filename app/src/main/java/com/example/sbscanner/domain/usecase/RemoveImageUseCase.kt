package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.models.Image
import com.example.sbscanner.domain.repository.ImageRepository

class RemoveImageUseCase(
    private val imageRepository: ImageRepository
) {

    suspend operator fun invoke(image: Image) {
        imageRepository.removeImage(image)
    }
}
