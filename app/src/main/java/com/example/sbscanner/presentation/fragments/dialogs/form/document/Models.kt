package com.example.sbscanner.presentation.fragments.dialogs.form.document

import com.example.sbscanner.domain.models.Document
import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.presentation.fragments.document.info.FormData

data class State(
    val boxId: Int = EMPTY_ID,
    val formData: FormData = FormData(),
)

sealed class Event {

    sealed class Ui : Event() {
        data class InitAdd(val boxId: Int, val docBarcode: String) : Ui()
        data class InitEdit(val boxId: Int, val docId: Int) : Ui()
        data class SaveDocClick(val formData: FormData) : Ui()
        data class ChangeForm(val formData: FormData): Ui()
    }

    sealed class Internal : Event() {
        data class LoadedDoc(val document: Document) : Internal()
        data class SavedDoc(val docId: Int) : Internal()
    }
}

sealed class Effect {
    data class CloseSaved(val docId: Int) : Effect()
}

sealed class Command {
    data class LoadDoc(val docId: Int) : Command()
    data class SaveDoc(val boxId: Int, val document: Document) : Command()
}
