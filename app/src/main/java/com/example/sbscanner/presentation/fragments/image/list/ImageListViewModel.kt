package com.example.sbscanner.presentation.fragments.image.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.sbscanner.App
import com.example.sbscanner.domain.usecase.GetDocumentUseCase
import com.example.sbscanner.domain.usecase.GetImageListUseCase
import com.example.sbscanner.presentation.adapters.images.toDelegate
import com.example.sbscanner.presentation.adapters.images.toUi
import com.example.sbscanner.presentation.fragments.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ImageListViewModel(
    private val getDocumentUseCase: GetDocumentUseCase,
    private val getImageListUseCase: GetImageListUseCase,
) : BaseViewModel<Event, Effect, Command, State>(State()) {

    override fun reduce(event: Event) {
        when (event) {
            is Event.Ui.Init -> {
                setState(currentState.copy(docId = event.docId))
                commitCommand(Command.LoadDoc(event.docId))
                commitCommand(Command.LoadImageList(event.docId))
            }
            is Event.Ui.ImageItemClick -> {
                val imgId = currentState.delegates[event.pos].id()
                commitEffect(Effect.OpenImage(imgId))
            }
            is Event.Ui.BackClick -> {
                commitEffect(Effect.ReturnBack)
            }

            is Event.Internal.LoadedDoc -> {
                setState(currentState.copy(docBarcode = event.document.barcode))
            }
            is Event.Internal.LoadedImageList -> {
                setState(
                    currentState.copy(
                        delegates = event.items.map { it.toDelegate() },
                        isEmpty = event.items.isEmpty()
                    )
                )
            }
        }
    }

    override suspend fun execute(command: Command): Flow<Event> {
        return when (command) {
            is Command.LoadImageList -> flow {
                getImageListUseCase(command.docId).collect {
                    val items = it.map { img -> img.toUi() }
                    emit(Event.Internal.LoadedImageList(items))
                }
            }
            is Command.LoadDoc -> flow {
                getDocumentUseCase(command.docId)?.let {
                    emit(Event.Internal.LoadedDoc(it))
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
                return ImageListViewModel(
                    application.getDocumentUseCase,
                    application.getImageListUseCase,
                ) as T
            }
        }
    }
}
