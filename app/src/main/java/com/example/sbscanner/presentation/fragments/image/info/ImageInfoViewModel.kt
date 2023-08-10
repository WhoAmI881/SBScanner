package com.example.sbscanner.presentation.fragments.image.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.sbscanner.App
import com.example.sbscanner.domain.usecase.GetImageUseCase
import com.example.sbscanner.domain.usecase.RemoveImageUseCase
import com.example.sbscanner.presentation.fragments.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ImageInfoViewModel(
    private val getImageUseCase: GetImageUseCase,
    private val removeImageUseCase: RemoveImageUseCase,
) : BaseViewModel<Event, Effect, Command, State>(State()) {

    override suspend fun reduce(event: Event) {
        when (event) {
            is Event.Ui.Init -> {
                commitCommand(Command.LoadImage(event.imgId))
            }
            is Event.Ui.RemoveClick -> {
                commitCommand(Command.RemoveImage(currentState.image))
            }
            is Event.Ui.CancelClick -> {
                commitEffect(Effect.ReturnBack)
            }

            is Event.Internal.LoadedImage -> {
                setState(currentState.copy(image = event.image))
            }
            is Event.Internal.RemovedImage -> {
                commitEffect(Effect.ReturnBack)
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
                return ImageInfoViewModel(
                    application.getImageUseCase,
                    application.removeImageUseCase,
                ) as T
            }
        }
    }
}
