package com.example.sbscanner.presentation.fragments.task.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.sbscanner.App
import com.example.sbscanner.domain.models.Task
import com.example.sbscanner.domain.usecase.GetTaskUseCase
import com.example.sbscanner.domain.usecase.SaveTaskResult
import com.example.sbscanner.domain.usecase.SaveTaskUseCase
import com.example.sbscanner.domain.utils.isNotEmptyId
import com.example.sbscanner.presentation.fragments.base.BaseViewModel
import kotlinx.coroutines.flow.*

class TaskInfoViewModel(
    private val saveTaskUseCase: SaveTaskUseCase,
    private val getTaskUseCase: GetTaskUseCase,
) : BaseViewModel<Event, Effect, Command, State>(State()) {

    override fun reduce(event: Event) {
        when (event) {
            is Event.Ui.Init -> if (event.taskId.isNotEmptyId()) {
                setState(currentState.copy(taskId = event.taskId))
                commitCommand(Command.LoadTask(event.taskId))
            }
            is Event.Ui.BarcodeTaskReceived -> {
                setState(currentState.copy(taskBarcode = event.barcode))
            }
            is Event.Ui.BarcodeUserIdReceived -> {
                setState(currentState.copy(userId = event.barcode))
            }
            is Event.Ui.InputUserId -> {
                setState(currentState.copy(userId = event.value))
            }
            is Event.Ui.ConfirmClick -> with(event) {
                if (userId.isBlank() || taskBarcode.isBlank()) {
                    commitEffect(Effect.EmptyData)
                    return
                }
                val task = Task(
                    id = currentState.taskId,
                    userId = userId,
                    barcode = taskBarcode,
                )
                commitCommand(Command.SaveTask(task))
            }
            is Event.Ui.DeleteTaskClick -> {
                commitEffect(Effect.OpenTaskDeleteDialog(currentState.taskId))
            }

            is Event.Internal.LoadedTask -> with(event.task) {
                setState(currentState.copy(userId = userId, taskBarcode = barcode))
            }
            is Event.Internal.AddedTask -> {
                commitEffect(Effect.OpenBoxList(event.taskId))
            }
            is Event.Internal.UpdatedTask -> {
                commitEffect(Effect.ReturnBack)
            }
            is Event.Internal.ErrorUpdatedTask -> {
                commitEffect(Effect.ErrorUpdate)
            }
        }
    }

    override suspend fun execute(command: Command): Flow<Event> = when (command) {
        is Command.LoadTask -> flow {
            getTaskUseCase(command.taskId).collect {
                emit(Event.Internal.LoadedTask(it))
            }
        }
        is Command.SaveTask -> flow {
            when (val result = saveTaskUseCase(command.task)) {
                is SaveTaskResult.TaskAdded -> {
                    emit(Event.Internal.AddedTask(result.taskId))
                }
                is SaveTaskResult.TaskUpdated -> {
                    emit(Event.Internal.UpdatedTask(result.taskId))
                }
                is SaveTaskResult.TaskAlreadyExists -> {
                    emit(Event.Internal.ErrorUpdatedTask)
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

                return TaskInfoViewModel(
                    application.saveTaskUseCase,
                    application.getTaskUseCase,
                ) as T
            }
        }
    }
}
