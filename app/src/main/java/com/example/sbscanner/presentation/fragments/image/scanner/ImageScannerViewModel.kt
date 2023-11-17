package com.example.sbscanner.presentation.fragments.image.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.sbscanner.App
import com.example.sbscanner.domain.usecase.*
import com.example.sbscanner.domain.utils.isEmptyId
import com.example.sbscanner.domain.utils.isNotEmptyId
import com.example.sbscanner.presentation.fragments.base.BaseViewModel
import com.example.sbscanner.presentation.fragments.base.CameraStateType
import com.example.sbscanner.presentation.fragments.base.FormStateType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ImageScannerViewModel(
    private val searchDocumentUseCase: SearchDocumentUseCase,
    private val getFullBoxUseCase: GetFullBoxUseCase
) : BaseViewModel<Event, Effect, Command, State>(State()) {

    override fun reduce(event: Event) {
        when (event) {
            is Event.Ui.Init -> {
                setState(currentState.copy(boxId = event.boxId))
            }

            is Event.Ui.CameraStateChange -> {
                setState(currentState.copy(cameraStateType = event.cameraStateType))

                if (event.cameraStateType != CameraStateType.OPEN) return

                when (currentState.formStateType) {
                    FormStateType.SCANNING -> {
                        commitEffect(Effect.StartScanning)
                    }

                    FormStateType.BARCODE_FOUND -> {}

                    FormStateType.TAKE_PHOTO -> {}
                }
            }

            is Event.Ui.BarcodeFound -> {
                setState(currentState.copy(formStateType = FormStateType.BARCODE_FOUND))
                commitCommand(Command.SearchDocument(event.barcode, currentState.boxId))
            }

            is Event.Ui.PhotoCreated -> {
                event.uri?.path?.let {
                    commitEffect(Effect.ShowImageDialog(currentState.docId, it))
                }
            }

            is Event.Ui.CloseModal -> {
                if (currentState.formStateType != FormStateType.TAKE_PHOTO) {
                    commitEffect(Effect.StartScanning)
                    setState(currentState.copy(formStateType = FormStateType.SCANNING))
                }
            }

            is Event.Ui.CloseDocForm -> if (event.docId.isEmptyId()) {
                commitEffect(Effect.StartScanning)
                setState(currentState.copy(formStateType = FormStateType.SCANNING))
            } else {
                setState(
                    currentState.copy(
                        docId = event.docId,
                        formStateType = FormStateType.TAKE_PHOTO
                    )
                )
            }

            is Event.Ui.CloseImageForm -> if (event.imgId.isNotEmptyId()) {
                commitEffect(Effect.StartScanning)
                setState(currentState.copy(formStateType = FormStateType.SCANNING))
            }

            is Event.Ui.ReturnBack -> {
                commitCommand(Command.CheckBoxIsCompleted(currentState.boxId))
            }


            is Event.Internal.FinishScanning -> {
                commitEffect(Effect.CloseScanning)
            }

            is Event.Internal.BoxIsNotCompleted -> {
                if (currentState.formStateType == FormStateType.SCANNING) {
                    commitEffect(Effect.StopScanning)
                }
                commitEffect(Effect.ShowWarningMessage(event.emptyDocumentsCount))
            }

            is Event.Internal.FoundInCurrentBox -> {
                setState(
                    currentState.copy(
                        docId = event.docId,
                        formStateType = FormStateType.TAKE_PHOTO
                    )
                )
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
            is Command.SearchDocument -> flow {
                when (val result = searchDocumentUseCase(command.docBarcode, command.boxId)) {
                    is SearchDocumentResult.ErrorBoxId -> {}

                    is SearchDocumentResult.BarcodeType -> when (result) {
                        is SearchDocumentResult.BarcodeType.BoxBarcode -> {
                            val documents = result.box.getDocumentsWithoutPictures()
                            if (documents.isEmpty()) {
                                emit(Event.Internal.FinishScanning)
                            } else {
                                emit(Event.Internal.BoxIsNotCompleted(documents.size))
                            }
                        }

                        is SearchDocumentResult.BarcodeType.NewDocBarcode -> {
                            emit(Event.Internal.FoundNewDocBarcode(docBarcode = result.barcode))
                        }

                        is SearchDocumentResult.BarcodeType.DocExistsInAnotherBox -> {
                            emit(Event.Internal.FoundInAnotherBox(result.box.barcode))
                        }

                        is SearchDocumentResult.BarcodeType.DocExistsInCurrentBox -> {
                            emit(Event.Internal.FoundInCurrentBox(result.docId))
                        }
                    }
                }
            }

            is Command.CheckBoxIsCompleted -> flow {
                getFullBoxUseCase(command.boxId)?.let {
                    val documents = it.getDocumentsWithoutPictures()
                    if (documents.isEmpty()) {
                        emit(Event.Internal.FinishScanning)
                    } else {
                        emit(Event.Internal.BoxIsNotCompleted(documents.size))
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
                return ImageScannerViewModel(
                    application.searchDocumentUseCase,
                    application.getFullBoxUseCase
                ) as T
            }
        }
    }
}

/*
            is Command.StartScanning -> flow {
                scanningDocumentUseCase(command.cameraScanner, command.boxId).collect {
                    when (it) {
                        is ScanningDocumentResult.ErrorBoxId -> {}
                        is ScanningDocumentResult.FoundBarcode -> {
                            emit(Event.Internal.ReceivedBarcode(it.barcode))
                        }

                        is ScanningDocumentResult.BarcodeType -> when (it) {
                            is ScanningDocumentResult.BarcodeType.BoxBarcode -> {
                                val emptyDoc =
                                    it.box.documents.count { doc -> doc.images.isEmpty() }
                                emit(Event.Internal.FoundBoxBarcode(emptyDoc))
                            }

                            is ScanningDocumentResult.BarcodeType.NewDocBarcode -> {
                                emit(Event.Internal.FoundNewDocBarcode(docBarcode = it.barcode))
                            }

                            is ScanningDocumentResult.BarcodeType.DocExistsInAnotherBox -> {
                                emit(Event.Internal.FoundInAnotherBox(it.box.barcode))
                            }

                            is ScanningDocumentResult.BarcodeType.DocExistsInCurrentBox -> {
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

            is Event.Ui.ChangeCameraState -> {
                setState(currentState.copy(cameraStateType = event.cameraStateType))
            }

            is Event.Ui.CameraInit -> with(event.cameraScanner) {
                when (currentState.formStateType) {
                    FormStateType.SCANNING -> {
                        commitCommand(Command.StartScanning(this, currentState.boxId))
                    }

                    FormStateType.BARCODE_FOUND -> {
                        commitCommand(Command.StartPreview(this))
                    }

                    FormStateType.TAKE_PHOTO -> {
                        commitCommand(Command.StartPreview(this))
                    }
                }
                setState(
                    currentState.copy(
                        cameraStateType = CameraStateType.SUCCESS,
                        cameraScanner = this
                    )
                )
            }


            is Event.Ui.TakePhotoClick -> currentState.cameraScanner?.let {
                commitCommand(Command.TakePhoto(it))
            }

 */