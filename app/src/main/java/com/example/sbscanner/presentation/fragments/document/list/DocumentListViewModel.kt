package com.example.sbscanner.presentation.fragments.document.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.sbscanner.App
import com.example.sbscanner.domain.usecase.*
import com.example.sbscanner.presentation.adapters.documents.toDelegate
import com.example.sbscanner.presentation.adapters.documents.toUi
import com.example.sbscanner.presentation.adapters.empty.EmptyDelegateItem
import com.example.sbscanner.presentation.adapters.empty.EmptyItem
import com.example.sbscanner.presentation.adapters.empty.toDelegate
import com.example.sbscanner.presentation.fragments.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DocumentListViewModel(
    private val getFullDocumentListUseCase: GetFullDocumentListUseCase,
) : BaseViewModel<Event, Effect, Command, State>(State()) {

    override fun reduce(event: Event) {
        when (event) {
            is Event.Ui.Init -> {
                setState(currentState.copy(boxId = event.boxId))
                commitSubCommand(Command.LoadDocList(event.boxId))
            }
            is Event.Ui.AddDocClick -> {
                commitEffect(Effect.OpenDocumentScanner(currentState.boxId))
            }
            is Event.Ui.RemoveDocClick -> {
                val docId = currentState.delegates[event.pos].id()
                commitEffect(Effect.OpenDeleteDocDialog(docId))
            }
            is Event.Ui.EditDocClick -> {
                val docId = currentState.delegates[event.pos].id()
                commitEffect(Effect.OpenEditDocument(currentState.boxId, docId))
            }
            is Event.Ui.ShowDocumentImagesClick -> {
                val docId = currentState.delegates[event.pos].id()
                commitEffect(Effect.OpenImageList(docId))
            }
            is Event.Ui.AddImageClick -> {
                commitEffect(Effect.OpenImageScanner(currentState.boxId))
            }
            is Event.Ui.ReturnBack -> {
                commitEffect(Effect.ReturnBack)
            }

            is Event.Internal.LoadedDocList -> {
                event.items.ifEmpty {
                    setState(
                        currentState.copy(
                            delegates = listOf(EmptyItem().toDelegate()),
                            imgEnable = false
                        )
                    )
                    return
                }
                setState(
                    currentState.copy(
                        delegates = event.items.map { it.toDelegate() },
                        imgEnable = true
                    )
                )
            }
        }
    }

    override suspend fun execute(command: Command): Flow<Event> {
        return when (command) {
            is Command.LoadDocList -> flow {
                getFullDocumentListUseCase(command.boxId).collect {
                    val items = it.map { model -> model.toUi() }
                    emit(Event.Internal.LoadedDocList(items))
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
                return DocumentListViewModel(
                    application.getFullDocumentListUseCase,
                ) as T
            }
        }
    }
}
