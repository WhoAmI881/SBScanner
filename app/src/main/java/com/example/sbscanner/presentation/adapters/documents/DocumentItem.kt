package com.example.sbscanner.presentation.adapters.documents

import com.example.sbscanner.domain.models.FullDocument
import com.example.sbscanner.domain.utils.EMPTY_ID

enum class DocState {
    FULL, NOT_FULL, SENT
}

data class DocumentItem(
    val id: Int = EMPTY_ID,
    val barcode: String = "",
    val title: String = "",
    val date: String = "",
    val isSimpleInventory: Boolean = false,
    val imageCount: Int = 0,
    val state: DocState
)

fun FullDocument.toUi(): DocumentItem {

    val state = when{
        images.isEmpty() -> DocState.NOT_FULL
        images.all { it.isSending } -> DocState.SENT
        else -> DocState.FULL
    }

    return DocumentItem(
        id = document.id,
        barcode = document.barcode,
        title = document.title,
        date = document.date,
        isSimpleInventory = document.isSimpleInventory,
        imageCount = images.size,
        state = state
    )
}

fun DocumentItem.toDelegate() = DocumentDelegateItem(this)