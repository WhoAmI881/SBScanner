package com.example.sbscanner.domain.models

import com.example.sbscanner.domain.utils.toDate

data class Box(
    val id: Int = 0,
    val barcode: String,
    val timestamp: Long = 0
) {
    val dateCreate = timestamp.toDate()
}

data class FullBox(
    val box: Box,
    val documents: List<FullDocument>
) {
    fun getDocumentsWithoutPictures() = documents.filter { doc ->
        doc.images.isEmpty()
    }
}

data class BoxWithDocuments(
    val box: Box,
    val documents: List<Document>
)
