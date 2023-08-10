package com.example.sbscanner.presentation.fragments.dialogs.delete.task

import com.example.sbscanner.domain.utils.EMPTY_ID

data class State(
    val taskId: Int = EMPTY_ID,
    val progress: Int = 0,
)

sealed class Event {

    sealed class Ui : Event() {
        data class Init(val taskId: Int) : Ui()
    }

    sealed class Internal : Event() {
        object RemovedTask : Internal()
        data class RemovedImage(val progress: Int): Internal()
    }
}

sealed class Effect {
    object CloseDialog : Effect()
    object OpenAddTask: Effect()
}

sealed class Command {
    data class RemoveTask(val taskId: Int) : Command()
}
