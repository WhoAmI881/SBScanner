package com.example.sbscanner.presentation.fragments.document.scanner

import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.presentation.camera2.CameraScanner
import com.example.sbscanner.presentation.fragments.base.CameraStateType
import com.example.sbscanner.presentation.fragments.base.FormStateType

data class State(
    val boxId: Int = EMPTY_ID,
    val docId: Int = EMPTY_ID,
    val cameraStateType: CameraStateType = CameraStateType.INIT,
    val formStateType: FormStateType = FormStateType.SCANNING,
)

sealed class Event {

    sealed class Ui : Event() {
        data class Init(val boxId: Int) : Ui()
        data class ChangeCameraState(val cameraStateType: CameraStateType) : Ui()
        data class BarcodeFound(val barcode: String) : Ui()
        object CloseDocForm : Ui()
        object CloseModal : Ui()
    }

    sealed class Internal : Event() {
        object FinishScanning : Internal()
        data class FoundNewDocBarcode(val docBarcode: String) : Internal()
        data class FoundInCurrentBox(val docId: Int) : Internal()
        data class FoundInAnotherBox(val boxBarcode: String) : Internal()
    }
}

sealed class Effect {
    object StartScanning: Effect()
    object StopScanning : Effect()
    data class ShowErrorFoundMessage(val barcode: String) : Effect()
    data class OpenDocumentAdd(val boxId: Int, val barcode: String) : Effect()
    data class OpenDocumentEdit(val boxId: Int, val docId: Int) : Effect()
    object CloseScanning : Effect()
}

sealed class Command {
    data class SearchDocument(val docBarcode: String, val boxId: Int) : Command()
}
