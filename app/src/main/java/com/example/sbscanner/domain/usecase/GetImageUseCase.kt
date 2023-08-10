package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.models.Image
import com.example.sbscanner.domain.repository.ImageRepository

class GetImageUseCase(
    private val imageRepository: ImageRepository
) {

    suspend operator fun invoke(imgId: Int): Image? {
        return imageRepository.getImage(imgId)
    }
}
