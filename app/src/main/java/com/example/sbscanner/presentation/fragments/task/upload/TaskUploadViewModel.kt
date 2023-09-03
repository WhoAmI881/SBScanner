package com.example.sbscanner.presentation.fragments.task.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.sbscanner.App
import com.example.sbscanner.domain.usecase.ErrorCode
import com.example.sbscanner.domain.usecase.GetSessionTaskUseCase
import com.example.sbscanner.domain.usecase.SessionResult
import com.example.sbscanner.presentation.fragments.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TaskUploadViewModel(
    private val getSessionTaskUseCase: GetSessionTaskUseCase,
) : BaseViewModel<Event, Effect, Command, State>(State()) {

    override fun reduce(event: Event) {
        when (event) {
            is Event.Ui.Init -> {
                if (event.serviceIsRunning.not()) {
                    commitCommand(Command.GetSession(event.taskId))
                }
                setState(
                    currentState.copy(
                        taskId = event.taskId,
                        serviceIsRunning = event.serviceIsRunning,
                    )
                )
            }
            is Event.Ui.ReloadClick -> {
                commitCommand(Command.GetSession(currentState.taskId))
                setState(
                    currentState.copy(
                        fragmentState = FragmentState.INIT_UPLOAD
                    )
                )
            }
            is Event.Ui.ErrorSendTask -> {
                setState(
                    currentState.copy(
                        fragmentState = FragmentState.ERROR,
                        errorType = ErrorType.SERVER
                    )
                )
            }
            is Event.Ui.StartSend -> {
                setState(
                    currentState.copy(
                        progress = 0,
                        serviceIsRunning = true,
                    )
                )
            }
            is Event.Ui.ImageSent -> {
                setState(
                    currentState.copy(
                        progress = event.progress,
                        fragmentState = FragmentState.PROGRESS_SENDING
                    ),
                )
            }
            is Event.Ui.LoseConnection -> {
                setState(
                    currentState.copy(
                        fragmentState = FragmentState.LOSE_CONNECTION,
                        errorType = ErrorType.CONNECTION
                    )
                )
            }
            is Event.Ui.SuccessSendTask -> {
                setState(
                    currentState.copy(
                        fragmentState = FragmentState.SUCCESS_SEND,
                    )
                )
            }

            is Event.Internal.LoadedSession -> {
                commitEffect(Effect.StartUploadService(currentState.taskId, event.sessionId))
            }
            is Event.Internal.ErrorLoadedSession -> {
                setState(
                    currentState.copy(
                        fragmentState = FragmentState.ERROR,
                        errorType = ErrorType.SESSION
                    )
                )
                commitEffect(Effect.ShowErrorCode(event.code))
            }
            is Event.Internal.ErrorUserId -> {
                commitEffect(
                    Effect.ShowInitTaskError(
                        currentState.taskId,
                        ErrorType.USER_ID.message
                    )
                )
                setState(
                    currentState.copy(
                        fragmentState = FragmentState.ERROR,
                        errorType = ErrorType.USER_ID
                    )
                )
            }
            is Event.Internal.ErrorTaskBarcode -> {
                commitEffect(
                    Effect.ShowInitTaskError(
                        currentState.taskId,
                        msg = ErrorType.TASK_BARCODE.message
                    )
                )
                setState(
                    currentState.copy(
                        fragmentState = FragmentState.ERROR,
                        errorType = ErrorType.TASK_BARCODE
                    )
                )
            }
            is Event.Internal.ErrorConnection -> {
                setState(
                    currentState.copy(
                        fragmentState = FragmentState.ERROR,
                        errorType = ErrorType.CONNECTION
                    )
                )
            }
        }
    }

    override suspend fun execute(command: Command): Flow<Event> {
        return when (command) {
            is Command.GetSession -> flow {
                when (val result = getSessionTaskUseCase(currentState.taskId)) {
                    is SessionResult.Success -> {
                        emit(Event.Internal.LoadedSession(result.sessionId))
                    }
                    is SessionResult.ErrorRequest -> when (result.error) {
                        ErrorCode.TASK_BARCODE -> {
                            emit(Event.Internal.ErrorTaskBarcode)
                        }
                        ErrorCode.USER_ID -> {
                            emit(Event.Internal.ErrorUserId)
                        }
                        else -> {
                            emit(Event.Internal.ErrorLoadedSession(result.error.code))
                        }
                    }
                    is SessionResult.LostConnection -> {
                        emit(Event.Internal.ErrorConnection)
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
                return TaskUploadViewModel(
                    application.getSessionTaskUseCase,
                ) as T
            }
        }
    }
}
