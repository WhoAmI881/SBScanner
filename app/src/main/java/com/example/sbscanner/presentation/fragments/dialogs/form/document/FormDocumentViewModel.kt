package com.example.sbscanner.presentation.fragments.dialogs.form.document

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.sbscanner.App
import com.example.sbscanner.domain.usecase.GetDocumentUseCase
import com.example.sbscanner.domain.usecase.SaveDocumentUseCase
import com.example.sbscanner.presentation.fragments.base.BaseViewModel
import com.example.sbscanner.presentation.fragments.document.info.FormData
import com.example.sbscanner.presentation.fragments.document.info.toDomain
import com.example.sbscanner.presentation.fragments.document.info.toFormData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FormDocumentViewModel(
    private val getDocumentUseCase: GetDocumentUseCase,
    private val saveDocumentUseCase: SaveDocumentUseCase
) : BaseViewModel<Event, Effect, Command, State>(State()) {

    override suspend fun reduce(event: Event) {
        when (event) {
            is Event.Ui.InitAdd -> {
                val form = FormData(barcode = event.docBarcode)
                setState(currentState.copy(boxId = event.boxId, formData = form))
            }
            is Event.Ui.InitEdit -> {
                val form = FormData(docId = event.docId)
                setState(currentState.copy(boxId = event.boxId, formData = form))
                commitCommand(Command.LoadDoc(event.docId))
            }
            is Event.Ui.SaveDocClick -> {
                val document = event.formData.copy(docId = currentState.formData.docId).toDomain()
                commitCommand(Command.SaveDoc(currentState.boxId, document))
            }

            is Event.Internal.LoadedDoc -> {
                setState(currentState.copy(formData = event.document.toFormData()))
            }
            is Event.Internal.SavedDoc -> {
                commitEffect(Effect.CloseSaved(event.docId))
            }
        }
    }

    override suspend fun execute(command: Command): Flow<Event> {
        return when (command) {
            is Command.LoadDoc -> flow {
                getDocumentUseCase(command.docId)?.let {
                    emit(Event.Internal.LoadedDoc(it))
                }
            }
            is Command.SaveDoc -> flow {
                val docId = saveDocumentUseCase(command.boxId, command.document)
                emit(Event.Internal.SavedDoc(docId))
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
                return FormDocumentViewModel(
                    application.getDocumentUseCase,
                    application.saveDocumentUseCase,
                ) as T
            }
        }
    }
}
