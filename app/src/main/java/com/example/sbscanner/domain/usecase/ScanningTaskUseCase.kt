package com.example.sbscanner.domain.usecase

import com.example.sbscanner.presentation.camera2.CameraScanner

class ScanningTaskUseCase {

    suspend operator fun invoke(cameraScanner: CameraScanner): String {
        val barcode = cameraScanner.startScanning()
        cameraScanner.startPreview()
        return barcode
    }
}
