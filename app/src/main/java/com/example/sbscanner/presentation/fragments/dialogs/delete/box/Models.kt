package com.example.sbscanner.presentation.fragments.dialogs.delete.box

import com.example.sbscanner.domain.utils.EMPTY_ID

data class State(
    val boxId: Int = EMPTY_ID,
    val progress: Int = 0,
)

sealed class Event {

    sealed class Ui : Event() {
        data class Init(val boxId: Int) : Ui()
    }

    sealed class Internal : Event() {
        object RemovedBox : Internal()
        data class RemovedImage(val progress: Int) : Internal()
    }
}

sealed class Effect {
    object CloseDialog : Effect()
}

sealed class Command {
    data class RemoveBox(val boxId: Int) : Command()
}
