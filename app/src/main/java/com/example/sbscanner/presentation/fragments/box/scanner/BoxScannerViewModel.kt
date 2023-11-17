package com.example.sbscanner.presentation.fragments.box.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.sbscanner.App
import com.example.sbscanner.domain.usecase.SaveBoxResult
import com.example.sbscanner.domain.usecase.SaveBoxUseCase
import com.example.sbscanner.presentation.fragments.base.BaseViewModel
import com.example.sbscanner.presentation.fragments.base.CameraStateType
import com.example.sbscanner.presentation.fragments.base.FormStateType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BoxScannerViewModel(
    private val saveBoxUseCase: SaveBoxUseCase,
) : BaseViewModel<Event, Effect, Command, State>(State()) {

    override fun reduce(event: Event) {
        when (event) {
            is Event.Ui.Init -> {
                setState(currentState.copy(taskId = event.taskId))
            }

            is Event.Ui.ChangeCameraState -> {
                setState(currentState.copy(cameraStateType = event.cameraStateType))
                if (event.cameraStateType != CameraStateType.OPEN) return

                when (currentState.formStateType) {
                    FormStateType.SCANNING -> {
                        commitEffect(Effect.StartScanning)
                    }

                    FormStateType.BARCODE_FOUND -> {
                    }

                    else -> {}
                }
            }

            is Event.Ui.BarcodeFound -> {
                setState(currentState.copy(formStateType = FormStateType.BARCODE_FOUND))
                commitCommand(Command.TrySaveBox(event.barcode, currentState.taskId))
            }

            is Event.Internal.SavedBox -> {
                commitEffect(Effect.OpenDocumentList(event.boxId))
            }
        }
    }

    override suspend fun execute(command: Command): Flow<Event> {
        return when (command) {
            is Command.TrySaveBox -> flow {
                when (val result = saveBoxUseCase(command.boxBarcode, command.taskId)) {
                    is SaveBoxResult.BoxSaved -> {
                        emit(Event.Internal.SavedBox(result.boxId))
                    }

                    is SaveBoxResult.BoxAlreadyExists -> {
                        emit(Event.Internal.SavedBox(result.boxId))
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
                    application.saveBoxUseCase
                ) as T
            }
        }
    }
}
