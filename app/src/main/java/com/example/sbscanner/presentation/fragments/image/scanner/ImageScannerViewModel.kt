package com.example.sbscanner.presentation.fragments.image.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.sbscanner.App
import com.example.sbscanner.domain.usecase.*
import com.example.sbscanner.domain.utils.isEmptyId
import com.example.sbscanner.domain.utils.isNotEmptyId
import com.example.sbscanner.presentation.camera2.CameraScanner
import com.example.sbscanner.presentation.fragments.base.BaseViewModel
import com.example.sbscanner.presentation.fragments.base.CameraState
import com.example.sbscanner.presentation.fragments.base.FormState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ImageScannerViewModel(
    private val scanningDocumentUseCase: ScanningDocumentUseCase,
    private val takePhotoUseCase: TakePhotoUseCase,
    private val getFullBoxUseCase: GetFullBoxUseCase
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
                    FormState.BARCODE_FOUND -> {
                        commitCommand(Command.StartPreview(this))
                    }
                    FormState.TAKE_PHOTO -> {
                        commitCommand(Command.StartPreview(this))
                    }
                }
                setState(currentState.copy(cameraState = CameraState.SUCCESS))
                cameraScanner = this
            }
            is Event.Ui.CloseModal -> cameraScanner?.let {
                commitCommand(Command.StartScanning(it, currentState.boxId))
                setState(currentState.copy(formState = FormState.SCANNING))
            }
            is Event.Ui.CloseDocForm -> if (event.docId.isEmptyId()) {
                cameraScanner?.let {
                    commitCommand(Command.StartScanning(it, currentState.boxId))
                    setState(currentState.copy(formState = FormState.SCANNING))
                }
            } else {
                setState(currentState.copy(docId = event.docId, formState = FormState.TAKE_PHOTO))
            }
            is Event.Ui.CloseImageForm -> if (event.imgId.isNotEmptyId()) {
                cameraScanner?.let {
                    commitCommand(Command.StartScanning(it, currentState.boxId))
                    setState(currentState.copy(formState = FormState.SCANNING))
                }
            }
            is Event.Ui.TakePhotoClick -> cameraScanner?.let {
                commitCommand(Command.TakePhoto(it))
            }
            is Event.Ui.ReturnBack -> {
                commitCommand(Command.LoadFullBox(currentState.boxId))
            }

            is Event.Internal.ReceivedBarcode -> {
                setState(currentState.copy(formState = FormState.BARCODE_FOUND))
            }
            is Event.Internal.CreatedPhoto -> {
                commitEffect(Effect.ShowImageDialog(currentState.docId, event.imgPath))
            }
            is Event.Internal.FoundBoxBarcode -> {
                if (event.emptyDocsCount == 0) {
                    commitEffect(Effect.CloseScanning)
                } else {
                    commitEffect(Effect.ShowWarningMessage(event.emptyDocsCount))
                }
            }
            is Event.Internal.FoundInCurrentBox -> {
                setState(currentState.copy(docId = event.docId, formState = FormState.TAKE_PHOTO))
            }
            is Event.Internal.FoundNewDocBarcode -> {
                commitEffect(Effect.ShowDocDialog(currentState.boxId, event.docBarcode))
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
                        is ScanningDocumentEvent.ErrorBoxId -> {}
                        is ScanningDocumentEvent.FoundBarcode -> {
                            emit(Event.Internal.ReceivedBarcode(it.barcode))
                        }
                        is ScanningDocumentEvent.BarcodeType -> when (it) {
                            is ScanningDocumentEvent.BarcodeType.BoxBarcode -> {
                                val emptyDoc =
                                    it.box.documents.count { doc -> doc.images.isEmpty() }
                                emit(Event.Internal.FoundBoxBarcode(emptyDoc))
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
            is Command.TakePhoto -> flow {
                takePhotoUseCase(command.cameraScanner)?.let {
                    emit(Event.Internal.CreatedPhoto(it))
                }
            }
            is Command.StartPreview -> flow {
                command.cameraScanner.startPreview()
            }
            is Command.LoadFullBox -> flow {
                getFullBoxUseCase(command.boxId)?.let {
                    val emptyDoc = it.documents.count { doc -> doc.images.isEmpty() }
                    emit(Event.Internal.FoundBoxBarcode(emptyDoc))
                    return@flow
                }
                emit(Event.Internal.FoundBoxBarcode(0))
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
                return ImageScannerViewModel(
                    application.scanningDocumentUseCase,
                    application.takePhotoUseCase,
                    application.getFullBoxUseCase
                ) as T
            }
        }
    }
}
