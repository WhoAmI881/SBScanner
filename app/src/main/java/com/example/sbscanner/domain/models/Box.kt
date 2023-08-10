package com.example.sbscanner.domain.models

data class Box(
    val id: Int = 0,
    val barcode: String,
    val timestamp: Long = 0
)

data class FullBox(
    val box: Box,
    val documents: List<FullDocument>
)

data class BoxWithDocuments(
    val box: Box,
    val documents: List<Document>
)
