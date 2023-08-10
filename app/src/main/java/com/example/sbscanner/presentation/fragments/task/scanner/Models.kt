package com.example.sbscanner.presentation.fragments.task.scanner

import com.example.sbscanner.presentation.camera2.CameraScanner
import com.example.sbscanner.presentation.fragments.base.CameraState
import com.example.sbscanner.presentation.fragments.base.FormState

data class State(
    val barcode: String = "",
    val cameraState: CameraState = CameraState.INIT,
    val formState: FormState = FormState.SCANNING
)

sealed class Event {

    sealed class Ui : Event() {
        object Init : Ui()
        data class ChangeCameraState(val cameraState: CameraState) : Ui()
        data class CameraInit(val cameraScanner: CameraScanner) : Ui()
    }

    sealed class Internal : Event() {
        data class ReceivedBarcode(val barcode: String) : Internal()
    }
}

sealed class Command {
    data class StartScanning(val cameraScanner: CameraScanner) : Command()
}

sealed class Effect {
    data class ReturnBack(val barcode: String) : Effect()
}
