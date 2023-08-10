package com.example.sbscanner.presentation.fragments.box.list

import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.presentation.adapters.base.DelegateItem
import com.example.sbscanner.presentation.adapters.boxes.BoxItem

data class State(
    val taskId: Int = EMPTY_ID,
    val sendEnable: Boolean = false,
    val maxImage: Int = 0,
    val sentImage: Int = 0,
    val delegates: List<DelegateItem> = listOf()
)

sealed class Event {

    sealed class Ui : Event() {
        data class Init(val taskId: Int) : Ui()
        object AddBoxClick : Ui()
        data class BoxItemClick(val pos: Int) : Ui()
        data class DeleteBoxClick(val pos: Int) : Ui()
        object SendTaskClick : Ui()
        object EditTaskClick : Ui()
    }

    sealed class Internal : Event() {
        data class LoadedBoxList(val items: List<BoxItem>) : Internal()
    }
}

sealed class Effect {
    data class OpenDocumentList(val boxId: Int) : Effect()
    data class OpenBoxScanner(val taskId: Int) : Effect()
    data class OpenEditTask(val taskId: Int) : Effect()
    data class OpenDeleteBoxDialog(val boxId: Int) : Effect()
    data class OpenTaskUpload(val taskId: Int) : Effect()
}

sealed class Command {
    data class LoadBoxList(val taskId: Int) : Command()
}
