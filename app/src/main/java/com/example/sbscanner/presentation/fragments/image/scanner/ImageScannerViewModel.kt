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
) : BaseViewModel<Event, Effect, Command, State>(State()) {

    private var cameraScanner: CameraScanner? = null

    override suspend fun reduce(event: Event) {
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

            is Event.Internal.ReceivedBarcode -> {
                setState(currentState.copy(formState = FormState.BARCODE_FOUND))
            }
            is Event.Internal.CreatedPhoto -> {
                commitEffect(Effect.ShowImageDialog(currentState.docId, event.imgPath))
            }
            is Event.Internal.FoundInCurrentBox -> {
                setState(currentState.copy(docId = event.docId, formState = FormState.TAKE_PHOTO))
            }
            is Event.Internal.NotFound -> {
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
                        is ScanningResult.FoundBarcode -> {
                            emit(Event.Internal.ReceivedBarcode(it.barcode))
                        }
                        is ScanningResult.FoundInCurrentBox -> {
                            emit(Event.Internal.FoundInCurrentBox(it.document.id))
                        }
                        is ScanningResult.FoundInAnotherBox -> {
                            emit(Event.Internal.FoundInAnotherBox(it.box.barcode))
                        }
                        is ScanningResult.NotFound -> {
                            emit(Event.Internal.NotFound(docBarcode = it.barcode))
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
                ) as T
            }
        }
    }
}
