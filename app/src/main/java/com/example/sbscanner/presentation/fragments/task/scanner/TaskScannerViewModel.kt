package com.example.sbscanner.presentation.fragments.task.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.sbscanner.App
import com.example.sbscanner.domain.usecase.ScanningTaskUseCase
import com.example.sbscanner.presentation.fragments.base.BaseViewModel
import com.example.sbscanner.presentation.fragments.base.CameraState
import com.example.sbscanner.presentation.fragments.base.FormState
import com.example.sbscanner.presentation.fragments.task.info.TaskInfoViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TaskScannerViewModel(
    private val scanningTaskUseCase: ScanningTaskUseCase
) : BaseViewModel<Event, Effect, Command, State>(State()) {

    override fun reduce(event: Event) = when (event) {
        is Event.Ui.Init -> {}
        is Event.Ui.ChangeCameraState -> {
            setState(currentState.copy(cameraState = event.cameraState))
        }
        is Event.Ui.CameraInit -> with(event.cameraScanner) {
            when (currentState.formState) {
                FormState.SCANNING -> {
                    commitCommand(Command.StartScanning(this))
                }
                FormState.BARCODE_FOUND -> {
                    commitCommand(Command.StartScanning(this))
                }
                else -> {}
            }
            setState(currentState.copy(cameraState = CameraState.SUCCESS))
        }

        is Event.Internal.ReceivedBarcode -> {
            setState(
                currentState.copy(
                    barcode = event.barcode,
                    formState = FormState.BARCODE_FOUND
                )
            )
            commitEffect(Effect.ReturnBack(event.barcode))
        }
    }

    override suspend fun execute(command: Command): Flow<Event> {
        return when (command) {
            is Command.StartScanning -> flow {
                val barcode = scanningTaskUseCase(command.cameraScanner)
                emit(Event.Internal.ReceivedBarcode(barcode))
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

                return TaskScannerViewModel(
                    application.scanningTaskUseCase,
                ) as T
            }
        }
    }
}
