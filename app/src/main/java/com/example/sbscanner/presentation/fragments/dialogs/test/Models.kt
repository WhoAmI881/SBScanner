package com.example.sbscanner.presentation.fragments.dialogs.test

data class State(
    val progress: Int = 0,
    val boxCount: Int = 10,
    val docCount: Int = 50,
    val imgCount: Int = 1,
) {
    val fullImg = boxCount * docCount * imgCount
}

sealed class Event {

    sealed class Ui : Event() {
        object Init : Ui()
    }

    sealed class Internal : Event() {
        data class ProgressReceived(val progress: Int) : Internal()
        object FillFinished : Internal()
    }
}

sealed class Effect {
    object CloseDialog : Effect()
}

sealed class Command {
    data class StartFill(val boxCount: Int, val docCount: Int, val imgCount: Int) : Command()
}
