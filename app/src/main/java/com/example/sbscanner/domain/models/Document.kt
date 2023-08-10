package com.example.sbscanner.domain.models

data class Document(
    val id: Int = 0,
    val barcode: String = "",
    val title: String = "",
    val date: String = "",
    val note: String = "",
    val isSimpleInventory: Boolean = false,
    val timestamp: Long = 0
)

data class FullDocument(
    val document: Document,
    val images: List<Image>
)
