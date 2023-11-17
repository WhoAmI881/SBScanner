package com.example.sbscanner.presentation.fragments.image.scanner

import android.net.Uri
import com.example.sbscanner.domain.utils.EMPTY_ID
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
        data class CameraStateChange(val cameraStateType: CameraStateType) : Ui()
        data class BarcodeFound(val barcode: String) : Ui()
        data class PhotoCreated(val uri: Uri?) : Ui()

        object CloseModal : Ui()
        object ReturnBack : Ui()
        data class CloseDocForm(val docId: Int = EMPTY_ID) : Ui()
        data class CloseImageForm(val imgId: Int = EMPTY_ID) : Ui()
    }

    sealed class Internal : Event() {
        object FinishScanning : Internal()
        data class BoxIsNotCompleted(val emptyDocumentsCount: Int) : Internal()
        data class FoundInCurrentBox(val docId: Int) : Internal()
        data class FoundInAnotherBox(val boxBarcode: String) : Internal()
        data class FoundNewDocBarcode(val docBarcode: String) : Internal()
    }
}

sealed class Effect {
    object StartScanning : Effect()
    object StopScanning : Effect()
    data class ShowErrorFoundMessage(val barcode: String) : Effect()
    data class ShowImageDialog(val docId: Int, val imgPath: String) : Effect()
    data class ShowDocDialog(val boxId: Int, val docBarcode: String) : Effect()
    data class ShowWarningMessage(val emptyDocCount: Int) : Effect()
    object CloseScanning : Effect()
}

sealed class Command {
    data class SearchDocument(val docBarcode: String, val boxId: Int) : Command()
    data class CheckBoxIsCompleted(val boxId: Int) : Command()
}
