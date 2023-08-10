package com.example.sbscanner.presentation.fragments.box.scanner

import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.presentation.camera2.CameraScanner
import com.example.sbscanner.presentation.fragments.base.CameraState
import com.example.sbscanner.presentation.fragments.base.FormState

data class State(
    val taskId: Int = EMPTY_ID,
    val boxBarcode: String = "",
    val cameraState: CameraState = CameraState.INIT,
    val formState: FormState = FormState.SCANNING
)

sealed class Event {

    sealed class Ui : Event() {
        data class Init(val taskId: Int) : Ui()
        data class ChangeCameraState(val cameraState: CameraState): Ui()
        data class CameraInit(val cameraScanner: CameraScanner): Ui()
    }

    sealed class Internal : Event() {
        data class ReceivedBarcode(val barcode: String): Internal()
        data class SavedBox(val boxId: Int) : Internal()
    }
}

sealed class Effect {
    data class OpenDocumentList(val boxId: Int) : Effect()
}

sealed class Command {
    data class StartScanning(val cameraScanner: CameraScanner, val taskId: Int): Command()
}
