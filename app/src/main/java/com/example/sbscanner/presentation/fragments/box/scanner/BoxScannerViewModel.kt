package com.example.sbscanner.presentation.fragments.box.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.sbscanner.App
import com.example.sbscanner.domain.models.Box
import com.example.sbscanner.domain.usecase.SaveBoxUseCase
import com.example.sbscanner.presentation.fragments.base.BaseViewModel
import com.example.sbscanner.presentation.fragments.base.CameraState
import com.example.sbscanner.presentation.fragments.base.FormState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BoxScannerViewModel(
    private val saveBoxUseCase: SaveBoxUseCase
) : BaseViewModel<Event, Effect, Command, State>(State()) {

    override suspend fun reduce(event: Event) {
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
                val barcode = command.cameraScanner.startScanning()
                emit(Event.Internal.ReceivedBarcode(barcode))
                command.cameraScanner.startPreview()
                val boxId = saveBoxUseCase(command.taskId, Box(barcode = barcode))
                emit(Event.Internal.SavedBox(boxId))
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
                    application.saveBoxUseCase,
                ) as T
            }
        }
    }
}
