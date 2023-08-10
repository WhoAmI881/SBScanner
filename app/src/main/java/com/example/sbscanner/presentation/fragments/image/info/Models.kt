package com.example.sbscanner.presentation.fragments.image.info

import com.example.sbscanner.domain.models.Image

data class State(
    val image: Image = Image()
)

sealed class Event {

    sealed class Ui : Event() {
        data class Init(val imgId: Int) : Ui()
        object RemoveClick: Ui()
        object CancelClick: Ui()
    }

    sealed class Internal : Event() {
        data class LoadedImage(val image: Image): Internal()
        object RemovedImage: Internal()
    }
}

sealed class Effect {
    object ReturnBack : Effect()
}

sealed class Command {
    data class LoadImage(val imgId: Int): Command()
    data class RemoveImage(val image: Image): Command()
}
