package com.example.sbscanner.presentation.fragments.dialogs.delete.box

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.sbscanner.App
import com.example.sbscanner.domain.usecase.RemoveBoxResult
import com.example.sbscanner.domain.usecase.RemoveBoxUseCase
import com.example.sbscanner.presentation.fragments.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteBoxViewModel(
    private val removeBoxUseCase: RemoveBoxUseCase,
) : BaseViewModel<Event, Effect, Command, State>(State()) {

    override suspend fun reduce(event: Event) {
        when (event) {
            is Event.Ui.Init -> {
                setState(currentState.copy(boxId = event.boxId))
                commitCommand(Command.RemoveBox(event.boxId))
            }

            is Event.Internal.RemovedImage -> {
                setState(currentState.copy(progress = event.progress))
            }
            is Event.Internal.RemovedBox -> {
                commitEffect(Effect.CloseDialog)
            }
        }
    }

    override suspend fun execute(command: Command): Flow<Event> {
        return when (command) {
            is Command.RemoveBox -> flow {
                removeBoxUseCase(command.boxId).collect {
                    when (it) {
                        is RemoveBoxResult.Start -> {

                        }
                        is RemoveBoxResult.RemovedImage -> {
                            emit(Event.Internal.RemovedImage(it.progress))
                        }
                        is RemoveBoxResult.ErrorRemoveImage -> {

                        }
                        is RemoveBoxResult.Success -> {
                            emit(Event.Internal.RemovedBox)
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
                return DeleteBoxViewModel(
                    application.removeBoxUseCase,
                ) as T
            }
        }
    }
}
