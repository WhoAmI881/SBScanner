package com.example.sbscanner.presentation.adapters.tasks

import com.example.sbscanner.domain.models.Task

data class TaskItem(
    val id: Int,
    val userId: String,
    val barcode: String
)

fun TaskItem.toDomain() = Task(
    id = id,
    userId = userId,
    barcode = barcode
)
