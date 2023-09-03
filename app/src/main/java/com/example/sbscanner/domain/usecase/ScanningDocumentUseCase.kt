package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.models.Box
import com.example.sbscanner.domain.models.FullBox
import com.example.sbscanner.domain.repository.BoxRepository
import com.example.sbscanner.presentation.camera2.CameraScanner
import kotlinx.coroutines.flow.flow

private val REGEX = "^[A-Za-z0-9]{7,}$".toRegex()

sealed class ScanningDocumentEvent {
    data class ErrorBoxId(val boxId: Int) : ScanningDocumentEvent()
    data class FoundBarcode(val barcode: String) : ScanningDocumentEvent()
    sealed class BarcodeType : ScanningDocumentEvent() {
        data class BoxBarcode(val box: FullBox) : BarcodeType()
        data class NewDocBarcode(val barcode: String) : BarcodeType()
        data class DocExistsInCurrentBox(val docId: Int) : BarcodeType()
        data class DocExistsInAnotherBox(val box: Box, val docId: Int) : BarcodeType()
    }
}

class ScanningDocumentUseCase(
    private val boxRepository: BoxRepository,
) {

    operator fun invoke(cameraScanner: CameraScanner, boxId: Int) = flow {
        val fbox = boxRepository.getFullBox(boxId)

        if (fbox == null) {
            emit(ScanningDocumentEvent.ErrorBoxId(boxId))
            return@flow
        }

        val barcode = cameraScanner.startScanning(REGEX)
        cameraScanner.startPreview()
        emit(ScanningDocumentEvent.FoundBarcode(barcode))

        if (barcode == fbox.box.barcode) {
            emit(ScanningDocumentEvent.BarcodeType.BoxBarcode(fbox))
            return@flow
        }

        val item = boxRepository.getBoxesWithDocuments().firstOrNull { box ->
            box.documents.any { doc -> doc.barcode == barcode }
        }
        if (item == null) {
            emit(ScanningDocumentEvent.BarcodeType.NewDocBarcode(barcode))
            return@flow
        }
        val document = item.documents.first { it.barcode == barcode }
        if (item.box.id == boxId) {
            emit(ScanningDocumentEvent.BarcodeType.DocExistsInCurrentBox(document.id))
        } else {
            emit(ScanningDocumentEvent.BarcodeType.DocExistsInAnotherBox(item.box, document.id))
        }
    }
}
