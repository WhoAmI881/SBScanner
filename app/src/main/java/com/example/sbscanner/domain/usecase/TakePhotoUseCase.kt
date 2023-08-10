package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.repository.ImageRepository
import com.example.sbscanner.presentation.camera2.CameraScanner

class TakePhotoUseCase(
    private val imageRepository: ImageRepository
) {

    suspend operator fun invoke(cameraScanner: CameraScanner): String? {
        val bitmap = cameraScanner.takePhoto()
        return imageRepository.saveTemporaryBitmap(bitmap)
    }
}
