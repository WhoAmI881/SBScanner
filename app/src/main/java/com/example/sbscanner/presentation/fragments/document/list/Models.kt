package com.example.sbscanner.presentation.fragments.document.list

import com.example.sbscanner.domain.models.Box
import com.example.sbscanner.domain.models.Document
import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.presentation.adapters.base.DelegateItem
import com.example.sbscanner.presentation.adapters.documents.DocumentItem

data class State(
    val boxId: Int = EMPTY_ID,
    val boxTitle: String = "",
    val imgEnable: Boolean = false,
    val delegates: List<DelegateItem> = listOf()
)

sealed class Event {

    sealed class Ui : Event() {
        data class Init(val boxId: Int) : Ui()
        object AddDocClick : Ui()
        data class RemoveDocClick(val pos: Int) : Ui()
        data class EditDocClick(val pos: Int) : Ui()
        object ReturnBack: Ui()

        data class ShowDocumentImagesClick(val pos: Int) : Ui()
        object AddImageClick : Ui()
    }

    sealed class Internal : Event() {
        data class LoadedDocList(val items: List<DocumentItem>) : Internal()
        data class LoadedBox(val box: Box) : Internal()
    }
}

sealed class Effect {
    data class OpenDocumentScanner(val boxId: Int) : Effect()
    data class OpenImageScanner(val boxId: Int) : Effect()
    data class OpenEditDocument(val boxId: Int, val docId: Int) : Effect()
    data class OpenImageList(val docId: Int) : Effect()
    data class OpenDeleteDocDialog(val docId: Int) : Effect()
    object ReturnBack: Effect()
}

sealed class Command {
    data class LoadBox(val boxId: Int) : Command()
    data class LoadDocList(val boxId: Int) : Command()
}
