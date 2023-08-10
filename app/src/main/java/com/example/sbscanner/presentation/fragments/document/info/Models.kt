package com.example.sbscanner.presentation.fragments.document.info

import com.example.sbscanner.domain.models.Document
import com.example.sbscanner.domain.utils.EMPTY_ID

data class State(
    val boxId: Int = EMPTY_ID,
    val formData: FormData = FormData(),
)

sealed class Event {

    sealed class Ui : Event() {
        data class InitAdd(val boxId: Int, val docBarcode: String) : Ui()
        data class InitEdit(val boxId: Int, val docId: Int) : Ui()
        data class SaveDocClick(val formData: FormData) : Ui()
        object CancelClick : Ui()
    }

    sealed class Internal : Event() {
        data class LoadedDoc(val document: Document) : Internal()
        object SavedDoc : Internal()
    }
}

sealed class Effect {
    object ReturnBack : Effect()
}

sealed class Command {
    data class LoadDoc(val docId: Int) : Command()
    data class SaveDoc(val boxId: Int, val document: Document) : Command()
}
