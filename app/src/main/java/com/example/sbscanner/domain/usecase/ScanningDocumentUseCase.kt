package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.models.Box
import com.example.sbscanner.domain.models.Document
import com.example.sbscanner.domain.repository.BoxRepository
import com.example.sbscanner.presentation.camera2.CameraScanner
import kotlinx.coroutines.flow.flow

sealed class ScanningResult{
    data class FoundBarcode(val barcode: String): ScanningResult()
    data class NotFound(val barcode: String): ScanningResult()
    data class FoundInCurrentBox(val document: Document): ScanningResult()
    data class FoundInAnotherBox(val box: Box, val document: Document): ScanningResult()
}

class ScanningDocumentUseCase(
    private val boxRepository: BoxRepository,
) {

    operator fun invoke(cameraScanner: CameraScanner, boxId: Int) = flow {
        val barcode = cameraScanner.startScanning()
        emit(ScanningResult.FoundBarcode(barcode))
        cameraScanner.startPreview()

        val item = boxRepository.getBoxesWithDocuments().firstOrNull { box ->
            box.documents.any { doc -> doc.barcode == barcode }
        }
        if(item == null){
            emit(ScanningResult.NotFound(barcode))
            return@flow
        }
        val document = item.documents.first { it.barcode == barcode }
        if (item.box.id == boxId) {
            emit(ScanningResult.FoundInCurrentBox(document))
        } else {
            emit(ScanningResult.FoundInAnotherBox(item.box, document))
        }
    }
}
