package com.example.sbscanner.presentation.fragments.box.scanner

import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.presentation.camera2.CameraScanner
import com.example.sbscanner.presentation.fragments.base.CameraStateType
import com.example.sbscanner.presentation.fragments.base.FormStateType

data class State(
    val taskId: Int = EMPTY_ID,
    val cameraStateType: CameraStateType = CameraStateType.INIT,
    val formStateType: FormStateType = FormStateType.SCANNING,
)

sealed class Event {

    sealed class Ui : Event() {
        data class Init(val taskId: Int) : Ui()
        data class ChangeCameraState(val cameraStateType: CameraStateType) : Ui()
        data class BarcodeFound(val barcode: String) : Ui()
    }

    sealed class Internal : Event() {
        data class SavedBox(val boxId: Int) : Internal()
    }
}

sealed class Effect {
    object StartScanning : Effect()
    data class OpenDocumentList(val boxId: Int) : Effect()
}

sealed class Command {
    data class TrySaveBox(val boxBarcode: String, val taskId: Int) : Command()
}
