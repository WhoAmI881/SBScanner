package com.example.sbscanner.presentation.fragments.image.scanner

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
        object CloseModal : Ui()
        object ReturnBack: Ui()
        data class CloseDocForm(val docId: Int = EMPTY_ID) : Ui()
        data class CloseImageForm(val imgId: Int = EMPTY_ID) : Ui()
        object TakePhotoClick : Ui()
    }

    sealed class Internal : Event() {
        data class ReceivedBarcode(val barcode: String) : Internal()
        data class CreatedPhoto(val imgPath: String) : Internal()
        data class FoundInCurrentBox(val docId: Int) : Internal()
        data class FoundInAnotherBox(val boxBarcode: String) : Internal()
        data class FoundNewDocBarcode(val docBarcode: String): Internal()
        data class FoundBoxBarcode(val emptyDocsCount: Int): Internal()
    }
}

sealed class Effect {
    data class ShowErrorFoundMessage(val barcode: String) : Effect()
    data class ShowImageDialog(val docId: Int, val imgPath: String) : Effect()
    data class ShowDocDialog(val boxId: Int, val docBarcode: String) : Effect()
    data class ShowWarningMessage(val emptyDocCount: Int): Effect()
    object CloseScanning: Effect()
}

sealed class Command {
    data class StartScanning(val cameraScanner: CameraScanner, val boxId: Int) : Command()
    data class TakePhoto(val cameraScanner: CameraScanner) : Command()
    data class StartPreview(val cameraScanner: CameraScanner) : Command()
    data class LoadFullBox(val boxId: Int): Command()
}
