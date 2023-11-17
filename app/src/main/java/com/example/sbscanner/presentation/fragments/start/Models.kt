package com.example.sbscanner.presentation.fragments.start

data class State(
    val uploadServiceIsRunning: Boolean = false,
    val allPermissionsGranted: Boolean = false
)

sealed class Event {

    sealed class Ui : Event() {
        data class Init(
            val allPermissionsGranted: Boolean,
            val uploadServiceIsRunning: Boolean
        ) : Ui()
        data class OnPermissionsChange(val allPermissionsGranted: Boolean) : Ui()
    }

    sealed class Internal : Event() {
        object TaskNotCreated: Internal()
        data class LoadedTaskId(val taskId: Int): Internal()
    }
}

sealed class Effect {
    object RequestPermissions : Effect()
    object AddTaskOpen: Effect()
    data class BoxListOpen(val taskId: Int): Effect()
    data class ProgressSendingTaskOpen(val taskId: Int): Effect()
}

sealed class Command {
    object LoadTask: Command()
}
