package com.example.sbscanner.domain.models

import com.example.sbscanner.domain.utils.toDate

data class Task(
    val id: Int,
    val userId: String,
    val barcode: String,
    val timestamp: Long = 0,
) {
    val dateCreate = timestamp.toDate()
}
