package com.example.sbscanner.presentation.fragments.dialogs.form.image

import com.example.sbscanner.domain.models.Image
import com.example.sbscanner.domain.utils.EMPTY_ID

data class State(
    val docId: Int = EMPTY_ID,
    val imgId: Int = EMPTY_ID,
    val imgPath: String = "",
)

sealed class Event {

    sealed class Ui : Event() {
        data class InitAdd(val docId: Int, val imgPath: String) : Ui()
        data class InitEdit(val imgId: Int) : Ui()
        object RemoveClick : Ui()
        object SaveClick : Ui()
        object CancelClick : Ui()
    }

    sealed class Internal : Event() {
        data class LoadedImage(val image: Image) : Internal()
        object RemovedImage : Internal()
        data class SavedImage(val imgId: Int) : Internal()
    }
}

sealed class Effect {
    data class CloseSaved(val imgId: Int) : Effect()
    object CloseDeleted : Effect()
}

sealed class Command {
    data class LoadImage(val imgId: Int) : Command()
    data class RemoveImage(val imgId: Int) : Command()
    data class SaveImage(val docId: Int, val imgPath: String) : Command()
}
