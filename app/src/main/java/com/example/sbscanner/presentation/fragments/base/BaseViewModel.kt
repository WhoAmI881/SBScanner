package com.example.sbscanner.presentation.fragments.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private data class JobData(
    val job: Job,
    val isSubscribedJob: Boolean
) {
    fun cancel() {
        if (job.isActive) {
            job.cancel()
        }
    }
}

private fun Job.toJobData(isSubscribedJob: Boolean = false): JobData {
    return JobData(this, isSubscribedJob)
}

abstract class BaseViewModel<Event : Any, Effect : Any, Command : Any, State : Any>(
    initState: State,
) : ViewModel() {

    private val jobs: MutableList<JobData> = mutableListOf()

    private var initEvent: Event? = null

    private val events: MutableSharedFlow<Event> = MutableSharedFlow()

    private val commands: MutableSharedFlow<Event> = MutableSharedFlow()

    private val _state: MutableStateFlow<State> = MutableStateFlow(initState)

    private val _effects: MutableSharedFlow<Effect> = MutableSharedFlow()

    private val commandSub: MutableList<Command> = mutableListOf()

    val state: StateFlow<State> = _state.asStateFlow()

    val effects: SharedFlow<Effect> = _effects.asSharedFlow()

    val currentState: State
        get() = _state.value

    init {
        viewModelScope.launch {
            events.collect(::reduce)
        }
    }

    override fun onCleared() {
        super.onCleared()
        jobs.forEach { it.cancel() }
        commandSub.clear()
    }

    abstract fun reduce(event: Event)

    abstract suspend fun execute(command: Command): Flow<Event>

    fun unsubscribeFromCommands() {
        jobs.forEach {
            if (it.isSubscribedJob) {
                it.cancel()
            }
        }
    }

    fun subscribeToCommands() {
        commandSub.forEach { executeCommand(it, true) }
    }

    fun setState(state: State) {
        _state.value = state
    }

    fun commitEvent(event: Event) {
        Log.i("EVENT", event.toString())
        reduce(event)
        /*
            viewModelScope.launch {
                Log.i("EVENT", event.toString())
                events.emit(event)
        }
         */
    }

    fun commitEffect(effect: Effect) {
        Log.i("EFFECT", effect.toString())
        viewModelScope.launch {
            _effects.emit(effect)
        }
    }

    fun commitCommand(command: Command) {
        executeCommand(command)
    }

    fun commitSubCommand(command: Command) {
        if (commandSub.contains(command).not()) {
            commandSub.add(command)
        }
    }

    private fun executeCommand(command: Command, isSubscribed: Boolean = false) {
        Log.i("COMMAND", command.toString())
        jobs += viewModelScope.launch(Dispatchers.IO) {
            try {
                execute(command).collect { commitEvent(it) }
            } catch (t: CancellationException) {
                throw t
            } catch (t: Throwable) {
                Log.e("ERROR_COMMAND", t.toString())
            }
        }.toJobData(isSubscribed)
    }

    fun setInitEvent(event: Event) {
        if (initEvent == null) {
            initEvent = event
            viewModelScope.launch {
                events.emit(event)
            }
        }
    }
}
