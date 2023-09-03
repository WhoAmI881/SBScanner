package com.example.sbscanner.presentation.fragments.box.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.sbscanner.App
import com.example.sbscanner.domain.usecase.*
import com.example.sbscanner.presentation.adapters.boxes.BoxState
import com.example.sbscanner.presentation.adapters.boxes.toDelegate
import com.example.sbscanner.presentation.adapters.boxes.toUi
import com.example.sbscanner.presentation.adapters.empty.EmptyItem
import com.example.sbscanner.presentation.adapters.empty.toDelegate
import com.example.sbscanner.presentation.fragments.base.BaseViewModel
import kotlinx.coroutines.flow.*

class BoxListViewModel(
    private val getFullBoxListUseCase: GetFullBoxListUseCase,
) : BaseViewModel<Event, Effect, Command, State>(State()) {

    override fun reduce(event: Event) {
        when (event) {
            is Event.Ui.Init -> {
                setState(currentState.copy(taskId = event.taskId))
                commitSubCommand(Command.LoadBoxList(event.taskId))
            }
            is Event.Ui.AddBoxClick -> {
                commitEffect(Effect.OpenBoxScanner(currentState.taskId))
            }
            is Event.Ui.BoxItemClick -> {
                val boxId = currentState.delegates[event.pos].id()
                commitEffect(Effect.OpenDocumentList(boxId))
            }
            is Event.Ui.DeleteBoxClick -> {
                val boxId = currentState.delegates[event.pos].id()
                commitEffect(Effect.OpenDeleteBoxDialog(boxId))
            }
            is Event.Ui.SendTaskClick -> {
                commitEffect(Effect.OpenTaskUpload(currentState.taskId))
            }
            is Event.Ui.EditTaskClick -> {
                commitEffect(Effect.OpenEditTask(currentState.taskId))
            }

            is Event.Internal.LoadedBoxList -> {
                event.items.ifEmpty { setState(
                        currentState.copy(
                            delegates = listOf(EmptyItem().toDelegate()),
                            sendEnable = false,
                            maxImage = 0,
                            sentImage = 0
                        )
                    )
                    return
                }

                setState(
                    currentState.copy(
                        delegates = event.items.map { it.toDelegate() },
                        sendEnable = event.items.any { it.state == BoxState.FULL },
                        maxImage = event.items.sumOf { it.imgCount },
                        sentImage = event.items.sumOf { it.imgSent },
                    )
                )
            }
        }
    }

    override suspend fun execute(command: Command): Flow<Event> {
        return when (command) {
            is Command.LoadBoxList -> flow {
                getFullBoxListUseCase(command.taskId).collect {
                    emit(Event.Internal.LoadedBoxList(it.map { box -> box.toUi() }))
                }
            }
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>, extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as App
                return BoxListViewModel(
                    application.getFullBoxListUseCase,
                ) as T
            }
        }
    }
}
