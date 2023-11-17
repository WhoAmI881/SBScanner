package com.example.sbscanner.presentation.fragments.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.sbscanner.App
import com.example.sbscanner.domain.usecase.GetTaskListUseCase
import com.example.sbscanner.presentation.fragments.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StartViewModel(
    private val getTaskListUseCase: GetTaskListUseCase
) : BaseViewModel<Event, Effect, Command, State>(State()) {

    override fun reduce(event: Event) {
        when (event) {
            is Event.Ui.Init -> {
                setState(
                    currentState.copy(
                        uploadServiceIsRunning = event.uploadServiceIsRunning,
                        allPermissionsGranted = event.allPermissionsGranted
                    )
                )
                if (event.allPermissionsGranted) {
                    commitCommand(Command.LoadTask)
                    return
                }
                commitEffect(Effect.RequestPermissions)
            }

            is Event.Ui.OnPermissionsChange -> {
                setState(currentState.copy(allPermissionsGranted = event.allPermissionsGranted))
                if (event.allPermissionsGranted) {
                    commitCommand(Command.LoadTask)
                }
            }

            is Event.Internal.TaskNotCreated -> {
                commitEffect(Effect.AddTaskOpen)
            }

            is Event.Internal.LoadedTaskId -> if (currentState.uploadServiceIsRunning) {
                commitEffect(Effect.ProgressSendingTaskOpen(event.taskId))
            } else {
                commitEffect(Effect.BoxListOpen(event.taskId))
            }
        }
    }

    override suspend fun execute(command: Command): Flow<Event> {
        return when (command) {
            is Command.LoadTask -> flow {
                val task = getTaskListUseCase().ifEmpty {
                    emit(Event.Internal.TaskNotCreated)
                    return@flow
                }.last()
                emit(Event.Internal.LoadedTaskId(task.id))
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
                return StartViewModel(
                    application.getTaskListUseCase
                ) as T
            }
        }
    }
}
