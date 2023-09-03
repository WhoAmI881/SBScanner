package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.models.Box
import com.example.sbscanner.domain.repository.BoxRepository
import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.domain.utils.getCurrentTimestamp
import com.example.sbscanner.presentation.camera2.CameraScanner
import kotlinx.coroutines.flow.flow

private val REGEX = "^[A-Za-z0-9]{7,}$".toRegex()

sealed class ScanningBoxEvent {
    data class FoundBarcode(val barcode: String) : ScanningBoxEvent()
    data class BoxSaved(val boxId: Int) : ScanningBoxEvent()
    data class BoxAlreadyExists(val boxId: Int) : ScanningBoxEvent()
}

class ScanningBoxUseCase(
    private val boxRepository: BoxRepository
) {

    suspend operator fun invoke(cameraScanner: CameraScanner, taskId: Int) = flow {
        val barcode = cameraScanner.startScanning(REGEX)
        emit(ScanningBoxEvent.FoundBarcode(barcode))
        cameraScanner.startPreview()
        val box = Box(barcode = barcode)
        val boxId = boxRepository.getBoxId(taskId, box)
        if (boxId == EMPTY_ID) {
            val id = boxRepository.addBox(taskId, box.copy(timestamp = getCurrentTimestamp()))
            emit(ScanningBoxEvent.BoxSaved(id))
        } else {
            emit(ScanningBoxEvent.BoxAlreadyExists(boxId))
        }
    }
}
