package com.example.sbscanner.presentation.fragments.image.list

import com.example.sbscanner.domain.models.Document
import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.presentation.adapters.base.DelegateItem
import com.example.sbscanner.presentation.adapters.images.ImageItem

data class State(
    val docId: Int = EMPTY_ID,
    val docBarcode: String = "",
    val delegates: List<DelegateItem> = listOf(),
    val isEmpty: Boolean = false
)

sealed class Event {

    sealed class Ui : Event() {
        data class Init(val docId: Int) : Ui()
        data class ImageItemClick(val pos: Int) : Ui()
        object BackClick: Ui()
    }

    sealed class Internal : Event() {
        data class LoadedDoc(val document: Document): Internal()
        data class LoadedImageList(val items: List<ImageItem>): Internal()
    }
}

sealed class Effect {
    data class OpenImage(val imgId: Int) : Effect()
    object ReturnBack : Effect()
}

sealed class Command {
    data class LoadDoc(val docId: Int) : Command()
    data class LoadImageList(val docId: Int) : Command()
}
