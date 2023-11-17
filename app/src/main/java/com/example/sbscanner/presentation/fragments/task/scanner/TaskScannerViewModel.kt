package com.example.sbscanner.presentation.fragments.task.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.sbscanner.presentation.fragments.base.BaseViewModel
import com.example.sbscanner.presentation.fragments.base.CameraStateType
import com.example.sbscanner.presentation.fragments.base.FormStateType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TaskScannerViewModel : BaseViewModel<Event, Effect, Command, State>(State()) {

    override fun reduce(event: Event) {
        when (event) {
            is Event.Ui.Init -> {}

            is Event.Ui.ChangeCameraState -> {
                setState(currentState.copy(cameraStateType = event.cameraStateType))
                if (event.cameraStateType != CameraStateType.OPEN) return

                when (currentState.formStateType) {
                    FormStateType.SCANNING -> {
                        commitEffect(Effect.StartScanning)
                    }

                    FormStateType.BARCODE_FOUND -> {
                        commitEffect(Effect.StartScanning)
                    }

                    else -> {}
                }
            }
        }
    }

    override suspend fun execute(command: Command): Flow<Event> {
        return flow { }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>, extras: CreationExtras
            ): T {
                return TaskScannerViewModel() as T
            }
        }
    }
}
