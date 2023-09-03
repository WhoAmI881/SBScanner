package com.example.sbscanner.presentation.fragments.document.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.sbscanner.App
import com.example.sbscanner.domain.usecase.ScanningDocumentUseCase
import com.example.sbscanner.domain.usecase.ScanningDocumentEvent
import com.example.sbscanner.presentation.camera2.CameraScanner
import com.example.sbscanner.presentation.fragments.base.BaseViewModel
import com.example.sbscanner.presentation.fragments.base.CameraState
import com.example.sbscanner.presentation.fragments.base.FormState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DocumentScannerViewModel(
    private val scanningDocumentUseCase: ScanningDocumentUseCase
) : BaseViewModel<Event, Effect, Command, State>(State()) {

    private var cameraScanner: CameraScanner? = null

    override fun reduce(event: Event) {
        when (event) {
            is Event.Ui.Init -> {
                setState(currentState.copy(boxId = event.boxId))
            }
            is Event.Ui.ChangeCameraState -> {
                setState(currentState.copy(cameraState = event.cameraState))
            }
            is Event.Ui.CameraInit -> with(event.cameraScanner) {
                when (currentState.formState) {
                    FormState.SCANNING -> {
                        commitCommand(Command.StartScanning(this, currentState.boxId))
                    }
                    FormState.BARCODE_FOUND -> {}
                    else -> {}
                }
                setState(currentState.copy(cameraState = CameraState.SUCCESS))
                cameraScanner = this
            }
            is Event.Ui.CloseModal -> {
                cameraScanner?.let {
                    setState(currentState.copy(formState = FormState.SCANNING))
                    commitCommand(Command.StartScanning(it, currentState.boxId))
                }
            }
            is Event.Ui.CloseDocForm -> {
                cameraScanner?.let {
                    setState(currentState.copy(formState = FormState.SCANNING))
                    commitCommand(Command.StartScanning(it, currentState.boxId))
                }
            }

            is Event.Internal.ReceivedBarcode -> {
                setState(currentState.copy(formState = FormState.BARCODE_FOUND))
            }
            is Event.Internal.FoundInCurrentBox -> {
                commitEffect(Effect.OpenDocumentEdit(currentState.boxId, event.docId))
            }
            is Event.Internal.FoundNewDocBarcode -> {
                commitEffect(Effect.OpenDocumentAdd(currentState.boxId, event.docBarcode))
            }
            is Event.Internal.FoundBoxBarcode -> {
                commitEffect(Effect.CloseScanning)
            }
            is Event.Internal.FoundInAnotherBox -> {
                commitEffect(Effect.ShowErrorFoundMessage(event.boxBarcode))
            }
        }
    }

    override suspend fun execute(command: Command): Flow<Event> {
        return when (command) {
            is Command.StartScanning -> flow {
                scanningDocumentUseCase(command.cameraScanner, command.boxId).collect {
                    when (it) {
                        is ScanningDocumentEvent.ErrorBoxId -> {

                        }
                        is ScanningDocumentEvent.FoundBarcode -> {
                            emit(Event.Internal.ReceivedBarcode(it.barcode))
                        }
                        is ScanningDocumentEvent.BarcodeType -> when (it) {
                            is ScanningDocumentEvent.BarcodeType.BoxBarcode -> {
                                emit(Event.Internal.FoundBoxBarcode)
                            }
                            is ScanningDocumentEvent.BarcodeType.NewDocBarcode -> {
                                emit(Event.Internal.FoundNewDocBarcode(docBarcode = it.barcode))
                            }
                            is ScanningDocumentEvent.BarcodeType.DocExistsInAnotherBox -> {
                                emit(Event.Internal.FoundInAnotherBox(it.box.barcode))
                            }
                            is ScanningDocumentEvent.BarcodeType.DocExistsInCurrentBox -> {
                                emit(Event.Internal.FoundInCurrentBox(it.docId))
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as App
                return DocumentScannerViewModel(
                    application.scanningDocumentUseCase
                ) as T
            }
        }
    }
}
