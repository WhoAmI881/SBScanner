package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.models.Box
import com.example.sbscanner.domain.models.FullBox
import com.example.sbscanner.domain.repository.BoxRepository

sealed class SearchDocumentResult {
    data class ErrorBoxId(val boxId: Int) : SearchDocumentResult()
    sealed class BarcodeType : SearchDocumentResult() {
        data class BoxBarcode(val box: FullBox) : BarcodeType()
        data class NewDocBarcode(val barcode: String) : BarcodeType()
        data class DocExistsInCurrentBox(val docId: Int) : BarcodeType()
        data class DocExistsInAnotherBox(val box: Box, val docId: Int) : BarcodeType()
    }
}

class SearchDocumentUseCase(
    private val boxRepository: BoxRepository,
) {

    suspend operator fun invoke(docBarcode: String, boxId: Int): SearchDocumentResult {
        val fbox = boxRepository.getFullBox(boxId)
            ?: return SearchDocumentResult.ErrorBoxId(boxId)

        if (docBarcode == fbox.box.barcode) {
            return SearchDocumentResult.BarcodeType.BoxBarcode(fbox)
        }

        val item = boxRepository.getBoxesWithDocuments().firstOrNull { box ->
            box.documents.any { doc -> doc.barcode == docBarcode }
        } ?: return SearchDocumentResult.BarcodeType.NewDocBarcode(docBarcode)

        val document = item.documents.first { it.barcode == docBarcode }
        return if (item.box.id == boxId) {
            SearchDocumentResult.BarcodeType.DocExistsInCurrentBox(document.id)
        } else {
            SearchDocumentResult.BarcodeType.DocExistsInAnotherBox(item.box, document.id)
        }
    }

    /*

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

    operator fun invoke(camera: CameraScanner, boxId: Int) = flow {
        val fbox = boxRepository.getFullBox(boxId)

        if (fbox == null) {
            emit(ScanningDocumentEvent.ErrorBoxId(boxId))
            return@flow
        }

        val barcode = camera.startScanning(REGEX)
        camera.startPreview()
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

     */
}
