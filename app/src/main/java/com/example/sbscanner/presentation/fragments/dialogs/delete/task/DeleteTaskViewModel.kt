package com.example.sbscanner.presentation.fragments.dialogs.delete.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.sbscanner.App
import com.example.sbscanner.domain.usecase.RemoveTaskResult
import com.example.sbscanner.domain.usecase.RemoveTaskUseCase
import com.example.sbscanner.presentation.fragments.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteTaskViewModel(
    private val removeTaskUseCase: RemoveTaskUseCase
) : BaseViewModel<Event, Effect, Command, State>(State()) {

    override  fun reduce(event: Event) {
        when (event) {
            is Event.Ui.Init -> {
                setState(currentState.copy(taskId = event.taskId))
                commitCommand(Command.RemoveTask(event.taskId))
            }

            is Event.Internal.RemovedImage -> {
                setState(currentState.copy(progress = event.progress))
            }
            is Event.Internal.RemovedTask -> {
                commitEffect(Effect.OpenAddTask)
            }
        }
    }

    override suspend fun execute(command: Command): Flow<Event> {
        return when (command) {
            is Command.RemoveTask -> flow {
                removeTaskUseCase(command.taskId).collect {
                    when (it) {
                        is RemoveTaskResult.Start -> {

                        }
                        is RemoveTaskResult.RemovedImage -> {
                            emit(Event.Internal.RemovedImage(it.progress))
                        }
                        is RemoveTaskResult.ErrorRemoveImage -> {

                        }
                        is RemoveTaskResult.Success -> {
                            emit(Event.Internal.RemovedTask)
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
                return DeleteTaskViewModel(
                    application.removeTaskUseCase,
                ) as T
            }
        }
    }
}