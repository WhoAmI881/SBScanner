package com.example.sbscanner.presentation.fragments.document.scanner

import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.presentation.camera2.CameraScanner
import com.example.sbscanner.presentation.fragments.base.CameraState
import com.example.sbscanner.presentation.fragments.base.FormState

data class State(
    val boxId: Int = EMPTY_ID,
    val docId: Int = EMPTY_ID,
    val cameraState: CameraState = CameraState.INIT,
    val formState: FormState = FormState.SCANNING
)

sealed class Event {

    sealed class Ui : Event() {
        data class Init(val boxId: Int) : Ui()
        data class ChangeCameraState(val cameraState: CameraState) : Ui()
        data class CameraInit(val cameraScanner: CameraScanner) : Ui()
        object CloseDocForm : Ui()
        object CloseModal : Ui()
    }

    sealed class Internal : Event() {
        data class ReceivedBarcode(val barcode: String) : Internal()
        data class FoundInCurrentBox(val docId: Int) : Internal()
        data class FoundInAnotherBox(val boxBarcode: String) : Internal()
        data class NotFound(val docBarcode: String) : Internal()
    }
}

sealed class Effect {
    data class ShowErrorFoundMessage(val barcode: String) : Effect()
    data class OpenDocumentAdd(val boxId: Int, val barcode: String) : Effect()
    data class OpenDocumentEdit(val boxId: Int, val docId: Int) : Effect()
}

sealed class Command {
    data class StartScanning(val cameraScanner: CameraScanner, val boxId: Int) : Command()
}
