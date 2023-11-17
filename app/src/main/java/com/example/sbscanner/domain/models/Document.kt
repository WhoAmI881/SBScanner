package com.example.sbscanner.domain.models

import com.example.sbscanner.domain.utils.toDate

data class Document(
    val id: Int = 0,
    val barcode: String = "",
    val title: String = "",
    val date: String = "",
    val note: String = "",
    val isSimpleInventory: Boolean = false,
    val timestamp: Long = 0
) {
    val dateCreate = timestamp.toDate()
}

data class FullDocument(
    val document: Document,
    val images: List<Image>
)
