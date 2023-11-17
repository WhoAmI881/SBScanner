package com.example.sbscanner.presentation.fragments.task.scanner

import com.example.sbscanner.presentation.fragments.base.CameraStateType
import com.example.sbscanner.presentation.fragments.base.FormStateType

data class State(
    val barcode: String = "",
    val cameraStateType: CameraStateType = CameraStateType.INIT,
    val formStateType: FormStateType = FormStateType.SCANNING
)

sealed class Event {

    sealed class Ui : Event() {
        object Init : Ui()
        data class ChangeCameraState(val cameraStateType: CameraStateType) : Ui()
    }

    sealed class Internal : Event() {
    }
}

sealed class Command {}

sealed class Effect {
    object StartScanning : Effect()
}
