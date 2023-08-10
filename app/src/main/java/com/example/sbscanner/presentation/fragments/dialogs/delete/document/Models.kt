package com.example.sbscanner.presentation.fragments.dialogs.delete.document

import com.example.sbscanner.domain.utils.EMPTY_ID

data class State(
    val docId: Int = EMPTY_ID,
    val progress: Int = 0,
)

sealed class Event {

    sealed class Ui : Event() {
        data class Init(val docId: Int) : Ui()
    }

    sealed class Internal : Event() {
        object RemovedDoc : Internal()
        data class RemovedImage(val progress: Int) : Internal()
    }
}

sealed class Effect {
    object CloseDialog : Effect()
}

sealed class Command {
    data class RemoveDoc(val docId: Int) : Command()
}
