package com.example.sbscanner.presentation.fragments.task.info

import com.example.sbscanner.domain.models.Task
import com.example.sbscanner.domain.utils.EMPTY_ID

data class State(
    val taskId: Int = EMPTY_ID,
    val userId: String = "",
    val taskBarcode: String = "",
)

sealed class Event {
    sealed class Ui : Event() {
        data class Init(val taskId: Int) : Ui()
        data class BarcodeTaskReceived(val barcode: String) : Ui()
        data class BarcodeUserIdReceived(val barcode: String) : Ui()
        data class ConfirmClick(val taskBarcode: String, val userId: String) : Ui()
        object DeleteTaskClick : Ui()
    }

    sealed class Internal : Event() {
        data class AddedTask(val taskId: Int) : Internal()
        data class UpdatedTask(val taskId: Int) : Internal()
        object ErrorUpdatedTask : Internal()
        data class LoadedTask(val task: Task) : Internal()
    }
}

sealed class Effect {
    data class OpenBoxList(val taskId: Int) : Effect()
    data class OpenTaskDeleteDialog(val taskId: Int) : Effect()
    object ReturnBack : Effect()
    object EmptyData : Effect()
    object ErrorUpdate : Effect()
}

sealed class Command {
    data class LoadTask(val taskId: Int) : Command()
    data class SaveTask(val task: Task) : Command()
}
