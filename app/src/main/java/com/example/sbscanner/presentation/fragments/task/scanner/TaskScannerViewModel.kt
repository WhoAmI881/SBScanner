package com.example.sbscanner.presentation.fragments.task.scanner

import com.example.sbscanner.presentation.fragments.base.BaseViewModel
import com.example.sbscanner.presentation.fragments.base.CameraState
import com.example.sbscanner.presentation.fragments.base.FormState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TaskScannerViewModel : BaseViewModel<Event, Effect, Command, State>(State()) {

    override suspend fun reduce(event: Event) = when (event) {
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
                val barcode = command.cameraScanner.startScanning()
                emit(Event.Internal.ReceivedBarcode(barcode))
                command.cameraScanner.startPreview()
            }
        }
    }
}
