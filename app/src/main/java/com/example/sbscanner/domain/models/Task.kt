package com.example.sbscanner.domain.models

data class Task(
    val id: Int,
    val userId: String,
    val barcode: String,
    val timestamp: Long = 0,
)

data class FullTask(
    val task: Task,
    val boxes: List<FullBox>
)
