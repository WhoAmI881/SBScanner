package com.example.sbscanner.presentation.fragments.box.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.sbscanner.App
import com.example.sbscanner.domain.usecase.ScanningBoxEvent
import com.example.sbscanner.domain.usecase.ScanningBoxUseCase
import com.example.sbscanner.presentation.fragments.base.BaseViewModel
import com.example.sbscanner.presentation.fragments.base.CameraState
import com.example.sbscanner.presentation.fragments.base.FormState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BoxScannerViewModel(
    private val scanningBoxUseCase: ScanningBoxUseCase,
) : BaseViewModel<Event, Effect, Command, State>(State()) {

    override fun reduce(event: Event) {
        return when (event) {
            is Event.Ui.Init -> {
                setState(currentState.copy(taskId = event.taskId))
            }
            is Event.Ui.ChangeCameraState -> {
                setState(currentState.copy(cameraState = event.cameraState))
            }
            is Event.Ui.CameraInit -> with(event.cameraScanner) {
                when (currentState.formState) {
                    FormState.SCANNING -> {
                        commitCommand(Command.StartScanning(this, currentState.taskId))
                    }
                    FormState.BARCODE_FOUND -> {
                        commitCommand(Command.StartScanning(this, currentState.taskId))
                    }
                    else -> {}
                }
                setState(currentState.copy(cameraState = CameraState.SUCCESS))
            }

            is Event.Internal.ReceivedBarcode -> {
                setState(currentState.copy(formState = FormState.BARCODE_FOUND))
            }
            is Event.Internal.SavedBox -> {
                commitEffect(Effect.OpenDocumentList(event.boxId))
            }
        }
    }

    override suspend fun execute(command: Command): Flow<Event> {
        return when (command) {
            is Command.StartScanning -> flow {
                scanningBoxUseCase(command.cameraScanner, command.taskId).collect {
                    when (it) {
                        is ScanningBoxEvent.FoundBarcode -> {
                            emit(Event.Internal.ReceivedBarcode(it.barcode))
                        }
                        is ScanningBoxEvent.BoxSaved -> {
                            emit(Event.Internal.SavedBox(it.boxId))
                        }
                        is ScanningBoxEvent.BoxAlreadyExists -> {
                            emit(Event.Internal.SavedBox(it.boxId))
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
                return BoxScannerViewModel(
                    application.scanningBoxUseCase
                ) as T
            }
        }
    }
}
