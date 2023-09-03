package com.example.sbscanner.presentation.fragments.dialogs.delete.document

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.sbscanner.App
import com.example.sbscanner.domain.usecase.RemoveDocResult
import com.example.sbscanner.domain.usecase.RemoveDocumentUseCase
import com.example.sbscanner.presentation.fragments.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteDocumentViewModel(
    private val removeDocumentUseCase: RemoveDocumentUseCase
) : BaseViewModel<Event, Effect, Command, State>(State()) {

    override fun reduce(event: Event) {
        when (event) {
            is Event.Ui.Init -> {
                setState(currentState.copy(docId = event.docId))
                commitCommand(Command.RemoveDoc(event.docId))
            }

            is Event.Internal.RemovedImage -> {
                setState(currentState.copy(progress = event.progress))
            }
            is Event.Internal.RemovedDoc -> {
                commitEffect(Effect.CloseDialog)
            }
        }
    }

    override suspend fun execute(command: Command): Flow<Event> {
        return when (command) {
            is Command.RemoveDoc -> flow {
                removeDocumentUseCase(command.docId).collect {
                    when (it) {
                        is RemoveDocResult.Start -> {

                        }
                        is RemoveDocResult.RemovedImage -> {
                            emit(Event.Internal.RemovedImage(it.progress))
                        }
                        is RemoveDocResult.ErrorRemoveImage -> {

                        }
                        is RemoveDocResult.Success -> {
                            emit(Event.Internal.RemovedDoc)
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
                return DeleteDocumentViewModel(
                    application.removeDocumentUseCase,
                ) as T
            }
        }
    }
}
