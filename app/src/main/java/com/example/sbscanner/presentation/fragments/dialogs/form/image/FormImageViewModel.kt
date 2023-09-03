package com.example.sbscanner.presentation.fragments.dialogs.form.image

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.sbscanner.App
import com.example.sbscanner.domain.models.Image
import com.example.sbscanner.domain.usecase.GetImageUseCase
import com.example.sbscanner.domain.usecase.RemoveImageUseCase
import com.example.sbscanner.domain.usecase.SaveImageUseCase
import com.example.sbscanner.presentation.fragments.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FormImageViewModel(
    private val getImageUseCase: GetImageUseCase,
    private val saveImageUseCase: SaveImageUseCase,
    private val removeImageUseCase: RemoveImageUseCase,
) : BaseViewModel<Event, Effect, Command, State>(State()) {

    override fun reduce(event: Event) {
        when (event) {
            is Event.Ui.InitEdit -> {
                setState(currentState.copy(imgId = event.imgId))
                commitCommand(Command.LoadImage(event.imgId))
            }
            is Event.Ui.InitAdd -> {
                setState(currentState.copy(docId = event.docId, imgPath = event.imgPath))
            }
            is Event.Ui.CancelClick -> {}
            is Event.Ui.RemoveClick -> {
                val image = Image(id = currentState.imgId, path = currentState.imgPath)
                commitCommand(Command.RemoveImage(image))
            }
            is Event.Ui.SaveClick -> {
                val image = Image(path = currentState.imgPath)
                commitCommand(Command.SaveImage(currentState.docId, image))
            }

            is Event.Internal.LoadedImage -> {
                setState(currentState.copy(imgPath = event.image.path))
            }
            is Event.Internal.RemovedImage -> {
                commitEffect(Effect.CloseDeleted)
            }
            is Event.Internal.SavedImage -> {
                commitEffect(Effect.CloseSaved(event.imgId))
            }
        }
    }

    override suspend fun execute(command: Command): Flow<Event> {
        return when (command) {
            is Command.LoadImage -> flow {
                getImageUseCase(command.imgId)?.let {
                    emit(Event.Internal.LoadedImage(it))
                }
            }
            is Command.RemoveImage -> flow {
                removeImageUseCase(command.image)
                emit(Event.Internal.RemovedImage)
            }
            is Command.SaveImage -> flow {
                val imgId = saveImageUseCase(command.docId, command.image)
                emit(Event.Internal.SavedImage(imgId))
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
                return FormImageViewModel(
                    application.getImageUseCase,
                    application.saveImageUseCase,
                    application.removeImageUseCase,
                ) as T
            }
        }
    }
}
