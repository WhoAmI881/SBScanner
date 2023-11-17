package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.repository.ImageRepository

class RemoveImageUseCase(
    private val imageRepository: ImageRepository
) {

    suspend operator fun invoke(imgId: Int) {
        imageRepository.removeImage(imgId)
    }
}
