package com.example.sbscanner.presentation.fragments.document.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.sbscanner.App
import com.example.sbscanner.domain.usecase.SearchDocumentResult
import com.example.sbscanner.domain.usecase.SearchDocumentUseCase
import com.example.sbscanner.presentation.fragments.base.BaseViewModel
import com.example.sbscanner.presentation.fragments.base.CameraStateType
import com.example.sbscanner.presentation.fragments.base.FormStateType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DocumentScannerViewModel(
    private val searchDocumentUseCase: SearchDocumentUseCase
) : BaseViewModel<Event, Effect, Command, State>(State()) {

    override fun reduce(event: Event) {
        when (event) {
            is Event.Ui.Init -> {
                setState(currentState.copy(boxId = event.boxId))
            }

            is Event.Ui.ChangeCameraState -> {
                setState(currentState.copy(cameraStateType = event.cameraStateType))

                if (event.cameraStateType != CameraStateType.OPEN) return

                when (currentState.formStateType) {
                    FormStateType.SCANNING -> {
                        commitEffect(Effect.StartScanning)
                    }
                    FormStateType.BARCODE_FOUND -> {}
                    else -> {}
                }
            }

            is Event.Ui.BarcodeFound -> {
                setState(currentState.copy(formStateType = FormStateType.BARCODE_FOUND))
                commitCommand(Command.SearchDocument(event.barcode, currentState.boxId))
            }

            is Event.Ui.CloseModal -> {
                commitEffect(Effect.StartScanning)
                setState(currentState.copy(formStateType = FormStateType.SCANNING))
            }

            is Event.Ui.CloseDocForm -> {
                commitEffect(Effect.StartScanning)
                setState(currentState.copy(formStateType = FormStateType.SCANNING))
            }

            is Event.Internal.FoundInCurrentBox -> {
                commitEffect(Effect.OpenDocumentEdit(currentState.boxId, event.docId))
            }

            is Event.Internal.FoundNewDocBarcode -> {
                commitEffect(Effect.OpenDocumentAdd(currentState.boxId, event.docBarcode))
            }

            is Event.Internal.FinishScanning -> {
                commitEffect(Effect.CloseScanning)
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
                            emit(Event.Internal.FinishScanning)
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
                    application.searchDocumentUseCase
                ) as T
            }
        }
    }
}
