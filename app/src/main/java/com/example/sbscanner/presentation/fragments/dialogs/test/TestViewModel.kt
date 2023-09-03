package com.example.sbscanner.presentation.fragments.dialogs.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.sbscanner.App
import com.example.sbscanner.domain.usecase.TestResult
import com.example.sbscanner.domain.usecase.TestUseCase
import com.example.sbscanner.presentation.fragments.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TestViewModel(
    private val testUseCase: TestUseCase,
) : BaseViewModel<Event, Effect, Command, State>(State()) {

    override fun reduce(event: Event) {
        when (event) {
            is Event.Ui.Init -> {
                commitCommand(
                    Command.StartFill(
                        currentState.boxCount,
                        currentState.docCount,
                        currentState.imgCount
                    )
                )
            }

            is Event.Internal.ProgressReceived -> {
                setState(currentState.copy(progress = event.progress))
            }
            is Event.Internal.FillFinished -> {
                commitEffect(Effect.CloseDialog)
            }
        }
    }

    override suspend fun execute(command: Command): Flow<Event> {
        return when (command) {
            is Command.StartFill -> flow {
                testUseCase(command.boxCount, command.docCount, command.imgCount).collect {
                    when (it) {
                        is TestResult.Progress -> {
                            emit(Event.Internal.ProgressReceived(it.progress))
                        }
                        is TestResult.Success -> {
                            emit(Event.Internal.FillFinished)
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
                return TestViewModel(
                    application.testUseCase
                ) as T
            }
        }
    }
}
